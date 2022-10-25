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

import org.joda.time.LocalDate
import org.mongodb.scala.bson.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats
//import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats.Implicits.jotLocalDateFormat
//import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.Implicits.objectIdFormat._
import scala.util.Try

case class MessageDetails(
  formId: String,
  statutoryOp: Option[Boolean],
  paperSentOp: Option[Boolean],
  sourceData: Option[String],
  batchId: Option[String],
  issueDate: Option[LocalDate] = Some(LocalDate.now),
  replyTo: Option[String],
  threadId: Option[String] = Some((new ObjectId().toString)),
  enquiryType: Option[String] = None,
  adviser: Option[Adviser] = None,
  waitTime: Option[String] = None,
  topic: Option[String] = None,
  properties: Option[JsValue] = None
) {

  require(
    if (threadId.nonEmpty) {
      Try(new ObjectId(threadId.getOrElse(""))).isSuccess
    } else { true },
    "threadId has invalid format"
  )

  def statutory: Boolean = statutoryOp.getOrElse(false)
  def paperSent: Boolean = paperSentOp.getOrElse(false)

}

object MessageDetails {
  import play.api.libs.json.JodaReads.DefaultJodaLocalDateReads
  import play.api.libs.json.JodaWrites.{ JodaDateTimeWrites => _, _ }
  implicit val objectIdFormats = MongoFormats.objectIdFormat
  val reads: Reads[MessageDetails] =
    ((__ \ "formId").read[String] and
      (__ \ "statutory").readNullable[Boolean] and
      (__ \ "paperSent").readNullable[Boolean] and
      (__ \ "sourceData").readNullable[String] and
      (__ \ "batchId").readNullable[String] and
      (__ \ "issueDate").readNullable[LocalDate] and
      (__ \ "replyTo").readNullable[String] and
      (__ \ "threadId").readNullable[String] and
      (__ \ "enquiryType").readNullable[String] and
      (__ \ "adviser").readNullable[Adviser] and
      (__ \ "waitTime").readNullable[String] and
      (__ \ "topic").readNullable[String] and
      Reads[Option[JsValue]](jsValue => JsSuccess((__ \ "properties").asSingleJson(jsValue).toOption)))(
      MessageDetails.apply _)

  implicit val format = Format(reads, Json.writes[MessageDetails])
}
