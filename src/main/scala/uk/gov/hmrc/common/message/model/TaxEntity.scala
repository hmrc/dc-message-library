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
        if (a == b) a else throw new RuntimeException(s"unmatched regimes: $a and $b")
      ),
      identifier,
      email
    )

  // scalastyle:off
  def getEnrolments(taxEntity: TaxEntity): Enrolments =
    taxEntity match {
      case TaxEntity(Regime.paye, id, _)  => Enrolments(s"IR-NINO~NINO~${id.value}")
      case TaxEntity(Regime.sa, id, _)    => Enrolments(s"IR-SA~UTR~${id.value}")
      case TaxEntity(Regime.ct, id, _)    => Enrolments(s"IR-CT~UTR~${id.value}")
      case TaxEntity(Regime.sdil, id, _)  => Enrolments(s"HMRC-OBTDS-ORG~SD.ETMPREGISTRATIONNUMBER~${id.value}")
      case TaxEntity(Regime.fhdds, id, _) => Enrolments(s"HMRC-OBTDS-ORG~FH.ETMPREGISTRATIONNUMBER~${id.value}")
      case TaxEntity(Regime.vat, HmrcMtdVat(value), _) =>
        Enrolments(s"HMRC-MTD-VAT~VRN~$value", s"HMRC-OSS-ORG~VRN~$value")
      case TaxEntity(Regime.vat, Vrn(value), _) =>
        Enrolments(s"HMRC-MTD-VAT~VRN~$value", s"HMRC-OSS-ORG~VRN~$value")
      case TaxEntity(Regime.vat, HmrcOssOrg(value), _) =>
        Enrolments(s"HMRC-MTD-VAT~VRN~$value", s"HMRC-OSS-ORG~VRN~$value")
      case TaxEntity(Regime.vat, HmceVatdecOrg(value), _) => Enrolments(s"HMRC-VATDEC-ORG~VATREGNO~$value")
      case TaxEntity(Regime.epaye, id, _)                 => Enrolments(s"IR-PAYE~EMPREF~${id.value}")
      case TaxEntity(Regime.cds, id, _)                   => Enrolments(s"HMRC-CUS-ORG~EORINUMBER~${id.value}")
      case TaxEntity(Regime.ppt, id, _)  => Enrolments(s"HMRC-PPT-ORG~ETMPREGISTRATIONNUMBER~${id.value}")
      case TaxEntity(Regime.itsa, id, _) => Enrolments(s"HMRC-MTD-IT~MTDITID~${id.value}")
      case TaxEntity(Regime.pods, HmrcPodsOrg(value), _)   => Enrolments(s"HMRC-PODS-ORG~PSAID~$value")
      case TaxEntity(Regime.pods, HmrcPodsPpOrg(value), _) => Enrolments(s"HMRC-PODSPP-ORG~PSPID~$value")
      case TaxEntity(Regime.ioss, HmrcIossOrg(value), _)   => Enrolments(s"HMRC-IOSS-ORG~IOSSNumber~$value")
      case TaxEntity(Regime.oss, HmrcOssOrg(value), _)     => Enrolments(s"HMRC-OSS-ORG~VRN~$value")
      case TaxEntity(Regime.ad, HmrcAdOrg(value), _)       => Enrolments(s"HMRC-AD-ORG~APPAID~$value")
      case r                                               => throw new RuntimeException(s"unsupported tax entity $r")
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
      case _: Vrn                                        => Regime.vat
      case _: HmrcOssOrg                                 => Regime.oss
      case _: Epaye                                      => Regime.epaye
      case _: HmceVatdecOrg                              => Regime.vat
      case _: HmrcCusOrg                                 => Regime.cds
      case _: HmrcPptOrg                                 => Regime.ppt
      case _: HmrcMtdItsa                                => Regime.itsa
      case _: HmrcPodsOrg                                => Regime.pods
      case _: HmrcPodsPpOrg                              => Regime.pods
      case _: HmrcIossOrg                                => Regime.ioss
      case _: HmrcAdOrg                                  => Regime.ad
      case x                                             => throw new RuntimeException(s"unsupported identifier $x")
    }
  // scalastyle:on

  def forAudit(entity: TaxEntity): Map[String, String] = Option(entity.identifier)
    .collect { case x: TaxIdWithName =>
      Map(x.name -> x.value)
    }
    .getOrElse(Map.empty)

  implicit def taxEntityFormat(implicit taxId: Format[TaxIdWithName]): Format[TaxEntity] = Json.format[TaxEntity]

  case class Epaye(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "EMPREF"
  }

  object Epaye extends (String => Epaye) {
    implicit val orgWrite: Writes[Epaye] = new SimpleObjectWrites[Epaye](_.value)
    implicit val orgRead: Reads[Epaye] = new SimpleObjectReads[Epaye]("IR-PAYE", Epaye.apply)
  }

  case class HmceVatdecOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "HMCE-VATDEC-ORG"
  }

  object HmceVatdecOrg extends (String => HmceVatdecOrg) {
    implicit val orgWrite: Writes[HmceVatdecOrg] = new SimpleObjectWrites[HmceVatdecOrg](_.value)
    implicit val orgRead: Reads[HmceVatdecOrg] =
      new SimpleObjectReads[HmceVatdecOrg]("HMCE-VATDEC-ORG", HmceVatdecOrg.apply)
  }

  case class HmrcCusOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "HMRC-CUS-ORG"
  }

  object HmrcCusOrg extends (String => HmrcCusOrg) {
    implicit val orgWrite: Writes[HmrcCusOrg] = new SimpleObjectWrites[HmrcCusOrg](_.value)
    implicit val orgRead: Reads[HmrcCusOrg] =
      new SimpleObjectReads[HmrcCusOrg]("HMRC-CUS-ORG", HmrcCusOrg.apply)
  }

  case class HmrcOssOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "HMRC-OSS-ORG"
  }

  object HmrcOssOrg extends (String => HmrcOssOrg) {
    implicit val orgWrite: Writes[HmrcOssOrg] = new SimpleObjectWrites[HmrcOssOrg](_.value)
    implicit val orgRead: Reads[HmrcOssOrg] =
      new SimpleObjectReads[HmrcOssOrg]("HMRC-OSS-ORG", HmrcOssOrg.apply)
  }

  case class HmrcPptOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "ETMPREGISTRATIONNUMBER"
  }

  object HmrcPptOrg extends (String => HmrcPptOrg) {
    implicit val orgWrite: Writes[HmrcPptOrg] = new SimpleObjectWrites[HmrcPptOrg](_.value)
    implicit val orgRead: Reads[HmrcPptOrg] =
      new SimpleObjectReads[HmrcPptOrg]("HMRC-PPT-ORG", HmrcPptOrg.apply)
  }

  case class HmrcPodsOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "PSAID"
  }

  object HmrcPodsOrg extends (String => HmrcPodsOrg) {
    implicit val orgWrite: Writes[HmrcPodsOrg] = new SimpleObjectWrites[HmrcPodsOrg](_.value)
    implicit val orgRead: Reads[HmrcPodsOrg] =
      new SimpleObjectReads[HmrcPodsOrg]("HMRC-PODS-ORG", HmrcPodsOrg.apply)
  }

  case class HmrcPodsPpOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "PSPID"
  }

  object HmrcPodsPpOrg extends (String => HmrcPodsPpOrg) {
    implicit val orgWrite: Writes[HmrcPodsPpOrg] = new SimpleObjectWrites[HmrcPodsPpOrg](_.value)
    implicit val orgRead: Reads[HmrcPodsPpOrg] =
      new SimpleObjectReads[HmrcPodsPpOrg]("HMRC-PODSPP-ORG", HmrcPodsPpOrg.apply)
  }

  case class HmrcIossOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "HMRC-IOSS-ORG"
  }

  object HmrcIossOrg extends (String => HmrcIossOrg) {
    implicit val orgWrite: Writes[HmrcIossOrg] = new SimpleObjectWrites[HmrcIossOrg](_.value)
    implicit val orgRead: Reads[HmrcIossOrg] =
      new SimpleObjectReads[HmrcIossOrg]("HMRC-IOSS-ORG", HmrcIossOrg.apply)
  }

  case class HmrcAdOrg(value: String) extends TaxIdentifier with SimpleName {
    override def toString: String = value
    val name = "HMRC-AD-ORG"
  }

  object HmrcAdOrg extends (String => HmrcAdOrg) {
    implicit val orgWrite: Writes[HmrcAdOrg] = new SimpleObjectWrites[HmrcAdOrg](_.value)
    implicit val orgRead: Reads[HmrcAdOrg] =
      new SimpleObjectReads[HmrcAdOrg]("HMRC-AD-ORG", HmrcAdOrg.apply)
  }
}

final case class Enrolments(main: String, fallback: List[String])

object Enrolments {

  def apply(e: String): Enrolments =
    Enrolments(e, Nil)

  def apply(e1: String, e2: String): Enrolments =
    Enrolments(e1, List(e2))
}
