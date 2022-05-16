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

package uk.gov.hmrc.message.library.model

import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.joda.time.{DateTime, LocalDate}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.http.controllers.RestFormats
import uk.gov.hmrc.mongo.json.BSONObjectIdFormats

import java.security.MessageDigest

object MessageRESTFormats extends RestFormats with BSONObjectIdFormats with AlertEmailTemplateMapper {

  implicit val messageApiV3Reads: Reads[Message] =
    ((__ \ "externalRef").read[ExternalRef] and
      (__ \ "recipient").read[Recipient] and
      (__ \ "messageType").read[String] and
      (__ \ "subject").read[String] and
      (__ \ "validFrom").readNullable[LocalDate](jodaDateReads("validFrom")) and
      (__ \ "deliveredOn").readNullable[DateTime] and
      (__ \ "content").read[String] and
      (__ \ "details").readNullable[MessageDetails] and
      (__ \ "alertQueue").readNullable[String] and
      (__ \ "emailAlertEventUrl").readNullable[String] and
      Reads[Option[Map[String, String]]](jsValue =>
          ( __ \ "alertDetails" \ "data").asSingleJson(jsValue) match {
             case JsDefined(value) => value.validate[Map[String, String]].map(Some.apply).
                                                          orElse(JsError("sourceData: invalid source data provided"))
             case JsUndefined() => JsSuccess(None)}) and
      Reads[Option[Map[String, String]]](jsValue =>
        ( __ \ "tags").asSingleJson(jsValue) match {
          case JsDefined(value) => value.validate[Map[String, String]]
            .map(Some.apply)
            .orElse(JsError("tags : invalid data provided"))
          case JsUndefined() => JsSuccess(None)})) {
      (externalRef, recipient, messageType, subject, vf, deliveredOn, content, messageDetails, alertQueue, emailAlertEventUrl, alertDetailsData, tags) =>
        val issueDate = messageDetails.flatMap(_.issueDate).getOrElse(LocalDate.now)

        val validFrom = vf.filter(_.isAfter(issueDate)).getOrElse(issueDate)

        val id = BSONObjectID.generate

        def decodeBase64String(input: String): String =
          new String(Base64.decodeBase64(input.getBytes("UTF-8")))

        val hash: String = {
          val sha256Digester = MessageDigest.getInstance("SHA-256")
          Base64.encodeBase64String(
            sha256Digester.digest(
              Seq(
                subject,
                content,
                messageDetails.map(_.formId).getOrElse(""),
                recipient.taxIdentifier.name,
                recipient.taxIdentifier.value,
                validFrom.toString
              ).mkString("/").getBytes("UTF-8")
            )
          )
        }

        val email = recipient.email.fold[Map[String, String]](Map.empty)(v => Map("email" -> v))
        val responseTime: Map[String, String] =
          messageDetails.flatMap(_.waitTime).fold[Map[String, String]](Map.empty)(v => Map("waitTime" -> v))
        val data = email ++ responseTime ++ Map("date" -> validFrom.toString, "subject" -> subject) ++ alertDetailsData.getOrElse(Map())

        val rendererService = externalRef.source.toUpperCase match {
          case "2WSM" => "two-way-message"
          case _      => "external-message-adapter"
        }

        val details = messageDetails.map { ds =>
          val threadId = ds.threadId.getOrElse(BSONObjectID.generate()) // DC-1738

          Details(
            Some(ds.formId),
            Some(messageType),
            None,
            None,
            Some(ds.paperSent),
            ds.batchId,
            Some(issueDate),
            ds.replyTo,
            Some(threadId),
            ds.enquiryType,
            ds.adviser,
            ds.waitTime,
            ds.topic,
            properties = ds.properties
          )
        }

        val templateId = messageDetails
          .map(_.formId)
          .map(emailTemplateFromMessageFormId(_))
          .getOrElse(messageType)

        val recipientName = recipient.name.map(_.withDefaultLine1)

        val url = s"/external-message-adapter/external/messages/${id.stringify}/content"
        Message(
          id = id,
          recipient = TaxEntity.create(recipient.taxIdentifier, recipient.email, recipient.regime),
          subject = subject,
          body = details,
          validFrom = validFrom,
          lastUpdated = None,
          alertFrom = Some(validFrom),
          alertDetails = AlertDetails(templateId, recipientName, data),
          alertQueue = alertQueue,
          hash = hash,
          statutory = messageDetails.exists(_.statutory),
          renderUrl = RenderUrl(rendererService, url),
          externalRef = Some(externalRef),
          content = Some(decodeBase64String(content)),
          sourceData = messageDetails.flatMap(_.sourceData),
          emailAlertEventUrl = emailAlertEventUrl,
          tags = tags
        )
    }

}
