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
import uk.gov.hmrc.common.message.util.TestDataSample.{ TEST_TEMPLATE_ID, TEST_TEMPLATE_ID_MAP }

class AlertSpec extends PlaySpec {

  "alertFormat" should {
    import Alert.alertFormat

    "read the json correctly" in new Setup {
      Json.parse(alertJsonString).as[Alert] mustBe alert
    }

    "throw exception for the invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(alertInvalidJsonString).as[Alert]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(alert) mustBe Json.parse(alertJsonString)
    }
  }

  trait Setup {
    val alert: Alert = Alert(templateId = TEST_TEMPLATE_ID, parameters = Some(TEST_TEMPLATE_ID_MAP))

    val alertJsonString: String =
      """{"templateId":"test_template","parameters":{"SA309A_CY":"myTemplateId_cy","SA309A":"myTemplateId"}}""".stripMargin

    val alertInvalidJsonString: String =
      """{"parameters":{"SA309A_CY":"myTemplateId_cy","SA309A":"myTemplateId"}}""".stripMargin
  }
}
