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
import org.mongodb.scala.bson.ObjectId
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsNull, JsObject, JsResultException, JsString, JsValue, Json }
import uk.gov.hmrc.common.message.model.LifecycleStatusType.Submitted
import uk.gov.hmrc.common.message.model.MessageRESTFormats.*
import uk.gov.hmrc.common.message.util.MessageFixtures.testMessageWithContent
import uk.gov.hmrc.common.message.util.TestData.{ FIVE, TEST_BODY, TEST_ENVELOP_ID, TEST_HASH, TEST_ID, TEST_LIFECYCLE, TEST_LOCAL_DATE, TEST_RENDER_URL, TEST_SOURCE_DATA, TEST_SOURCE_MDTP, TEST_SUBJECT, TEST_TEMPLATE_ID, TEST_TIME_INSTANT, THREE }
import uk.gov.hmrc.domain.{ CtUtr, HmrcMtdItsa, Nino }
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.Implicits.objectIdFormat

import java.util.UUID

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
          "threadId":"6308ebc948f9b01e9092074e",
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

  "alertParams" must {
    "return correct value" in new Setup {
      message.alertParams mustBe Map("test_key" -> "test_value")
    }
  }

  "alertTemplateName" must {
    "return correct value" in new Setup {
      message.alertTemplateName mustBe TEST_TEMPLATE_ID
    }
  }

  "source" must {
    "return correct value" in new Setup {
      message.source mustBe Some(TEST_SOURCE_MDTP)
    }
  }

  "taxPayerName" must {
    "return correct value" in new Setup {
      message.taxPayerName mustBe Some(recipientName)
    }
  }

  "auditData" must {
    "return correct value" in new Setup {
      val auditData: Map[String, String] = message.auditData

      auditData.size must be(THREE)

      auditData("issueDate") mustBe "2023-01-01"
      auditData("type") mustBe "tax-summary-notification"
    }
  }

  "hardCopyAuditData" must {
    "return correct value" in new Setup {
      val hardCopyAuditData: Map[String, String] = message.hardCopyAuditData

      hardCopyAuditData.size must be(FIVE)

      hardCopyAuditData("ctutr") mustBe "123412342134"
      hardCopyAuditData("validFrom") mustBe "2025-11-28"
      hardCopyAuditData("issueDate") mustBe "2023-01-01"
      hardCopyAuditData("type") mustBe "tax-summary-notification"
    }
  }

  "taxIdWithNameWrites" must {
    "serialize TaxIdWithName to json" in {
      import uk.gov.hmrc.common.message.model.Message._
      val saUtr: TaxIdWithName = GenerateRandom.utrGenerator.nextSaUtr
      val expectedResult = Json.obj("name" -> "sautr", "value" -> saUtr.value)
      Json.toJson(saUtr) mustBe expectedResult
    }
  }

  "objectId" must {
    "serialize and deserialize from Json" in {
      val objectId: ObjectId = new ObjectId
      Json.toJson(objectId).as[ObjectId] mustBe objectId
    }
  }

  "LocalDate" must {
    "serialize and deserialize from Json" in {
      val localDate: LocalDate = LocalDate.now()
      Json.toJson(localDate).as[LocalDate] mustBe localDate
    }
  }

  "MessageDetails" must {
    "serialize and deserialize from Json" in {
      val messageDetails = MessageDetails(
        "formId",
        None,
        None,
        Some("sourceDate"),
        Some("batchId"),
        Some(LocalDate.now()),
        threadId = Some(new ObjectId().toString),
        replyTo = None,
        enquiryType = None,
        adviser = None,
        waitTime = None,
        topic = None,
        properties = None
      )
      Json.toJson(messageDetails).as[MessageDetails] mustBe messageDetails
    }
  }

  "Details" must {
    "deserialize from empty Json" in {
      import uk.gov.hmrc.common.message.model.MessageMongoFormats.DetailsFormatter.format
      val details = Json.parse(s"""{}""").as[Details]

      details mustBe Details(form = None, `type` = None, suppressedAt = None, detailsId = None, issueDate = None)
    }

    // format: off
    "deserialize from non-empty Json" in {
      import uk.gov.hmrc.common.message.model.MessageMongoFormats.DetailsFormatter.format
      val detailsJson = Json.parse(s"""{
                                      | "form":         "form",
                                      | "type":         "form-type",
                                      | "suppressedAt": "suppressed-at",
                                      | "detailsId":    "details-id",
                                      | "paperSent":    true,
                                      | "batchId":      "batch-id",
                                      | "issueDate":    "2024-07-16",
                                      | "replyTo":      "reply-to",
                                      | "threadId":     "thread-id",
                                      | "enquiryType":  "enquiry-type",
                                      | "adviser":      { "pidId": "pid-id" },
                                      | "waitTime":     "wait-time",
                                      | "topic":        "topic",
                                      | "envelopId":    "envelop-id",
                                      | "properties":   { "Hello": "world" }
                                      |}""".stripMargin)
      val details = detailsJson.as[Details]

      details mustBe Details(
        form          = Some("form"),
        `type`        = Some("form-type"),
        suppressedAt  = Some("suppressed-at"),
        detailsId     = Some("details-id"),
        paperSent     = Some(true),
        batchId       = Some("batch-id"),
        issueDate     = Some(LocalDate.of(2024, 7, 16)),
        replyTo       = Some("reply-to"),
        threadId      = Some("thread-id"),
        enquiryType   = Some("enquiry-type"),
        adviser       = Some(Adviser(pidId = "pid-id")),
        waitTime      = Some("wait-time"),
        topic         = Some("topic"),
        envelopId     = Some("envelop-id"),
        properties    = Some(JsObject(Seq("Hello" -> JsString("world"))))
      )
    }
    // format: on
  }

  "ConversationItem" must {
    "serialize to correct json" in {
      val conversationItem = ConversationItem(
        "id",
        "subject",
        Some(
          Details(None, Some("tax-summary-notification"), None, None, issueDate = Some(LocalDate.parse("2023-01-01")))
        ),
        LocalDate.parse("2023-01-01"),
        None
      )
      Json.toJson(conversationItem) mustBe Json.parse(
        """{"id":"id","subject":"subject","body":{"type":"tax-summary-notification","issueDate":"2023-01-01"},"validFrom":"2023-01-01"}"""
      )
    }
  }

  "hardCopyAuditData" must {
    "return correct audit data for a recipient with ITSA ID" in {
      val message = testMessageWithContent(
        id = new ObjectId("6409cd09f156deb4633d3c56"),
        uuid = UUID.fromString("41c44af3-9e38-4249-bec7-03aacd3da5f8"),
        recipientId = HmrcMtdItsa("XCIT00000564721"),
        content = "Test content"
      )

      message.hardCopyAuditData must {
        contain("messageId" -> "6409cd09f156deb4633d3c56") and
          contain("HMRC-MTD-IT" -> "XCIT00000564721")
      }
    }

    "return correct audit data for a recipient with NINO" in {
      val message = testMessageWithContent(
        id = new ObjectId("6409cd09f156deb4633d3c56"),
        uuid = UUID.fromString("41c44af3-9e38-4249-bec7-03aacd3da5f8"),
        recipientId = Nino("CE123456D"),
        content = "Test content"
      )

      message.hardCopyAuditData must {
        contain("messageId" -> "6409cd09f156deb4633d3c56") and
          contain("nino" -> "CE123456D")
      }
    }
  }

  "Lifecycle.lifecycleFormat" must {
    import Lifecycle.lifecycleFormat

    "read the json correctly" in new Setup {
      Json.parse(lifeCycleJsonString).as[Lifecycle] mustBe TEST_LIFECYCLE
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(lifeCycleInvalidJsonString).as[Lifecycle]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(TEST_LIFECYCLE) mustBe Json.parse(lifeCycleJsonString)
    }
  }

  "MessageContentParameters.messageTemplateFormats" should {
    import MessageContentParameters.messageTemplateFormats

    "read the json correctly" in new Setup {
      Json.parse(msgContentParametersJsString).as[MessageContentParameters] mustBe msgContentParameters
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(msgContentParametersInvalidJsString).as[MessageContentParameters]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(msgContentParameters) mustBe Json.parse(msgContentParametersJsString)
    }
  }

  "MessageStatus.format" should {
    import MessageStatus.format

    "read the json correctly" in new Setup {
      Json.parse(msgStatusJsonString).as[MessageStatus] mustBe msgStatus
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(msgStatusInvalidJsonString).as[MessageStatus]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(msgStatus) mustBe Json.parse(msgStatusJsonString)
    }
  }

  "Adviser.adviserFormat" should {
    import Adviser.adviserFormat

    "read the json correctly" in new Setup {
      Json.parse(adviserJsonString).as[Adviser] mustBe adviser
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(adviserInvalidJsonString).as[Adviser]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(adviser) mustBe Json.parse(adviserJsonString)
    }
  }

  "SendAlertResponse.format" should {
    import SendAlertResponse.format

    "read the json correctly" in new Setup {
      Json.parse(sendAlertResponseJsonString).as[SendAlertResponse] mustBe sendAlertResponse
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(sendAlertResponseInvalidJsonString).as[SendAlertResponse]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(sendAlertResponse) mustBe Json.parse(sendAlertResponseJsonString)
    }
  }

  trait Setup {
    val msgContentParameters: MessageContentParameters =
      MessageContentParameters(data = JsNull, templateId = TEST_TEMPLATE_ID)

    val msgStatus: MessageStatus = MessageStatus(envelopeId = Some(TEST_ENVELOP_ID), status = Some(Submitted))

    val adviser: Adviser = Adviser(pidId = TEST_ID)

    val sendAlertResponse: SendAlertResponse = SendAlertResponse(sendAlert = true)

    val details: Details =
      Details(
        form = None,
        `type` = Some("tax-summary-notification"),
        suppressedAt = None,
        detailsId = None,
        issueDate = Some(LocalDate.parse("2023-01-01"))
      )

    val recipientName: TaxpayerName = TaxpayerName(title = Some(TEST_TEMPLATE_ID))

    val alertDetails: AlertDetails = AlertDetails(
      templateId = TEST_TEMPLATE_ID,
      recipientName = Some(recipientName),
      data = Map("test_key" -> "test_value")
    )

    val message: Message = Message(
      recipient = TaxEntity(Regime.ct, CtUtr("123412342134"), None),
      subject = TEST_SUBJECT,
      body = Some(details),
      validFrom = TEST_LOCAL_DATE,
      alertFrom = None,
      alertDetails = alertDetails,
      lastUpdated = Some(TEST_TIME_INSTANT),
      hash = TEST_HASH,
      statutory = true,
      renderUrl = TEST_RENDER_URL,
      sourceData = Some(TEST_SOURCE_DATA),
      externalRef = Some(ExternalRef(id = TEST_ID, source = TEST_SOURCE_MDTP))
    )

    val lifeCycleJsonString: String =
      """{
        |"status":{"name":"SUBMITTED","updated":"+3562062-11-04T15:12:14Z"},
        |"startedAt":"+3562062-11-03T15:12:14Z",
        |"notification":{"count":5,"lastSent":"+3562062-11-03T15:12:14Z"}
        |}""".stripMargin

    val lifeCycleInvalidJsonString: String =
      """{
        |"status":{"updated":"+3562062-11-04T15:12:14Z"},
        |"startedAt":"+3562062-11-03T15:12:14Z",
        |"notification":{"count":5,"lastSent":"+3562062-11-03T15:12:14Z"}
        |}""".stripMargin

    val msgContentParametersJsString: String = """{"data":null,"templateId":"test_template"}""".stripMargin
    val msgContentParametersInvalidJsString: String = """{"templateId":"test_template"}""".stripMargin

    val msgStatusJsonString: String = """{"envelopeId":"test_envelopeId","status":"SUBMITTED"}""".stripMargin
    val msgStatusInvalidJsonString: String = """{"envelopeId":5,"status":"SUBMITTED"}""".stripMargin

    val adviserJsonString: String = """{"pidId":"test_id"}""".stripMargin
    val adviserInvalidJsonString: String = """{}""".stripMargin

    val sendAlertResponseJsonString: String = """{"sendAlert":true}""".stripMargin
    val sendAlertResponseInvalidJsonString: String = """{}""".stripMargin
  }
}
