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

class FormIdsSpec extends PlaySpec {

  "ITSA_FORM_IDS_EN" should {

    "return correct list of itsa form ids for English in lowercase" in {
      FormIds.ITSA_FORM_IDS_EN mustBe List("lpp1a_itsa", "lpp1b_itsa", "lpp2_itsa", "lpp4_itsa", "par1_itsa")
    }
  }

  "ITSA_FORM_IDS_CY" should {

    "return correct list of itsa form ids for Welsh in lowercase" in {
      FormIds.ITSA_FORM_IDS_CY mustBe List(
        "lpp1a_itsa_cy",
        "lpp1b_itsa_cy",
        "lpp2_itsa_cy",
        "lpp4_itsa_cy",
        "par1_itsa_cy"
      )
    }
  }
}
