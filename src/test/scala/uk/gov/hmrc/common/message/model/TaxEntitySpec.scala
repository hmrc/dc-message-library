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
import uk.gov.hmrc.domain._

import uk.gov.hmrc.common.message.model.TaxEntity._
import uk.gov.hmrc.common.message.util.MessageFixtures

class TaxEntitySpec extends PlaySpec {
  "forAudit" must {

    "correctly map TaxIdWithName" in {
      val utr = GenerateRandom.utr()
      forAudit(MessageFixtures.createTaxEntity(utr)) mustBe Map("sautr" -> utr.value)
    }
    "correctly map TaxIdWithName for CtUtr" in {
      val utr = CtUtr("123456")
      forAudit(MessageFixtures.createTaxEntity(utr)) mustBe Map("ctutr" -> utr.value)
    }
  }

  "regimeOf" must {

    "produce paye regime from Nino taxId" in {
      TaxEntity.regimeOf(Nino("AB123456C")) mustBe Regime.paye
    }
    "produce sa regime from SaUtr taxId" in {
      TaxEntity.regimeOf(SaUtr("123412342134")) mustBe Regime.sa
    }
    "produce ct regime from CtUtr taxId" in {
      TaxEntity.regimeOf(CtUtr("123412342134")) mustBe Regime.ct
    }
    "produce vat regime from HmrcMtdVat taxId" in {
      TaxEntity.regimeOf(HmrcMtdVat("123412342134")) mustBe Regime.vat
    }
    "produce vat regime from HmceVatdecOrg taxId" in {
      TaxEntity.regimeOf(HmceVatdecOrg("123412342134")) mustBe Regime.vat
    }
    "produce epaye regime from Epaye taxId" in {
      TaxEntity.regimeOf(Epaye("840/Pd00123456")) mustBe Regime.epaye
    }
    """produce sdil regime from HmrcObtdsOrg taxId and "SD" in the 3-d and 4-th charaters of the value""" in {
      TaxEntity.regimeOf(HmrcObtdsOrg("XZSD00000100024")) mustBe Regime.sdil
    }
    """produce fhdds regime from HmrcObtdsOrg taxId and "FH" in the 3-d and 4-th charaters of the value""" in {
      TaxEntity.regimeOf(HmrcObtdsOrg("XZFH00000100024")) mustBe Regime.fhdds
    }
    "produce itsa regime from HmrcMtdItsa taxId" in {
      TaxEntity.regimeOf(HmrcMtdItsa("X99999999999")) mustBe Regime.itsa
    }
    "produce ppt regime from HmrcPptOrg taxId" in {
      TaxEntity.regimeOf(HmrcPptOrg("XMPPT0000000001")) mustBe Regime.ppt
    }
    "throw exception" in {
      val thrown = the[RuntimeException] thrownBy TaxEntity.regimeOf(HmrcObtdsOrg("foobar"))
      thrown.getMessage must include("unsupported identifier foobar")
    }
  }

  "getEnrolment" must {

    "produce IR-NINO enrolment from Nino tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.paye, Nino("AB123456C"), None)) mustBe "IR-NINO~NINO~AB123456C"
    }
    "produce IR-SA enrolment from SaUtr tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.sa, SaUtr("123412342134"), None)) mustBe "IR-SA~UTR~123412342134"
    }
    "produce IR-CT enrolment from CtUtr tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.ct, CtUtr("123412342134"), None)) mustBe "IR-CT~UTR~123412342134"
    }
    "produce HMRC-MTD-VAT enrolment from HmrcMtdVat tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.vat, HmrcMtdVat("123412342134"), None)) mustBe "HMRC-MTD-VAT~VRN~123412342134"
    }
    "produce HMRC-VATDEC-ORG enrolment from HmceVatdecOrg tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.vat, HmceVatdecOrg("123412342134"), None)) mustBe "HMRC-VATDEC-ORG~VATREGNO~123412342134"
    }
    "produce IR-PAYE enrolment from Epaye tax entity" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.epaye, Epaye("840/Pd00123456"), None)) mustBe "IR-PAYE~ACCOUNTSREF~840/Pd00123456"
    }
    """produce HMRC-OBTDS-ORG enrolment from HmrcObtdsOrg SDIL tax entity""" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.sdil, HmrcObtdsOrg("XZSD00000100024"), None)) mustBe "HMRC-OBTDS-ORG~SD.ETMPREGISTRATIONNUMBER~XZSD00000100024"
    }
    """produce HMRC-OBTDS-ORG enrolment from HmrcObtdsOrg FHDDS tax entity""" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.fhdds, HmrcObtdsOrg("XZFH00000100024"), None)) mustBe "HMRC-OBTDS-ORG~FH.ETMPREGISTRATIONNUMBER~XZFH00000100024"
    }
    """produce HMRC-CUS-ORG enrolment from HmrcCusOrg tax entity""" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.cds, HmrcCusOrg("GB123456789"), None)) mustBe "HMRC-CUS-ORG~EORINUMBER~GB123456789"
    }
    """produce HMRC-PPT-ORG enrolment from HmrcPptOrg tax entity""" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.ppt, HmrcPptOrg("XMPPT0000000001"), None)) mustBe "HMRC-PPT-ORG~ETMPREGISTRATIONNUMBER~XMPPT0000000001"
    }
    """produce HMRC-MTD-IT enrolment from HmrcMtdIt tax entity""" in {
      TaxEntity.getEnrolment(TaxEntity(Regime.itsa, HmrcMtdItsa("GB123456789"), None)) mustBe "HMRC-MTD-IT~MTDITID~GB123456789"
    }
    "throw exception" in {
      val thrown = the[RuntimeException] thrownBy TaxEntity.getEnrolment(TaxEntity(Regime.vat, HmrcObtdsOrg("foobar")))
      thrown.getMessage must include("unsupported regime")
    }
  }

  "create" must {

    "construct TaxEntity when regime is None" in {
      TaxEntity.create(identifier = Nino("AB123456C"), regime = None) mustBe TaxEntity(Regime.paye, Nino("AB123456C"))
    }
    "construct TaxEntity when regime matches identifier's one" in {
      TaxEntity.create(identifier = Nino("AB123456C"), regime = Some(Regime.paye)) mustBe TaxEntity(
        Regime.paye,
        Nino("AB123456C"))
    }
    "throw exception when regime doesn't match identifier's one" in {
      val thrown = the[RuntimeException] thrownBy TaxEntity
        .create(identifier = Nino("AB123456C"), regime = Some(Regime.sa))
      thrown.getMessage must include("unmatched regimes: sa and paye")

    }
  }

  "Epaye toString" must {
    "return value" in {
      Epaye("840/Pd00123456").toString mustBe "840/Pd00123456"
    }
    "throw exception if value invalid" in {
      intercept[IllegalArgumentException] {
        Epaye("840Pd0012345").toString
      }.getMessage must be("requirement failed: failed to validate 840Pd0012345")
    }
  }

  "HmceVatdecOrg toString" must {
    "return value" in {
      HmceVatdecOrg("123412342134").toString mustBe "123412342134"
    }
  }

  "HmrcCusOrg toString" must {
    "return value" in {
      HmrcCusOrg("GB123456789").toString mustBe "GB123456789"
    }
  }

  "HmrcPptOrg toString" must {
    "return value" in {
      HmrcPptOrg("XMPPT0000000001").toString mustBe "XMPPT0000000001"
    }
  }

  "HmrcMtdIt toString" must {
    "return value" in {
      HmrcMtdItsa("X99999999999").toString mustBe "X99999999999"
    }
  }
}
