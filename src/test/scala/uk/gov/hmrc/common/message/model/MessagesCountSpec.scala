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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsResultException, Json }
import uk.gov.hmrc.common.message.util.TestData.{ FIVE, SIX, THREE, TWO }

class MessagesCountSpec extends PlaySpec {

  "(+)" must {

    "add both read and unread when + is applied" in {
      MessagesCount(1, 0) + MessagesCount(FIVE, 0) mustBe MessagesCount(SIX, 0)
      MessagesCount(0, 1) + MessagesCount(0, TWO) mustBe MessagesCount(0, THREE)
      MessagesCount(1, 1) + MessagesCount(FIVE, TWO) mustBe MessagesCount(SIX, THREE)
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

  "formatMessageCount" should {
    import MessagesCount.formatMessageCount

    "read the json correctly" in new Setup {
      Json.parse(msgCountJsonString).as[MessagesCount] mustBe msgCount
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(msgCountInvalidJsonString).as[MessagesCount]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(msgCount) mustBe Json.parse(msgCountJsonString)
    }
  }

  trait Setup {
    val msgCount: MessagesCount = MessagesCount(total = FIVE, unread = TWO)

    val msgCountJsonString: String = """{"total":5,"unread":2}""".stripMargin
    val msgCountInvalidJsonString: String = """{"unread":2}""".stripMargin
  }
}
