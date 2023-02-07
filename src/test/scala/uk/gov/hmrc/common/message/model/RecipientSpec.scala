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
import play.api.libs.json._
import uk.gov.hmrc.common.message.model.TaxEntity.{ Epaye, HmrcPodsOrg, HmrcPodsPpOrg, HmrcPptOrg }
import uk.gov.hmrc.domain._

class RecipientSpec extends PlaySpec {

  "Regime deserialisation" must {

    "work with valid paye value" in {
      JsString("paye").asOpt[Regime.Value].value mustBe Regime.paye
    }
    "work with valid sa value" in {
      JsString("sa").asOpt[Regime.Value].value mustBe Regime.sa
    }
    "work with valid fhdds value" in {
      JsString("fhdds").asOpt[Regime.Value].value mustBe Regime.fhdds
    }
    "work with valid vat value" in {
      JsString("vat").asOpt[Regime.Value].value mustBe Regime.vat
    }
    "work with valid epaye value" in {
      JsString("epaye").asOpt[Regime.Value].value mustBe Regime.epaye
    }
    "work with valid sdil value" in {
      JsString("sdil").asOpt[Regime.Value].value mustBe Regime.sdil
    }
    "work with valid itsa value" in {
      JsString("itsa").asOpt[Regime.Value].value mustBe Regime.itsa
    }
    "work with invalid value" in {
      implicitly[Reads[Regime.Value]].reads(JsString("invalid-regime")) mustBe JsError(
        Seq(JsPath() -> Seq(JsonValidationError("error.expected.validenumvalue"))))
    }

  }

  "Regime Json serialisation" must {

    "serialise Regime.paye to JsString" in {
      Json.toJson(Regime.paye) mustBe JsString("paye")
    }
    "serialise Regime.sa to JsString" in {
      Json.toJson(Regime.sa) mustBe JsString("sa")
    }
    "serialise Regime.fhdds to JsString" in {
      Json.toJson(Regime.fhdds) mustBe JsString("fhdds")
    }
    "serialise Regime.vat to JsString" in {
      Json.toJson(Regime.vat) mustBe JsString("vat")
    }
    "serialise Regime.epaye to JsString" in {
      Json.toJson(Regime.epaye) mustBe JsString("epaye")
    }
    "serialise Regime.sdil to JsString" in {
      Json.toJson(Regime.sdil) mustBe JsString("sdil")
    }
    "serialise Regime.itsa to JsString" in {
      Json.toJson(Regime.itsa) mustBe JsString("itsa")
    }

  }

  "Recipient deserialisation" must {

    "work with valid recipient" in {
      val recipient = Json.parse("""{
                                   |       "taxIdentifier":{
                                   |           "name":"HMRC-OBTDS-ORG",
                                   |           "value":"XZFH00000100024"
                                   |       },
                                   |       "regime":"fhdds"

       }""".stripMargin).as[Recipient]
      recipient mustBe Recipient(
        taxIdentifier = HmrcObtdsOrg("XZFH00000100024"),
        name = None,
        regime = Some(Regime.fhdds))
    }

    "work with valid recipient for IR-PAYE" in {
      val recipient = Json.parse("""{
                                   |       "taxIdentifier":{
                                   |           "name":"IR-PAYE.EMPREF",
                                   |           "value":"000AB12345"
                                   |       },
                                   |       "regime":"epaye"

       }""".stripMargin).as[Recipient]
      recipient mustBe Recipient(taxIdentifier = Epaye("000AB12345"), name = None, regime = Some(Regime.epaye))
    }

    "return error for an invalid IR-PAYE EMPREF value" in {
      val error = intercept[JsResultException] { Json.parse("""{
                                                              |       "taxIdentifier":{
                                                              |           "name":"IR-PAYE.EMPREF",
                                                              |           "value":"AB12345"
                                                              |       },
                                                              |       "regime":"epaye"
       }""".stripMargin).as[Recipient] }

      error.errors.head._2.head.message mustBe "The backend has rejected the message due to an invalid EMPREF value - AB12345"
    }

    "return error for a missing IR-PAYE EMPREF value" in {
      val error = intercept[JsResultException] { Json.parse("""{
                                                              |       "taxIdentifier":{
                                                              |           "name":"IR-PAYE.EMPREF"
                                                              |       },
                                                              |       "regime":"epaye"
       }""".stripMargin).as[Recipient] }

      error.errors.head._2.head.message mustBe "The backend has rejected the message due to a missing tax identifier value."
    }

    "return error for a missing IR-PAYE EMPREF name" in {
      val error = intercept[JsResultException] { Json.parse("""{
                                                              |       "taxIdentifier":{
                                                              |           "value":"000AB12345"
                                                              |       },
                                                              |       "regime":"epaye"
       }""".stripMargin).as[Recipient] }

      error.errors.head._2.head.message mustBe "The backend has rejected the message due to a missing tax identifier name."
    }

    "return error for an unknown tax identifier name" in {
      val error = intercept[JsResultException] { Json.parse("""{
                                                              |       "taxIdentifier":{
                                                              |           "name":"IR-UNKNOWN",
                                                              |           "value":"AB12345"
                                                              |       },
                                                              |       "regime":"epaye"
       }""".stripMargin).as[Recipient] }

      error.errors.head._2.head.message mustBe "The backend has rejected the message due to an unknown tax identifier."
    }

    "Work for IR-PAYEs - case insensitive" in {
      val recipient: Seq[Recipient] = List("EMPREF", "empref", "EmpRef").map { ref =>
        Json.parse(s"""{
                      |       "taxIdentifier":{
                      |           "name":"IR-PAYE.$ref",
                      |           "value":"840Pd00123456"
                      |       },
                      |       "regime":"epaye"

       }""".stripMargin).as[Recipient]
      }
      recipient mustBe List(
        Recipient(taxIdentifier = Epaye("840Pd00123456"), name = None, regime = Some(Regime.epaye)),
        Recipient(taxIdentifier = Epaye("840Pd00123456"), name = None, regime = Some(Regime.epaye)),
        Recipient(taxIdentifier = Epaye("840Pd00123456"), name = None, regime = Some(Regime.epaye))
      )
    }

    "Work for HMRC-PPT-ORGs - case insensitive" in {
      val recipient: Seq[Recipient] =
        List("ETMPREGISTRATIONNUMBER", "etmpregistrationnumber", "EtmpRegistrationNumber").map { ref =>
          Json.parse(s"""{
                        |       "taxIdentifier":{
                        |           "name":"HMRC-PPT-ORG.$ref",
                        |           "value":"XMPPT0000000001"
                        |       },
                        |       "regime":"ppt"

       }""".stripMargin).as[Recipient]
        }
      recipient mustBe List(
        Recipient(taxIdentifier = HmrcPptOrg("XMPPT0000000001"), name = None, regime = Some(Regime.ppt)),
        Recipient(taxIdentifier = HmrcPptOrg("XMPPT0000000001"), name = None, regime = Some(Regime.ppt)),
        Recipient(taxIdentifier = HmrcPptOrg("XMPPT0000000001"), name = None, regime = Some(Regime.ppt))
      )
    }

    "Work for HMRC-PODS-ORGs - case insensitive" in {
      val recipient: Seq[Recipient] = List("PSAID", "psaid", "PsaId").map { ref =>
        Json.parse(s"""{
                      |       "taxIdentifier":{
                      |           "name":"HMRC-PODS-ORG.$ref",
                      |           "value":"A2100006"
                      |       },
                      |       "regime":"pods"

       }""".stripMargin).as[Recipient]
      }
      recipient mustBe List(
        Recipient(taxIdentifier = HmrcPodsOrg("A2100006"), name = None, regime = Some(Regime.pods)),
        Recipient(taxIdentifier = HmrcPodsOrg("A2100006"), name = None, regime = Some(Regime.pods)),
        Recipient(taxIdentifier = HmrcPodsOrg("A2100006"), name = None, regime = Some(Regime.pods))
      )
    }

    "Work for HMRC-PODSPP-ORGs - case insensitive" in {
      val recipient: Seq[Recipient] = List("PSPID", "pspid", "PspId").map { ref =>
        Json.parse(s"""{
                      |       "taxIdentifier":{
                      |           "name":"HMRC-PODSPP-ORG.$ref",
                      |           "value":"A2100006"
                      |       },
                      |       "regime":"pods"

       }""".stripMargin).as[Recipient]
      }
      recipient mustBe List(
        Recipient(taxIdentifier = HmrcPodsPpOrg("A2100006"), name = None, regime = Some(Regime.pods)),
        Recipient(taxIdentifier = HmrcPodsPpOrg("A2100006"), name = None, regime = Some(Regime.pods)),
        Recipient(taxIdentifier = HmrcPodsPpOrg("A2100006"), name = None, regime = Some(Regime.pods))
      )
    }
  }
}
