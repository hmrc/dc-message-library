/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.common.message.util

import org.apache.commons.codec.binary.Base64
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import play.api.libs.json.{ JsObject, JsValue, Json, OFormat }
import uk.gov.hmrc.common.message.model.*
import uk.gov.hmrc.common.message.validationmodule.MessageValidationException

import java.time.LocalDate
import java.util.regex.Pattern
import scala.collection.mutable.ListBuffer
import scala.util.{ Failure, Success, Try }

object SecureMessageUtil {

  val langPattern = Pattern.compile("^(en|cy)$", Pattern.CASE_INSENSITIVE)
  lazy val NO_SUBJECT = "(no subject)"

  def checkValidContent(message: Message): Try[Message] = message.content match {
    case Some(content) if !validateLang(Jsoup.parse(content)) =>
      Failure(MessageValidationException("Content Body: Missing/Invalid language"))
    case Some(content) if !validateSubject(Jsoup.parse(content), message.subject.nonEmpty) =>
      Failure(MessageValidationException("Content Body: Missing Subject"))
    case _ => Success(message)
  }

  private def validateLang(doc: Document): Boolean =
    doc.getElementsByTag("section").size() match {
      case 0 => true
      case n =>
        (doc.getElementsByAttribute("lang").size == n) &&
        (doc.getElementsByAttributeValueMatching("lang", langPattern).size() > 0)
    }

  private def validateSubject(doc: Document, hasExternalSubject: Boolean): Boolean =
    doc.getElementsByTag("section").size() match {
      case 0 => hasExternalSubject
      case n => hasExternalSubject || doc.getElementsByAttribute("subject").size == n
    }

  private def decodeBase64String(input: String): String =
    new String(Base64.decodeBase64(input.getBytes("UTF-8")))

  private def encodeBase64String(input: String): String =
    new String(Base64.encodeBase64(input.getBytes("UTF-8")))

  def createSecureMessage(v3Request: JsValue): JsValue = {

    val alertQueue = (v3Request \ "alertQueue").asOpt[String]
    val alertDetails = (v3Request \ "alertDetails").asOpt[JsValue]
    val tags = (v3Request \ "tags").asOpt[JsValue]
    val validFrom = (v3Request \ "validFrom").asOpt[LocalDate]

    val recipient = (v3Request \ "recipient").as[Recipient]
    val (content, language) = createContent((v3Request \ "content").as[String], (v3Request \ "subject").asOpt[String])

    def createRecipient: JsValue = {
      var recipientJson = Json.obj(
        "taxIdentifier" -> Json.obj(
          "name"  -> (v3Request \ "recipient" \ "taxIdentifier" \ "name").as[String],
          "value" -> (v3Request \ "recipient" \ "taxIdentifier" \ "value").as[String]
        )
      )

      if (recipient.name.isDefined) recipientJson = recipientJson ++ Json.obj("name" -> recipient.name)
      if (recipient.email.isDefined) recipientJson = recipientJson ++ Json.obj("email" -> recipient.email)

      recipientJson = recipientJson ++ Json.obj("regime" -> TaxEntity.regimeOf(recipient.taxIdentifier))

      recipientJson
    }

    def messageDetails: JsObject = {

      var details = Json.obj()

      val formId = (v3Request \ "details" \ "formId").asOpt[String]
      if (formId.isDefined)
        details = details ++ Json.obj("formId" -> formId)

      val statutory = (v3Request \ "details" \ "statutory").asOpt[Boolean]
      if (statutory.isDefined)
        details = details ++ Json.obj("statutory" -> statutory)

      val paperSent = (v3Request \ "details" \ "paperSent").asOpt[Boolean]
      if (paperSent.isDefined)
        details = details ++ Json.obj("paperSent" -> paperSent)

      val sourceData = (v3Request \ "details" \ "sourceData").asOpt[String]
      if (sourceData.isDefined)
        details = details ++ Json.obj("sourceData" -> sourceData)

      val batchId = (v3Request \ "details" \ "batchId").asOpt[String]
      if (batchId.isDefined)
        details = details ++ Json.obj("batchId" -> batchId)

      val issueDate = (v3Request \ "details" \ "issueDate").asOpt[LocalDate]
      if (issueDate.isDefined)
        details = details ++ Json.obj("issueDate" -> issueDate)

      val replyTo = (v3Request \ "details" \ "replyTo").asOpt[String]
      if (replyTo.isDefined)
        details = details ++ Json.obj("replyTo" -> replyTo)

      val threadId = (v3Request \ "details" \ "threadId").asOpt[String]
      if (threadId.isDefined)
        details = details ++ Json.obj("threadId" -> threadId)

      val enquiryType = (v3Request \ "details" \ "enquiryType").asOpt[String]
      if (enquiryType.isDefined)
        details = details ++ Json.obj("enquiryType" -> enquiryType)

      val adviser = (v3Request \ "details" \ "adviser").asOpt[Adviser]
      if (adviser.isDefined)
        details = details ++ Json.obj("adviser" -> adviser)

      val waitTime = (v3Request \ "details" \ "waitTime").asOpt[String]
      if (waitTime.isDefined)
        details = details ++ Json.obj("waitTime" -> waitTime)

      val topic = (v3Request \ "details" \ "topic").asOpt[String]
      if (topic.isDefined)
        details = details ++ Json.obj("topic" -> topic)

      val properties = (v3Request \ "details" \ "properties").asOpt[JsValue]
      if (properties.isDefined)
        details = details ++ Json.obj("properties" -> properties)

      details
    }

    var secureMessage = Json.obj(
      "externalRef" -> (v3Request \ "externalRef").as[ExternalRef],
      "recipient"   -> createRecipient,
      "messageType" -> (v3Request \ "messageType").as[String],
      "content"     -> Json.toJson(content),
      "language"    -> language
    )

    val details: JsObject = messageDetails
    if (details.value.nonEmpty)
      secureMessage = secureMessage ++ Json.obj("details" -> details)

    if (validFrom.isDefined) {
      secureMessage = secureMessage ++ Json.obj("validFrom" -> validFrom)
    }
    if (alertQueue.isDefined) {
      secureMessage = secureMessage ++ Json.obj("alertQueue" -> alertQueue)
    }
    if (alertDetails.isDefined) {
      secureMessage = secureMessage ++ Json.obj("alertDetails" -> alertDetails)
    }
    if (tags.isDefined) {
      secureMessage = secureMessage ++ Json.obj("tags" -> tags)
    }

    secureMessage
  }

  def createContent(content: String, subjectOp: Option[String]): (List[Content], String) = {
    val doc: Document = Jsoup.parse(decodeBase64String(content))

    val contentList: ListBuffer[Content] = ListBuffer.empty[Content]

    doc.getElementsByTag("section") match {
      case elements: Elements if elements.size() > 0 =>
        elements.forEach { e =>
          val lang = e.attr("lang")
          val subjectAttr = e.attr("subject")
          val subject = if (subjectAttr.isEmpty) subjectOp.getOrElse(NO_SUBJECT) else subjectAttr
          val body = e.toString
          contentList += Content(lang, subject, encodeBase64String(body))
        }
        val langOfSubject = doc.getElementsByAttribute("lang").attr("lang")
        (contentList.toList, langOfSubject)
      case _ =>
        contentList += Content("en", subjectOp.getOrElse(NO_SUBJECT), content)
        (contentList.toList, "en")
    }
  }

  case class Content(lang: String, subject: String, body: String)

  object Content {
    implicit val contentFormat: OFormat[Content] = Json.format[Content]
  }
}
