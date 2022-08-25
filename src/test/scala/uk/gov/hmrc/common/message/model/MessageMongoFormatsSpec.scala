/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.common.message.model

import org.joda.time.{DateTime, LocalDate}
import org.mongodb.scala.bson.ObjectId
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import uk.gov.hmrc.common.message.util.MessageFixtures._
import uk.gov.hmrc.common.message.util.{MessageFixtures, Resources}
import uk.gov.hmrc.domain.SaUtr

import java.time.Instant

class MessageMongoFormatsSpec extends AnyWordSpecLike with Matchers {

  val taxPayername = TaxpayerName(
    title = Some("Dr"),
    forename = Some("Bruce"),
    secondForename = Some("Hulk"),
    surname = Some("Banner"),
    honours = Some("Green")
  )

  "The message mongo format" must {
    val exampleMessage = Message(
      id = new ObjectId("55a921d84f573b6f14325b57"),
      recipient = MessageFixtures.createTaxEntity(SaUtr("12345678")),
      subject = "asdfg",
      body = Some(Details(None, Some("tax-summary-notification"), None, None, issueDate = Some(LocalDate.now))),
      validFrom = LocalDate.parse("2015-07-17"),
      alertFrom = Some(LocalDate.parse("2015-07-18")),
      alertDetails = AlertDetails("templateId", Some(taxPayername), Map("key 1" -> "value 1", "key2" -> "value2")),
      alerts = Some(EmailAlert(None, DateTime.parse("2015-07-17T15:40:08.829Z"), false, None)),
      rescindment =
        Some(Rescindment(java.time.Instant.ofEpochMilli(DateTime.parse("2015-07-17T15:40:09.829Z").toInstant.getMillis), Rescindment.Type.GeneratedInError, "blah")),
      lastUpdated = None,
      hash = "O4KWyUPKQySWUVzQVfoPswBEKfN1gLe9dXi7EzCwp5U=",
      statutory = false,
      renderUrl = RenderUrl("sa-message-renderer", "/messages/sa/12345678/55a921d84f573b6f14325b57"),
      sourceData = None,
      tags = Some(Map("notificationType" -> "Direct Debit"))
    )

    "be able to read and write to the current mongo format" in {
      val messageReads = Json.toJson(exampleMessage)(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage
    }

    "be able to read and write to the current mongo format with verificationBreak" in {
      val messageReads = Json.toJson(exampleMessage.copy(verificationBrake = Some(true)))(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(verificationBrake = Some(true))
    }

    "be able to read and write to the current mongo format without recipientName" in {
      val messageReads = Json.toJson(exampleMessage.copy(alertDetails = AlertDetails("newMessageAlert", None, Map())))(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(alertDetails = AlertDetails("newMessageAlert", None, Map()))
    }

    "be able to read the mongo format without the rescindment" in {
      val messageReads = Json.toJson(exampleMessage.copy(rescindment = None))(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(rescindment = None)
    }

    "be able to read the mongo format without the alertDetails" in {
      val messageReads = Json.toJson(exampleMessage.copy(alertDetails = AlertDetails("newMessageAlert", None, Map()), rescindment = None))(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(alertDetails = AlertDetails("newMessageAlert", None, Map()), rescindment = None)
    }

    "be able to read the mongo format without the statutory flag" in {
      val messageReads = Json.toJson(exampleMessage.copy(statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        )))(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        ))
    }

    "be able to read the render url if it is already stored in the database as a field" in {

      val messageReads = Json.toJson(exampleMessage.copy(
        statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        ),
        renderUrl = RenderUrl("abc", "url")
      )
      )(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        ),
        renderUrl = RenderUrl("abc", "url"))
    }

    "be able to construct the default the render url if it is not stored in the database as a field" in {
      val messageReads = Json.toJson(exampleMessage.copy(
        statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        ),
        renderUrl = RenderUrl("sa-message-renderer", "/messages/sa/12345678/55a921d84f573b6f14325b57")
      )
      )(MessageMongoFormats.messageMongoFormat)
      val messageWrites = messageReads.as[Message](MessageMongoFormats.messageMongoFormat)
      messageWrites mustBe exampleMessage.copy(
        statutory = true,
        body = Some(
          Details(
            Some("SA316 2015"),
            Some("print-suppression-notification"),
            Some("2015-01-02"),
            Some("C0123456781234568")
          )
        ),
        renderUrl = RenderUrl("sa-message-renderer", "/messages/sa/12345678/55a921d84f573b6f14325b57")

      )
    }
  }
}
