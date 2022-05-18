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

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.domain._
import TaxEntity.{ Epaye, HmceVatdecOrg, HmrcCusOrg, HmrcPodsOrg, HmrcPodsPpOrg, HmrcPptOrg }

sealed trait Regime

object Regime extends Enumeration {
  type Regime = Value
  val paye, sa, ct, fhdds, vat, epaye, sdil, cds, itsa, ppt, pods = Value

  implicit val format: Format[Regime] = Format(Reads.enumNameReads(Regime), Writes.enumNameWrites)
}

case class Recipient(
  taxIdentifier: TaxIdWithName,
  name: Option[TaxpayerName],
  email: Option[String] = None,
  regime: Option[Regime.Value] = None
)

object Recipient {
  implicit val tif: Format[TaxIdWithName] = TaxIdentifierRESTV2Formats.format

  implicit val format: OFormat[Recipient] = Json.format[Recipient]
}

object TaxIdentifierRESTV2Formats {

  implicit val identifierReads: Reads[TaxIdWithName] =
    ((__ \ "name").readNullable[String] and (__ \ "value").readNullable[String]).tupled
      .flatMap[TaxIdWithName] {
        case (Some("sautr"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(SaUtr(value))
          }
        case (Some("nino"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(Nino(value))
          }
        case (Some("ctutr"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(CtUtr(value))
          }
        case (Some("HMRC-OBTDS-ORG"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcObtdsOrg(value))
          }
        case (Some("HMRC-MTD-VAT"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcMtdVat(value))
          }
        case (Some("empRef"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(Epaye(value))
          }
        case (Some("HMCE-VATDEC-ORG"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmceVatdecOrg(value))
          }
        case (Some("HMRC-CUS-ORG"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcCusOrg(value))
          }
        case (Some("HMRC-PPT-ORG.ETMPREGISTRATIONNUMBER"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcPptOrg(value))
          }
        case (Some("HMRC-MTD-IT"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcMtdItsa(value))
          }
        case (Some("MTDBSA"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcMtdItsa(value))
          }
        case (Some("MTDITID"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcMtdItsa(value))
          }
        case (Some("HMRC-PODS-ORG.PSAID"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcPodsOrg(value))
          }
        case (Some("HMRC-PODSPP-ORG.PSPID"), Some(value)) =>
          Reads[TaxIdWithName] { _ =>
            JsSuccess(HmrcPodsPpOrg(value))
          }
        case (_, None) =>
          Reads[TaxIdWithName] { _ =>
            JsError("The backend has rejected the message due to an unknown tax identifier.")
          }
        case (Some(_), _) =>
          Reads[TaxIdWithName] { _ =>
            JsError("The backend has rejected the message due to an unknown tax identifier.")
          }
        case (None, _) =>
          Reads[TaxIdWithName] { _ =>
            JsError("The backend has rejected the message due to an unknown tax identifier.")
          }
      }

  implicit val identifierWrites: Writes[TaxIdWithName] = new Writes[TaxIdWithName] {
    override def writes(taxId: TaxIdWithName): JsValue = JsObject(Seq(taxId.name -> JsString(taxId.value)))
  }

  implicit val format: Format[TaxIdWithName] = Format(identifierReads, identifierWrites)
}
