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

package uk.gov.hmrc.message.library.model

import play.api.libs.json._
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.domain._

final case class TaxEntity(
  regime: Regime.Value,
  identifier: TaxIdWithName,
  email: Option[String] = None
)

object TaxEntity {

  def create(identifier: TaxIdWithName, email: Option[String] = None, regime: Option[Regime.Value] = None): TaxEntity =
    TaxEntity(
      regime.foldLeft(regimeOf(identifier))((b, a) =>
        if (a == b) a else throw new RuntimeException(s"unmatched regimes: $a and $b")),
      identifier,
      email)

  def getEnrolment(taxEntity: TaxEntity): String =
    taxEntity match {
      case TaxEntity(Regime.paye, id, _)                  => s"IR-NINO~NINO~${id.value}"
      case TaxEntity(Regime.sa, id, _)                    => s"IR-SA~UTR~${id.value}"
      case TaxEntity(Regime.ct, id, _)                    => s"IR-CT~UTR~${id.value}"
      case TaxEntity(Regime.sdil, id, _)                  => s"HMRC-OBTDS-ORG~SD.ETMPREGISTRATIONNUMBER~${id.value}"
      case TaxEntity(Regime.fhdds, id, _)                 => s"HMRC-OBTDS-ORG~FH.ETMPREGISTRATIONNUMBER~${id.value}"
      case TaxEntity(Regime.vat, HmrcMtdVat(value), _)    => s"HMRC-MTD-VAT~VRN~$value"
      case TaxEntity(Regime.vat, HmceVatdecOrg(value), _) => s"HMRC-VATDEC-ORG~VATREGNO~$value"
      case TaxEntity(Regime.epaye, id, _)                 => s"IR-PAYE~TAXOFFICEREFERENCE~${id.value}"
      case TaxEntity(Regime.cds, id, _)                   => s"HMRC-CUS-ORG~EORINUMBER~${id.value}"
      case TaxEntity(Regime.ppt, id, _)                   => s"HMRC-PPT-ORG~ETMPREGISTRATIONNUMBER~${id.value}"
      case TaxEntity(Regime.itsa, id, _)                  => s"HMRC-MTD-IT~MTDITID~${id.value}"
      case r                                              => throw new RuntimeException(s"unsupported regime $r")
    }

  // https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?spaceKey=DF&title=HMRC-OBTDS-ORG+GG+Service
  def regimeOf(identifier: TaxIdWithName): Regime.Value =
    identifier match {
      case _: Nino                                       => Regime.paye
      case _: SaUtr                                      => Regime.sa
      case _: CtUtr                                      => Regime.ct
      case x: HmrcObtdsOrg if x.value matches "^..SD.*$" => Regime.sdil
      case x: HmrcObtdsOrg if x.value matches "^..FH.*$" => Regime.fhdds
      case _: HmrcMtdVat                                 => Regime.vat
      case _: Epaye                                      => Regime.epaye
      case _: HmceVatdecOrg                              => Regime.vat
      case _: HmrcCusOrg                                 => Regime.cds
      case _: HmrcPptOrg                                 => Regime.ppt
      case _: HmrcMtdItsa                                => Regime.itsa
      case x                                             => throw new RuntimeException(s"unsupported identifier $x")
    }

  def forAudit(entity: TaxEntity): Map[String, String] = {
    val id = entity.identifier
    Map(id.name -> id.value)
  }

  implicit def taxEntityFormat(implicit taxId: Format[TaxIdWithName]): Format[TaxEntity] = Json.format[TaxEntity]

  case class Epaye(value: String) extends TaxIdentifier with SimpleName {
    override def toString = value
    val name = "empRef"
  }

  case class HmceVatdecOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString = value
    val name = "HMCE-VATDEC-ORG"
  }

  case class HmrcCusOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString = value
    val name = "HMRC-CUS-ORG"
  }

  case class HmrcPptOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString = value
    val name = "ETMPREGISTRATIONNUMBER"
  }

}
