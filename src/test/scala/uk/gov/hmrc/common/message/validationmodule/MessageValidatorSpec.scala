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
import uk.gov.hmrc.common.message.model._
import uk.gov.hmrc.domain.{ HmrcMtdVat, SaUtr }

class MessageValidatorSpec extends AnyWordSpecLike with MockitoSugar with Matchers with ScalaFutures {

  "isGmc" must {

    val message = Message(
      id = new ObjectId,
      recipient = TaxEntity(Regime.sa, SaUtr("1234567890"), Some("test@test.com")),
      subject = "RE: Subject",
      body = Some(
        Details(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          Some("5c85a5000000000000000000"),
          None,
          None,
          None,
          None,
          None,
          None
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
      subject = "RE: Subject",
      body = None,
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

    "return Success - when tax identifier is HMRC-MTD-VAT and the email is present" in {
      MessageValidator.checkEmailPresentForVat(message).isSuccess mustBe true
    }

    "return Failure - when tax identifier is HMRC-MTD-VAT and the email is absent" in {
      val messageNoEmail = message.copy(recipient = TaxEntity(Regime.sa, HmrcMtdVat("mtd-vat"), None))
      MessageValidator.checkEmailPresentForVat(messageNoEmail).isFailure mustBe true
    }

  }
}
