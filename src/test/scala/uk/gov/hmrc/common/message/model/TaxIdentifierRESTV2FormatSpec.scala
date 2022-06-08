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

import org.scalatest.Inside
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import uk.gov.hmrc.domain.TaxIds._
import uk.gov.hmrc.domain._
import uk.gov.hmrc.common.message.model.TaxEntity.{ Epaye, HmceVatdecOrg, HmrcCusOrg, HmrcPptOrg }
import uk.gov.hmrc.common.message.model.TaxIdentifierRESTV2Formats._

class TaxIdentifierRESTV2FormatSpec extends PlaySpec {

  "JSON deserialisation" must {

    "Work for SA UTRs" in {
      val json = Json.parse("""{"name": "sautr", "value": "1234554321"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(SaUtr("1234554321"))
    }

    "Work for NINO" in {
      val json = Json.parse("""{"name": "nino", "value": "CS700100A"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(Nino("CS700100A"))
    }

    "Work for CT UTRs" in {
      val json = Json.parse("""{"name": "ctutr", "value": "123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(CtUtr("123456789"))
    }

    "Work for HMRC-OBTDS-ORGs" in {
      val json = Json.parse("""{"name": "HMRC-OBTDS-ORG", "value": "123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcObtdsOrg("123456789"))
    }

    "Work for HMRC-MTD-VATs" in {
      val json = Json.parse("""{"name": "HMRC-MTD-VAT", "value": "123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcMtdVat("123456789"))
    }

    "Work for empRefs" in {
      val json = Json.parse("""{"name": "empRef", "value": "12345/6789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(Epaye("12345/6789"))
    }

    "Work for HmceVatdecOrg" in {
      val json = Json.parse("""{"name": "HMCE-VATDEC-ORG", "value": "12345"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmceVatdecOrg("12345"))
    }

    "Work for HMRC-CUS-ORGs" in {
      val json = Json.parse("""{"name": "HMRC-CUS-ORG", "value": "GB123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcCusOrg("GB123456789"))
    }

    "Work for HMRC-PPT-ORGs" in {
      val json = Json.parse("""{"name": "HMRC-PPT-ORG.ETMPREGISTRATIONNUMBER", "value": "XMPPT0000000001"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcPptOrg("XMPPT0000000001"))
    }

    "Work for HMRC-MTD-ITs" in {
      val json = Json.parse("""{"name": "HMRC-MTD-IT", "value": "GB123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcMtdItsa("GB123456789"))
    }

    "Work for MTDITIDs" in {
      val json = Json.parse("""{"name": "MTDITID", "value": "GB123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcMtdItsa("GB123456789"))
    }

    "Work for MTDBSA" in {
      val json = Json.parse("""{"name": "MTDBSA", "value": "GB123456789"}""")
      Json.fromJson[TaxIdWithName](json) mustBe JsSuccess(HmrcMtdItsa("GB123456789"))
    }

    "Fail for an unknown identifier type" in {
      val json = Json.parse("""{"name": "vrn", "value": "1234554321"}""")
      val outcome = Json.fromJson[TaxIdWithName](json)

      Inside.inside(outcome) {
        case JsError(errors) =>
          errors.head._2.head mustBe JsonValidationError(
            """The backend has rejected the message due to an unknown tax identifier."""
          )
      }
    }
  }

  "JSON serialisation" must {
    "Works for TaxIdWithName " in {
      val saUtr: TaxIdWithName = GenerateRandom.utrGenerator.nextSaUtr
      val expectedResult = Json.obj("sautr" -> saUtr.value)
      Json.toJson(saUtr)(identifierWrites) mustBe expectedResult
    }
  }

  "Input validation" must {
    "fail for null tax identifier value" in {
      val taxId = Json.parse("""{"name": "sautr", "value": null}""")
      val thrown = the[JsResultException] thrownBy taxId.as[TaxIdWithName]
      thrown.getMessage must include("The backend has rejected the message due to an unknown tax identifier.")
    }

    "fail for missing tax identifier value" in {
      val taxId = Json.parse("""{"name": "sautr"}""")
      val thrown = the[JsResultException] thrownBy taxId.as[TaxIdWithName]
      thrown.getMessage must include("The backend has rejected the message due to an unknown tax identifier.")
    }

    "fail for null tax identifier name" in {
      val taxId = Json.parse("""{"name": null, "value": "123"}""")
      val thrown = the[JsResultException] thrownBy taxId.as[TaxIdWithName]
      thrown.getMessage must include("The backend has rejected the message due to an unknown tax identifier.")
    }

    "fail for missing tax identifier name" in {
      val taxId = Json.parse("""{"value": "123"}""")
      val thrown = the[JsResultException] thrownBy taxId.as[TaxIdWithName]
      thrown.getMessage must include("The backend has rejected the message due to an unknown tax identifier.")
    }

  }
}
