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

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import org.joda.time.{DateTime, LocalDate}
import org.mongodb.scala.bson.ObjectId
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{Format, JsError, JsObject, JsResult, JsString, JsSuccess, JsValue, Json, OFormat, Reads, Writes}
import uk.gov.hmrc.common.message.model.Rescindment.Type.GeneratedInError
import uk.gov.hmrc.domain.TaxIds._
import uk.gov.hmrc.mongo.workitem.ProcessingStatus
import uk.gov.hmrc.mongo.workitem.ProcessingStatus.ToDo
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats.Implicits.jotLocalDateFormat


case class MessageContentParameters(data: ContentParameters, templateId: String)
object MessageContentParameters {
  implicit val messageTemplateFormats = Json.format[MessageContentParameters]
}

case class Rescindment(time: DateTime, `type`: Rescindment.Type, ref: String)
object Rescindment {
  sealed trait Type extends EnumEntry
  object Type extends Enum[Type] with PlayJsonEnum[Type] {
    val values = findValues

    case object GeneratedInError extends Type

  }
  import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats.Implicits.jotDateTimeFormat
  implicit val rescindmentFormat: Format[Rescindment] = Json.format[Rescindment]
}

object AlertQueueTypes {
  val alertQueueTypes = List("PRIORITY", "DEFAULT", "BACKGROUND")
}

trait Alertable {
  def alertParams: Map[String, String]

  def recipient: TaxEntity

  def alertTemplateName: String

  def auditData: Map[String, String]

  def id: ObjectId

  def externalRef: Option[ExternalRef]

  def statutory: Boolean

  def validFrom: LocalDate

  def hardCopyAuditData: Map[String, String]

  def taxPayerName: Option[TaxpayerName]

  def alertQueue: Option[String]

  def source: Option[String]
}

case class Message(
  id: ObjectId = new ObjectId,
  recipient: TaxEntity,
  subject: String,
  body: Option[Details],
  validFrom: LocalDate,
  alertFrom: Option[LocalDate],
  alertDetails: AlertDetails,
  alerts: Option[EmailAlert] = None,
  alertQueue: Option[String] = None,
  readTime: Option[DateTime] = None,
  archiveTime: Option[DateTime] = None,
  contentParameters: Option[MessageContentParameters] = None,
  status: ProcessingStatus = ToDo,
  rescindment: Option[Rescindment] = None,
  lastUpdated: Option[DateTime],
  hash: String,
  statutory: Boolean,
  renderUrl: RenderUrl,
  sourceData: Option[String],
  externalRef: Option[ExternalRef] = None,
  content: Option[String] = None,
  emailAlertEventUrl: Option[String] = None,
  verificationBrake: Option[Boolean] = None,
  lifecycle: Option[Lifecycle] = None,
  tags: Option[Map[String, String]] = None,
  deliveredOn: Option[DateTime] = None,
  mailgunStatus: Option[MailgunStatus] = None
) extends Alertable {

  def alertParams: Map[String, String] = alertDetails.data

  @deprecated("We should remove this and replace with just rescindment", "28/7/15")
  def sentInError: Option[Boolean] = rescindment.map(_.`type` == GeneratedInError)

  override def alertTemplateName: String = alertDetails.templateId

  def source: Option[String] = externalRef.map(_.source)

  override def taxPayerName: Option[TaxpayerName] = alertDetails.recipientName

  override def auditData: Map[String, String] =
    body.map(_.toMap).getOrElse(Map.empty) ++ Map("messageId" -> id.toString)

  override def hardCopyAuditData: Map[String, String] =
    Map(
      "messageId" -> id.toString,
      "utr"       -> recipient.identifier.value,
      "validFrom" -> validFrom.toString
    ) ++ body.map(_.toMap).getOrElse(Map.empty)
}

object Message {
  import MessageMongoFormats._

  implicit val taxIdWithNameWrites = new Writes[TaxIdWithName] {
    override def writes(taxId: TaxIdWithName): JsValue =
      JsObject(Seq("name" -> JsString(taxId.name), "value" -> JsString(taxId.value)))
  }

  implicit val taxEntityWrites: Writes[TaxEntity] = Json.writes[TaxEntity]



}

case class ConversationItem(
  id: String,
  subject: String,
  body: Option[Details],
  validFrom: LocalDate,
  content: Option[String]
)

object ConversationItem {
   def apply(message: Message): ConversationItem =
    ConversationItem(
      message.id.toString,
      message.subject,
      message.body,
      message.validFrom,
      Some(Base64.encodeBase64String(message.content.getOrElse("").getBytes("UTF-8")))
    )
  implicit val messageListItemWrites = Json.writes[ConversationItem]
}

case class RenderUrl(service: String, url: String)
object RenderUrl {
  implicit val format: Format[RenderUrl] = Json.format[RenderUrl]
}

case class AlertDetails(templateId: String, recipientName: Option[TaxpayerName], data: Map[String, String])
object AlertDetails {
  implicit val format: Format[AlertDetails] = Json.format[AlertDetails]
}

final case class SendAlertResponse(sendAlert: Boolean)
object SendAlertResponse {
  implicit val format = Json.format[SendAlertResponse]
}

case class Adviser(pidId: String)
object Adviser {
  implicit val adviserFormat: Format[Adviser] = Json.format[Adviser]
}

case class Notification(count: Int, lastSent: DateTime)
object Notification {
  implicit def notificationFormat(implicit dtf: Format[DateTime]): Format[Notification] = Json.format[Notification]
}

case class LifecycleStatus(name: LifecycleStatusType, updated: DateTime)
object LifecycleStatus {
  implicit def lifecycleStatusFormat(implicit dtf: Format[DateTime]): Format[LifecycleStatus] =
    Json.format[LifecycleStatus]
}

case class Lifecycle(status: LifecycleStatus, startedAt: DateTime, notification: Option[Notification] = None)
object Lifecycle {
  implicit def lifecycleFormat(implicit dtf: Format[DateTime]): Format[Lifecycle] = Json.format[Lifecycle]
}

case class Details(
  form: Option[String],
  `type`: Option[String],
  suppressedAt: Option[String],
  detailsId: Option[String],
  paperSent: Option[Boolean] = None,
  batchId: Option[String] = None,
  issueDate: Option[LocalDate] = Some(LocalDate.now),
  replyTo: Option[String] = None,
  threadId: Option[ObjectId] = None,
  enquiryType: Option[String] = None,
  adviser: Option[Adviser] = None,
  waitTime: Option[String] = None,
  topic: Option[String] = None,
  envelopId: Option[String] = None,
  properties: Option[JsValue] = None
) {

  val paramsMap = Map(
    "formId"       -> form,
    "type"         -> `type`,
    "suppressedAt" -> suppressedAt,
    "detailsId"    -> detailsId,
    "paperSent"    -> paperSent,
    "batchId"      -> batchId,
    "issueDate"    -> issueDate,
    "replyTo"      -> replyTo,
    "threadId"     -> threadId.map(_.toString),
    "enquiryType"  -> enquiryType,
    "adviser"      -> adviser.map(_.pidId),
    "topic"        -> topic,
    "envelopId"    -> envelopId,
    "properties"   -> properties
  )

  val toMap = paramsMap.collect { case (key, Some(value)) => key -> value.toString }
}
object Details {
  import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.Implicits.objectIdFormat
  implicit val format: OFormat[Details] = Json.format[Details]
}

case class MessageStatus(
  envelopeId: Option[String],
  status: Option[LifecycleStatusType]
)

object MessageStatus {
  implicit val format: OFormat[MessageStatus] = Json.format[MessageStatus]
}

sealed trait MailgunStatus {
  val name = toString.toLowerCase
}

case object Delivered extends MailgunStatus

object MailgunStatus {
  implicit val reads: Reads[MailgunStatus] = new Reads[MailgunStatus] {
    override def reads(json: JsValue): JsResult[MailgunStatus] =
      json match {
        case JsString(status) if status == Delivered.name => JsSuccess(Delivered)
        case other                                        => JsError(s"Could not convert to ProcessingStatus from $other")
      }
  }

  implicit val writes: Writes[MailgunStatus] = new Writes[MailgunStatus] {
    override def writes(status: MailgunStatus): JsValue = JsString(status.name)
  }

}
