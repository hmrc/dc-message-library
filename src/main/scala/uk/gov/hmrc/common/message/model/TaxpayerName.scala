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

import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsPath, _ }

case class TaxpayerName(
  title: Option[String] = None,
  forename: Option[String] = None,
  secondForename: Option[String] = None,
  surname: Option[String] = None,
  honours: Option[String] = None,
  line1: Option[String] = None,
  line2: Option[String] = None,
  line3: Option[String] = None
) {

  def asMap: Map[String, String] =
    Seq(
      title.map("title"                   -> _),
      forename.map("forename"             -> _),
      secondForename.map("secondForename" -> _),
      surname.map("surname"               -> _),
      honours.map("honours"               -> _),
      line1.map(l1 => if (l1.trim.isEmpty) "line1" -> "Customer" else "line1" -> l1),
      line2.map("line2" -> _),
      line3.map("line3" -> _)
    ).flatten.flatMap {
      case (nameTitle, nameValue) if nameValue.trim.nonEmpty =>
        Some(TaxpayerName.PREFIX ++ nameTitle -> nameValue.trim)
      case _ => None
    }.toMap

  def withDefaultLine1: TaxpayerName = copy(line1 = line1.orElse(Some("Customer")))

}

object TaxpayerName {
  val PREFIX = "recipientName_"

  val reads: Reads[TaxpayerName] = (
    (JsPath \ "title").readNullable[String] and
      (JsPath \ "forename").readNullable[String] and
      (JsPath \ "secondForename").readNullable[String] and
      (JsPath \ "surname").readNullable[String] and
      (JsPath \ "honours").readNullable[String] and
      (JsPath \ "line1").readNullable[String].map(_.map(x => x.trim)) and
      (JsPath \ "line2").readNullable[String].map(_.map(x => x.trim)) and
      (JsPath \ "line3").readNullable[String].map(_.map(x => x.trim))
  )(TaxpayerName.apply _)

  implicit val taxpayerNameFormat: Format[TaxpayerName] = Format(reads, Json.writes[TaxpayerName])
}
