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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ExternalRef(id: String, source: String)

object ExternalRef {
  implicit val reads =
    ((__ \ "id").readNullable[String] and (__ \ "source").readNullable[String]).tupled.flatMap[ExternalRef] {
      case (Some(id), Some(source)) if !(id.isEmpty || source.isEmpty) =>
        Reads[ExternalRef] { _ =>
          JsSuccess(ExternalRef(id, source))
        }
      case _ =>
        Reads[ExternalRef] { _ =>
          JsError("Missing or empty externalRef id or source")
        }
    }

  implicit val writes = Json.writes[ExternalRef]
}
