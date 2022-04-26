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

package uk.gov.hmrc.model

import org.joda.time.LocalDate
import play.api.libs.functional.syntax._
import play.api.libs.json.JodaWrites.{JodaDateTimeWrites => _}
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.json.BSONObjectIdFormats

case class MessageDetails(
  formId: String,
  statutoryOp: Option[Boolean],
  paperSentOp: Option[Boolean],
  sourceData: Option[String],
  batchId: Option[String],
  issueDate: Option[LocalDate] = Some(LocalDate.now),
  replyTo: Option[String],
  threadId: Option[BSONObjectID] = Some(BSONObjectID.generate),
  enquiryType: Option[String] = None,
  adviser: Option[Adviser] = None,
  waitTime: Option[String] = None,
  topic: Option[String] = None,
  properties: Option[JsValue] = None
) {
  def statutory: Boolean = statutoryOp.getOrElse(false)
  def paperSent: Boolean = paperSentOp.getOrElse(false)

}

object MessageDetails extends BSONObjectIdFormats {

  val reads: Reads[MessageDetails] =
    ((__ \ "formId").read[String] and
      (__ \ "statutory").readNullable[Boolean] and
      (__ \ "paperSent").readNullable[Boolean] and
      (__ \ "sourceData").readNullable[String] and
      (__ \ "batchId").readNullable[String] and
      (__ \ "issueDate").readNullable[LocalDate](jodaDateReads("issueDate")) and
      (__ \ "replyTo").readNullable[String] and
      (__ \ "threadId").readNullable[BSONObjectID] and
      (__ \ "enquiryType").readNullable[String] and
      (__ \ "adviser").readNullable[Adviser] and
      (__ \ "waitTime").readNullable[String] and
      (__ \ "topic").readNullable[String] and
      Reads[Option[JsValue]](jsValue => JsSuccess((__ \ "properties").asSingleJson(jsValue).toOption)))(
      MessageDetails.apply _)

  implicit val format = Format(reads, Json.writes[MessageDetails])
}
