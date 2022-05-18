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

package uk.gov.hmrc.message2.library.model

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.message2.library.model.MessageRESTFormats._

class MessageSpec extends PlaySpec {
  "message creation from two-way-message" must {
    "adviser reply message successfully serialise with a topic" in {

      val expectedJson =
        s"""{
        "externalRef":{
          "id":"123412342314",
          "source":"2WSM"
        },
        "recipient":{
          "taxIdentifier":{
          "name":"nino",
          "value":"AB123456C"
        },
          "email":"someEmail@test.com"
        },
        "messageType":"2wsm-advisor",
        "subject":"QUESTION",
        "content":"SGVsbG8gV29scmQ=",
        "details":{
          "formId":"2wsm-reply",
          "replyTo":"5c85a5000000000000000001",
          "enquiryType":"p800",
          "threadId":"5c85a5000000000000000000",
          "batchId":"batch-id",
          "topic":"some-topic-name"
        }
      }"""
      val parsedMessage = Json.parse(expectedJson).as[Message]
      parsedMessage.body.get.topic.get must equal("some-topic-name")
    }
  }

  "message creation from CDS HMRC-CUS-ORG" must {
    "adviser message successfully serialised" in {

      val expectedJson =
        s"""{
        "externalRef":{
          "id":"GB123412342314",
          "source":"mdtp"
        },
        "recipient":{
          "taxIdentifier":{
          "name":"HMRC-CUS-ORG",
          "value":"GB1234567890"
        },
          "email":"someEmail@test.com"
        },
        "messageType":"cds_ddi_setup_dcs_alert",
        "subject":"QUESTION",
        "content":"SGVsbG8gV29scmQ=",
        "alertQueue": "DEFAULT",
        "tags": {
          "notificationType": "Direct Debit"
        }
      }"""
      val parsedMessage = Json.parse(expectedJson).as[Message]
      parsedMessage.tags.getOrElse(Map()).getOrElse("notificationType", "") must equal("Direct Debit")
    }
  }

  "message creation from HMRC-PPT-ORG" must {
    "adviser message successfully serialised" in {

      val expectedJson =
        s"""{
        "externalRef":{
          "id":"GB123412342315",
          "source":"mdtp"
        },
        "recipient":{
          "taxIdentifier":{
          "name":"HMRC-PPT-ORG.ETMPREGISTRATIONNUMBER",
          "value":"XMPPT0000000001"
        },
          "email":"someEmail@test.com"
        },
        "messageType":"ppt_ddi_setup_dcs_alert",
        "subject":"QUESTION",
        "content":"SGVsbG8gV29scmQ=",
        "alertQueue": "DEFAULT",
        "tags": {
          "notificationType": "Direct Debit"
        }
      }"""
      val parsedMessage = Json.parse(expectedJson).as[Message]
      parsedMessage.tags.getOrElse(Map()).getOrElse("notificationType", "") must equal("Direct Debit")
    }
  }

  "taxIdWithNameWrites" must {
    "serialize TaxIdWithName to json" in {
      import uk.gov.hmrc.message2.library.model.Message._
      val saUtr: TaxIdWithName = GenerateRandom.utrGenerator.nextSaUtr
      val expectedResult = Json.obj("name" -> "sautr", "value" -> saUtr.value)
      Json.toJson(saUtr) mustBe expectedResult
    }
  }

  "bsonObjectIdWrites" must {
    "serialize BSONObjectID to json" in {
      import uk.gov.hmrc.message2.library.model.Message._
      val bsonId: BSONObjectID = BSONObjectID.generate()

      Json.toJson(bsonId)(bsonObjectIdWrites) mustBe Json.toJson(bsonId.stringify)

    }
  }
}
