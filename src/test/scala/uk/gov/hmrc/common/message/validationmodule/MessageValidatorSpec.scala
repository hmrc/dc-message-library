/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.common.message.validationmodule

import junit.framework.TestCase
import org.bson.types.ObjectId

import java.time.LocalDate
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.common.message.model.*
import uk.gov.hmrc.common.message.util.TestDataSample.{ EMPTY_STRING, TEST_BATCH_ID, TEST_DETAILS_ID, TEST_EMAIL, TEST_EXTERNAL_REF, TEST_FORM_TYPE, TEST_HASH, TEST_LOCAL_DATE, TEST_MAIL_SUBJECT, TEST_RENDER_URL, TEST_SAUTR, TEST_SOURCE_DATA, TEST_SOURCE_GMC, TEST_TEMPLATE_ID, TEST_THREAD_ID }
import uk.gov.hmrc.domain.{ HmrcMtdVat, SaUtr, SimpleName, TaxIdentifier }

import scala.util.{ Failure, Success }

class MessageValidatorSpec extends AnyWordSpecLike with MockitoSugar with Matchers with ScalaFutures {

  "isGmc" must {

    val message = Message(
      id = new ObjectId,
      recipient = TaxEntity(Regime.sa, TEST_SAUTR, Some(TEST_EMAIL)),
      subject = TEST_MAIL_SUBJECT,
      body = Some(
        Details(
          form = None,
          `type` = None,
          suppressedAt = None,
          detailsId = None,
          threadId = Some(TEST_THREAD_ID)
        )
      ),
      validFrom = LocalDate.now(),
      alertFrom = None,
      alertDetails = AlertDetails("template-id", None, Map()),
      lastUpdated = None,
      hash = "*hash*",
      statutory = true,
      renderUrl = RenderUrl(service = "my-service", url = "service-url"),
      sourceData = None,
      externalRef = None
    )

    "return true if the message payload has 'gmc' as the external reference's source in a case-insensitive way" in new TestCase {
      val messageGmc = message.copy(externalRef = Some(ExternalRef("some-id", "gMc")))
      MessageValidator.isGmc(messageGmc) mustBe true
    }

    "return false if the message payload doesn't have 'gmc' as the external reference" in new TestCase {
      val messageNotGmc = message.copy(externalRef = Some(ExternalRef("another-id", "not-gmc")))
      MessageValidator.isGmc(messageNotGmc) mustBe false
    }
  }

  "checkEmailPresentForVat" must {

    val message = Message(
      id = new ObjectId,
      recipient = TaxEntity(Regime.sa, HmrcMtdVat("mtd-vat"), Some("test@test.com")),
      subject = TEST_MAIL_SUBJECT,
      body = None,
      validFrom = LocalDate.now(),
      alertFrom = None,
      alertDetails = AlertDetails("template-id", None, Map()),
      lastUpdated = None,
      hash = "*hash*",
      statutory = true,
      renderUrl = TEST_RENDER_URL,
      sourceData = None,
      externalRef = None
    )

    "return Success - when tax identifier is HMRC-MTD-VAT and the email is present" in {
      MessageValidator.checkEmailPresentForVat(message).isSuccess mustBe true
    }

    "return Failure - when tax identifier is HMRC-MTD-VAT and the email is absent" in {
      val messageNoEmail = message.copy(recipient = TaxEntity(Regime.sa, HmrcMtdVat("mtd-vat"), None))
      MessageValidator.checkEmailPresentForVat(messageNoEmail).isFailure mustBe true
    }
  }

  "isValidMessage" must {

    "return success for a valid message" in new Setup {
      MessageValidator.isValidMessage(validMessage) mustBe Success[Message](validMessage)
      MessageValidator.isValidMessage(validMessageWithSourceData) mustBe Success[Message](validMessageWithSourceData)

      MessageValidator.isValidMessage(validMessage.copy(alertQueue = Some("PRIORITY"))) mustBe Success[Message](
        validMessageWithAlertQueue
      )
    }

    "return MessageValidationException with correct error message details" when {

      "details are invalid" in new Setup {
        val messageWithInvalidDetails: Message =
          validMessage.copy(
            body = Some(details.copy(form = None)),
            externalRef = Some(TEST_EXTERNAL_REF.copy(source = TEST_SOURCE_GMC))
          )

        MessageValidator.isValidMessage(messageWithInvalidDetails) mustBe Failure(
          MessageValidationException("details: details not provided where it is required")
        )
      }

      "source data is invalid" in new Setup {
        val messageWithNoSourceData: Message =
          validMessage.copy(
            externalRef = Some(TEST_EXTERNAL_REF.copy(source = TEST_SOURCE_GMC)),
            sourceData = None
          )

        val messageWithEmptySourceData: Message =
          validMessage.copy(
            externalRef = Some(TEST_EXTERNAL_REF.copy(source = TEST_SOURCE_GMC)),
            sourceData = Some(EMPTY_STRING)
          )

        val messageWithSourceDataAndIsGMC: Message =
          validMessage.copy(
            externalRef = Some(TEST_EXTERNAL_REF.copy(source = TEST_SOURCE_GMC)),
            sourceData = None
          )

        MessageValidator.isValidMessage(messageWithNoSourceData) mustBe Failure(
          MessageValidationException("Invalid Message")
        )

        MessageValidator.isValidMessage(messageWithEmptySourceData).toString mustBe
          Failure(new IllegalArgumentException("sourceData: invalid source data provided")).toString

        MessageValidator.isValidMessage(messageWithSourceDataAndIsGMC).toString mustBe
          Failure(MessageValidationException("Invalid Message")).toString
      }

      "email address is empty in alertDetails" in new Setup {
        val alertDetailsWithEmptyEmailKey: AlertDetails =
          AlertDetails(TEST_TEMPLATE_ID, None, Map("email" -> EMPTY_STRING))

        val messageWithInvalidAlertDetails: Message = validMessage.copy(alertDetails = alertDetailsWithEmptyEmailKey)

        MessageValidator.isValidMessage(messageWithInvalidAlertDetails).toString mustBe Failure(
          MessageValidationException("email: invalid email address provided")
        ).toString
      }

      "alert queue is empty" in new Setup {
        val messageWithEmptyQueue: Message = validMessage.copy(alertQueue = Some(EMPTY_STRING))

        MessageValidator.isValidMessage(messageWithEmptyQueue).toString mustBe Failure(
          MessageValidationException("alertQueue: invalid alert queue provided")
        ).toString
      }

      "validFrom date is before the issueDate (checkValidIssueDate)" in new Setup {
        val messageWithInvalidDates: Message = validMessage.copy(validFrom = TEST_LOCAL_DATE.minusDays(1))

        MessageValidator.isValidMessage(messageWithInvalidDates).toString mustBe Failure(
          MessageValidationException("Issue date after the valid from date")
        ).toString
      }

      "recipient email is not valid" in new Setup {
        val messageWithInvalidRecipientEmail: Message =
          validMessage.copy(recipient = TaxEntity(Regime.sa, SaUtr("1234567890"), Some("test#test.com")))

        MessageValidator.isValidMessage(messageWithInvalidRecipientEmail).toString mustBe Failure(
          MessageValidationException("email: invalid email address provided")
        ).toString
      }

      "recipient email is absent and recipient tax identifier is not valid" in new Setup {

        case class InvalidIdentifier(itr: String) extends TaxIdentifier with SimpleName {
          override def toString: String = itr
          def value: String = itr
          val name = "itr"
        }

        val messageWithInvalidRecipientEmail: Message =
          validMessage.copy(recipient =
            TaxEntity(regime = Regime.sa, identifier = InvalidIdentifier("1234567890"), email = None)
          )

        MessageValidator.isValidMessage(messageWithInvalidRecipientEmail).toString mustBe Failure(
          MessageValidationException("email: email address not provided")
        ).toString
      }

      "alert queue type is invalid" in new Setup {
        val messageWithInvalidQueueType: Message = validMessage.copy(alertQueue = Some("UNKNOWN"))

        MessageValidator.isValidMessage(messageWithInvalidQueueType).toString mustBe Failure(
          MessageValidationException("Invalid alert queue submitted")
        ).toString
      }

      "recipient email is absent and recipient tax identifier is of type HmrcMtdVat" in new Setup {
        val messageWithInvalidRecipientEmail: Message =
          validMessage.copy(recipient =
            TaxEntity(regime = Regime.sa, identifier = HmrcMtdVat("1234567890"), email = None)
          )

        MessageValidator.isValidMessage(messageWithInvalidRecipientEmail).toString mustBe Failure(
          MessageValidationException("email: email address not provided")
        ).toString
      }
    }
  }

  trait Setup {
    val details: Details = Details(
      form = Some("SA300"),
      `type` = Some(TEST_FORM_TYPE),
      suppressedAt = None,
      detailsId = Some(TEST_DETAILS_ID),
      paperSent = Some(false),
      batchId = Some(TEST_BATCH_ID),
      issueDate = Some(TEST_LOCAL_DATE),
      replyTo = None,
      threadId = Some(TEST_THREAD_ID)
    )

    val validMessage: Message = Message(
      id = new ObjectId,
      recipient = TaxEntity(Regime.sa, SaUtr("1234567890"), Some("test@test.com")),
      subject = TEST_MAIL_SUBJECT,
      body = Some(details),
      validFrom = TEST_LOCAL_DATE,
      alertFrom = None,
      alertDetails = AlertDetails(TEST_TEMPLATE_ID, None, Map()),
      lastUpdated = None,
      hash = TEST_HASH,
      statutory = true,
      renderUrl = TEST_RENDER_URL,
      sourceData = Some(TEST_SOURCE_DATA),
      externalRef = Some(TEST_EXTERNAL_REF)
    )

    lazy val validMessageWithSourceData: Message = Message(
      id = new ObjectId,
      recipient = TaxEntity(Regime.sa, SaUtr("1234567890"), Some("test@test.com")),
      subject = TEST_MAIL_SUBJECT,
      body = Some(details),
      validFrom = TEST_LOCAL_DATE,
      alertFrom = None,
      alertDetails = AlertDetails(TEST_TEMPLATE_ID, None, Map()),
      lastUpdated = None,
      hash = TEST_HASH,
      statutory = true,
      renderUrl = TEST_RENDER_URL,
      sourceData = Some(TEST_SOURCE_DATA),
      externalRef = Some(TEST_EXTERNAL_REF)
    )

    lazy val validMessageWithAlertQueue: Message = validMessage.copy(alertQueue = Some("PRIORITY"))
  }
}
