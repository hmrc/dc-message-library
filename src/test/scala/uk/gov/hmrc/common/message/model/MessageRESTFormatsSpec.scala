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

package uk.gov.hmrc.common.message.model

import java.time.LocalDate
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class MessageRESTFormatsSpec extends PlaySpec {

  "must always have alertFrom even when validFrom is not provided" in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithoutAlertFrom))
    parsedMessage.get.alertFrom mustBe Some(LocalDate.now())
  }

  "alertFrom must be the same as validFrom when valid from is provided" in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithAlertFrom))
    parsedMessage.get.alertFrom must not be None
    parsedMessage.get.alertFrom mustBe Some(parsedMessage.get.validFrom)
  }

  "must parse validFrom successfully" in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithTags))
    parsedMessage.get.validFrom mustBe LocalDate.parse("2018-01-01")
  }

  "must parse issueDate successfully" in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithTags))
    parsedMessage.get.body.get.issueDate mustBe Some(LocalDate.parse("2017-12-01"))
  }

  "check the renderer is set to 'two-way-message' for 2WSM " in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageFrom2WSM))
    parsedMessage.get.renderUrl.service mustEqual "two-way-message"
  }

  "check the renderer is set to 'message' for anything NOT 2WSM " in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithoutAlertFrom))
    parsedMessage.get.renderUrl.service mustEqual "external-message-adapter"
  }

  "check tags are serialised into their map counterpart" in {
    val parsedMessage = MessageRESTFormats.messageApiV3Reads.reads(Json.parse(messageWithTags))
    parsedMessage.get.tags.getOrElse(Map()).getOrElse("notificationType", "") mustEqual "Direct Debit"
  }

  val messageWithTags = """{
                          |   "externalRef":{
                          |       "id":"666",
                          |       "source":"mdtp"
                          |   },
                          |   "recipient":{
                          |       "taxIdentifier":{
                          |           "name":"HMRC-CUS-ORG",
                          |           "value":"GB1234567890"
                          |       },
                          |       "name":{
                          |           "line1": "Mr. John Smith"
                          |       },
                          |       "email":"johnsmith@gmail.com"
                          |   },
                          |   "details": {
                          |       "formId": "some-form-id",
                          |       "issueDate": "2017-12-01"
                          |   },
                          |   "validFrom": "2018-01-01",
                          |   "messageType":"cds_ddi_setup_dcs_alert",
                          |   "subject":"Confirmation of your CDS Registration",
                          |   "content":"SGVsbG8gV29ybGQ=",
                          |   "alertQueue":"DEFAULT",
                          |    "tags": {
                          |      "notificationType": "Direct Debit"
                          |    }
       }""".stripMargin

  val messageWithoutAlertFrom = """{
                                  |   "externalRef":{
                                  |       "id":"666",
                                  |       "source":"sees"
                                  |   },
                                  |   "recipient":{
                                  |       "taxIdentifier":{
                                  |           "name":"HMRC-OBTDS-ORG",
                                  |           "value":"XZFH00000100024"
                                  |       },
                                  |       "name":{
                                  |           "line1": "Mr. John Smith"
                                  |       },
                                  |       "email":"johnsmith@gmail.com"
                                  |   },
                                  |   "messageType":"fhddsAlertMessage",
                                  |   "subject":"Confirmation of your FHDDS Registration",
                                  |   "content":"SGVsbG8gV29ybGQ=",
                                  |   "alertQueue":"PRIORITY"
       }""".stripMargin

  val messageWithAlertFrom = """{
                               |   "externalRef":{
                               |       "id":"666",
                               |       "source":"sees"
                               |   },
                               |   "recipient":{
                               |       "taxIdentifier":{
                               |           "name":"HMRC-OBTDS-ORG",
                               |           "value":"XZFH00000100024"
                               |       },
                               |       "name":{
                               |           "line1": "Mr. John Smith"
                               |       },
                               |       "email":"johnsmith@gmail.com"
                               |   },
                               |   "validFrom": "2018-01-01",
                               |   "messageType":"fhddsAlertMessage",
                               |   "subject":"Confirmation of your FHDDS Registration",
                               |   "content":"SGVsbG8gV29ybGQ=",
                               |   "alertQueue":"PRIORITY"
       }""".stripMargin

  val messageFrom2WSM = """{
                          |   "externalRef":{
                          |       "id":"666",
                          |       "source":"2WSM"
                          |   },
                          |   "recipient":{
                          |       "taxIdentifier":{
                          |           "name":"HMRC-OBTDS-ORG",
                          |           "value":"XZFH00000100024"
                          |       },
                          |       "name":{
                          |           "line1": "Mr. John Smith"
                          |       },
                          |       "email":"johnsmith@gmail.com"
                          |   },
                          |   "validFrom": "2018-01-01",
                          |   "messageType":"fhddsAlertMessage",
                          |   "subject":"Confirmation of your FHDDS Registration",
                          |   "content":"SGVsbG8gV29ybGQ=",
                          |   "alertQueue":"PRIORITY"
       }""".stripMargin

}
