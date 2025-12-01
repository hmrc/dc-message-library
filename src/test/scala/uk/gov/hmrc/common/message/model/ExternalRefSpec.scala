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
import play.api.libs.json.{ JsResultException, Json }
import uk.gov.hmrc.common.message.util.TestData.{ TEST_ID, TEST_SOURCE_GMC }

class ExternalRefSpec extends PlaySpec {

  "Json Reads" should {
    import ExternalRef.reads

    "read the json correctly" in new Setup {
      Json.parse(externalRefJsonString).as[ExternalRef] mustBe externalRef
    }

    "throw exception for invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(externalRefInvalidJsonString).as[ExternalRef]
      }
    }
  }

  "Json Writes" should {
    "write the object correctly" in new Setup {
      Json.toJson(externalRef) mustBe Json.parse(externalRefJsonString)
    }
  }

  trait Setup {
    val externalRef: ExternalRef = ExternalRef(id = TEST_ID, source = TEST_SOURCE_GMC)

    val externalRefJsonString: String = """{"id":"test_id","source":"gmc"}""".stripMargin
    val externalRefInvalidJsonString: String = """{"source":"gmc"}""".stripMargin
  }
}
