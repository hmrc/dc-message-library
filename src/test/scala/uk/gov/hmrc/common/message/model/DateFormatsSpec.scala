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

package uk.gov.hmrc.common.message.model

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsString, Json }
import uk.gov.hmrc.common.message.util.TestDataSample.{ TEST_LOCAL_DATE, TEST_TIME_INSTANT }

import java.time.Instant

class DateFormatsSpec extends PlaySpec {

  "formatLocalDateWrites" should {
    import DateFormats.formatLocalDateWrites

    "write the input date correctly" in new Setup {
      Json.toJson(TEST_LOCAL_DATE)(formatLocalDateWrites("yyyy-MM-dd")) mustBe JsString("2025-11-28")
    }
  }

  "formatInstantReads" should {
    import DateFormats.formatInstantReads

    "read the valid Instant json correctly" ignore {
      val dateCorrector: (String => String) = (_: String) => "yyyy-MM-dd"

      Json.parse("20111203 10:15:30Z").as[Instant](formatInstantReads(dateCorrector)) mustBe TEST_TIME_INSTANT
    }
  }

  "formatInstantWrites" should {
    import DateFormats.formatInstantWrites

    "write the input date correctly" in new Setup {
      Json.toJson(TEST_TIME_INSTANT)(formatInstantWrites()) mustBe JsString("+3562062-11-03T15:12:14.000+0000")
    }
  }

  trait Setup {}
}
