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

    "threadId if supplied" should {
      "be a valid hex string" in {
        val json =
          Json.obj(
            "formId"   -> "formId",
            "threadId" -> "5c85a5000000000000000001"
          )
        json.as[MessageDetails].threadId.get mustBe "5c85a5000000000000000001"
      }
    }

    "issueDate" should {
      "be serialized" in {
        val localDate = "2022-10-10"
        val json = s"""{
                      |      "formId":"formId",
                      |       "issueDate":"$localDate"
                      |   }""".stripMargin
        Json.parse(json).as[MessageDetails].issueDate.get.toString() mustBe "2022-10-10"
      }
    }
  }

  "fail validation if its not valid hash string" in {
    val json =
      Json.obj(
        "formId"   -> "formId",
        "threadId" -> "some invalid hash"
      )

    the[IllegalArgumentException] thrownBy (json
      .as[MessageDetails]) must have message "requirement failed: threadId has invalid format"

  }
}
