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
import play.api.libs.json.Json

class MessageCountSpec extends PlaySpec {

  "MessageCount" must {

    "add both read and unread when + is applied" in {
      MessagesCount(1, 0) + MessagesCount(5, 0) mustBe MessagesCount(6, 0)
      MessagesCount(0, 1) + MessagesCount(0, 2) mustBe MessagesCount(0, 3)
      MessagesCount(1, 1) + MessagesCount(5, 2) mustBe MessagesCount(6, 3)
    }

    "be the same when adding empty" in {
      MessagesCount(1, 0) + MessagesCount.empty mustBe MessagesCount(1, 0)
      MessagesCount(0, 1) + MessagesCount.empty mustBe MessagesCount(0, 1)
      MessagesCount(1, 1) + MessagesCount.empty mustBe MessagesCount(1, 1)
    }

    "serialise to json" in {
      val result = Json.toJson(MessagesCount(1, 0))
      val expectedResult = Json.obj("total" -> 1, "unread" -> 0)
      result mustBe expectedResult
    }
  }
}
