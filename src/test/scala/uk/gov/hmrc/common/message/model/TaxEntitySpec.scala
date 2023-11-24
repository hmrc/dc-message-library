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
    "produce vat regime from VRN taxId" in {
      TaxEntity.regimeOf(Vrn("123412342134")) mustBe Regime.vat
    }
    "produce vat regime from HmceVatdecOrg taxId" in {
      TaxEntity.regimeOf(HmceVatdecOrg("123412342134")) mustBe Regime.vat
    }
    "produce epaye regime from Epaye taxId" in {
      TaxEntity.regimeOf(Epaye("840Pd00123456")) mustBe Regime.epaye
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
    "produce ioss regime from HmrcIossOrg taxId" in {
      TaxEntity.regimeOf(HmrcIossOrg("XX9999999999")) mustBe Regime.ioss
    }
    "produce ppt regime from HmrcPptOrg taxId" in {
      TaxEntity.regimeOf(HmrcPptOrg("XMPPT0000000001")) mustBe Regime.ppt
    }
    "throw exception" in {
      val thrown = the[RuntimeException] thrownBy TaxEntity.regimeOf(HmrcObtdsOrg("foobar"))
      thrown.getMessage must include("unsupported identifier foobar")
    }
  }

  "getEnrolments" must {

    val testCases = List(
      ("IR-NINO", TaxEntity(Regime.paye, Nino("AB123456C"), None), "Nino", Enrolments("IR-NINO~NINO~AB123456C")),
      ("IR-SA", TaxEntity(Regime.sa, SaUtr("123412342134"), None), "SaUtr", Enrolments("IR-SA~UTR~123412342134")),
      ("IR-CT", TaxEntity(Regime.ct, CtUtr("123412342134"), None), "CtUtr", Enrolments("IR-CT~UTR~123412342134")),
      (
        "HMRC-MTD-VAT",
        TaxEntity(Regime.vat, HmrcMtdVat("123412342134"), None),
        "HmrcMtdVat",
        Enrolments("HMRC-MTD-VAT~VRN~123412342134", "HMRC-OSS-ORG~VRN~123412342134")),
      (
        "HMRC-MTD-VAT.VRN",
        TaxEntity(Regime.vat, Vrn("123412342134"), None),
        "Vrn",
        Enrolments("HMRC-MTD-VAT~VRN~123412342134", "HMRC-OSS-ORG~VRN~123412342134")),
      (
        "HMRC-VATDEC-ORG",
        TaxEntity(Regime.vat, HmceVatdecOrg("123412342134"), None),
        "HmceVatdecOrg",
        Enrolments("HMRC-VATDEC-ORG~VATREGNO~123412342134")),
      (
        "IR-PAYE",
        TaxEntity(Regime.epaye, Epaye("840Pd00123456"), None),
        "Epaye",
        Enrolments("IR-PAYE~EMPREF~840Pd00123456")),
      (
        "HMRC-OBTDS-ORG",
        TaxEntity(Regime.sdil, HmrcObtdsOrg("XZSD00000100024"), None),
        "HmrcObtdsOrg SDIL",
        Enrolments("HMRC-OBTDS-ORG~SD.ETMPREGISTRATIONNUMBER~XZSD00000100024")),
      (
        "HMRC-OBTDS-ORG",
        TaxEntity(Regime.fhdds, HmrcObtdsOrg("XZFH00000100024"), None),
        "HmrcObtdsOrg FHDDS",
        Enrolments("HMRC-OBTDS-ORG~FH.ETMPREGISTRATIONNUMBER~XZFH00000100024")),
      (
        "HMRC-CUS-ORG",
        TaxEntity(Regime.cds, HmrcCusOrg("GB123456789"), None),
        "HmrcCusOrg",
        Enrolments("HMRC-CUS-ORG~EORINUMBER~GB123456789")),
      (
        "HMRC-PPT-ORG",
        TaxEntity(Regime.ppt, HmrcPptOrg("XMPPT0000000001"), None),
        "HmrcPptOrg",
        Enrolments("HMRC-PPT-ORG~ETMPREGISTRATIONNUMBER~XMPPT0000000001")),
      (
        "HMRC-MTD-IT",
        TaxEntity(Regime.itsa, HmrcMtdItsa("GB123456789"), None),
        "HmrcMtdIt",
        Enrolments("HMRC-MTD-IT~MTDITID~GB123456789")),
      (
        "HMRC-PODS-ORG",
        TaxEntity(Regime.pods, HmrcPodsOrg("AB1231232344"), None),
        "HmrcPodsOrg",
        Enrolments("HMRC-PODS-ORG~PSAID~AB1231232344")),
      (
        "HMRC-PODSPP-ORG",
        TaxEntity(Regime.pods, HmrcPodsPpOrg("AB1231232344"), None),
        "HmrcPodsPpOrg",
        Enrolments("HMRC-PODSPP-ORG~PSPID~AB1231232344")),
      (
        "HMRC-IOSS-ORG",
        TaxEntity(Regime.ioss, HmrcIossOrg("AB1231232344"), None),
        "HmrcIossOrg",
        Enrolments("HMRC-IOSS-ORG~IOSSNumber~AB1231232344"))
    )

    testCases.foreach {
      case (to, taxEntity, from, expected) =>
        s"produce $to enrolment from $from tax entity" in {
          TaxEntity.getEnrolments(taxEntity) mustBe expected
        }
    }
    "throw exception" in {
      val thrown = the[RuntimeException] thrownBy TaxEntity.getEnrolments(TaxEntity(Regime.vat, HmrcObtdsOrg("foobar")))
      thrown.getMessage must include("unsupported tax entity")
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
      Epaye("840Pd00123456").toString mustBe "840Pd00123456"
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

  "HmrcIossOrg toString" must {
    "return value" in {
      HmrcIossOrg("XX9999999999").toString mustBe "XX9999999999"
    }
  }
}
