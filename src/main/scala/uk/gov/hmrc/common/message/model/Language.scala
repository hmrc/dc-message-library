/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json._

enum Language(val entryName: String) {
  case English extends Language("en")
  case Welsh extends Language("cy")
}

object Language {
  implicit val format: Format[Language] = new Format[Language] {
    def reads(json: JsValue): JsResult[Language] = json match {
      case JsString("cy") | JsString("CY") => JsSuccess(Welsh)
      case _                               => JsSuccess(English)
    }

    def writes(language: Language): JsValue = JsString(language.entryName)
  }
}
