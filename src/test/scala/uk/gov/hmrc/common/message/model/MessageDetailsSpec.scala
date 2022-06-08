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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class MessageDetailsSpec extends PlaySpec {

  "MessageDetails" must {

    "deserialise valid properties" in {

      val randomJsValue = Json.obj("randomKey" -> "randomValue")
      val json =
        Json.obj(
          "formId"     -> "formId",
          "properties" -> randomJsValue
        )

      json.as[MessageDetails].properties mustBe Some(randomJsValue)
    }

    "set properties to None, when not provided" in {
      val json =
        Json.obj(
          "formId" -> "formId"
        )

      json.as[MessageDetails].properties mustBe None

    }
  }

}
