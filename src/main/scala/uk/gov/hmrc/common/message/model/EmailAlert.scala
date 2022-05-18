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

import org.joda.time.DateTime
import play.api.libs.json._

// TODO it would be good to have different types for success and failure...
case class EmailAlert(
  emailAddress: Option[String],
  alertTime: DateTime,
  success: Boolean,
  failureReason: Option[String]
)

object EmailAlert {
  implicit def alertFormat(implicit dtf: Format[DateTime]): OFormat[EmailAlert] =
    Json.format[EmailAlert]
}
