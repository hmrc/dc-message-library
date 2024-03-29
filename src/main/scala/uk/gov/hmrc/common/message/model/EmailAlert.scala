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

import java.time.Instant
import play.api.libs.json._

// TODO it would be good to have different types for success and failure...
case class EmailAlert(
  emailAddress: Option[String],
  alertTime: Instant,
  success: Boolean,
  failureReason: Option[String]
)

object EmailAlert {
  def success(emailAddress: String): EmailAlert =
    EmailAlert(Some(emailAddress), SystemTimeSource.now(), success = true, None)

  def failure(failureReason: String): EmailAlert =
    EmailAlert(None, SystemTimeSource.now(), success = false, Some(failureReason))

  implicit def alertFormat(implicit dtf: Format[Instant]): OFormat[EmailAlert] =
    Json.format[EmailAlert]
}
