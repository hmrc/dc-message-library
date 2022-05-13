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

package uk.gov.hmrc.message.library.model

import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import reactivemongo.bson.BSONString

class ProcessingStatusSpec extends PlaySpec {
  import ProcessingStatus._
  "ProcessingStatus" must {
    "parse the Status to Json" in {
      Json.toJson[ProcessingStatus](ToDo) mustBe JsString("todo")
      Json.toJson[ProcessingStatus](InProgress) mustBe JsString("in-progress")
      Json.toJson[ProcessingStatus](Succeeded) mustBe JsString("succeeded")
      Json.toJson[ProcessingStatus](Deferred) mustBe JsString("deferred")
      Json.toJson[ProcessingStatus](Failed) mustBe JsString("failed")
      Json.toJson[ProcessingStatus](PermanentlyFailed) mustBe JsString("permanently-failed")
      Json.toJson[ProcessingStatus](Ignored) mustBe JsString("ignored")
      Json.toJson[ProcessingStatus](Duplicate) mustBe JsString("duplicate")
      Json.toJson[ProcessingStatus](Cancelled) mustBe JsString("cancelled")
      Json.toJson[ProcessingStatus](Delivered) mustBe JsString("delivered")
    }
    "read Json to ProcessingStatus" in {
      JsString("delivered").as[ProcessingStatus] mustBe Delivered
    }
    "return error if Json cannot be converted to ProcessingStatus" in {
      assertThrows[JsResultException] { JsString("unknown").as[ProcessingStatus] }
    }
    "convert BSONString to ProcessingStatus" in {
      BSONString("delivered").as[ProcessingStatus] mustBe Delivered
    }
  }
}