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

package uk.gov.hmrc.common.message

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{ JsValue, Reads }

import scala.util.{ Failure, Success, Try }

package object model {

  type ContentParameters = JsValue
  val defaultJodaDateFormat = "yyyy-MM-dd"

  def formatDate(date: LocalDate): String =
    "%04d-%02d-%02d".format(date.getYear, date.getMonthOfYear, date.getDayOfMonth)

  def jodaDateReads: Reads[LocalDate] =
    Reads[LocalDate](
      js =>
        js.validate[String]
          .map[LocalDate](dtString =>
            Try {
              LocalDate.parse(dtString, DateTimeFormat.forPattern(defaultJodaDateFormat))
            } match {
              case Success(reads) => reads
              case Failure(_)     => throw DateValidationException("Invalid date format provided")
          }))
}
case class DateValidationException(message: String) extends RuntimeException(message)
