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

import org.scalatestplus.play.PlaySpec

class AlertEmailTemplateMapperSpec extends PlaySpec with AlertEmailTemplateMapper {

  "The alert email template mapper" must {

    "use custom templates for atsv2, SA309A, SA316, SA300, SS300, P800, PA302, AD message alerts" in {
      emailTemplateFromMessageFormId("atsv2") mustBe "annual_tax_summaries_message_alert"
      emailTemplateFromMessageFormId("SA309A") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316") mustBe "newMessageAlert_SA316"
      emailTemplateFromMessageFormId("SA300") mustBe "newMessageAlert_SA300"
      emailTemplateFromMessageFormId("SS300") mustBe "newMessageAlert_SS300"
      emailTemplateFromMessageFormId("P800 2032") mustBe "newMessageAlert_P800"
      emailTemplateFromMessageFormId("PA302 2032") mustBe "newMessageAlert_PA302"
      emailTemplateFromMessageFormId("AD2") mustBe "newMessageAlert_AD"
      emailTemplateFromMessageFormId("AD3") mustBe "newMessageAlert_AD"
    }

    "map all the SA not custom templates to `newMessageAlert_formId`" in {
      templatesToMapToNewMessageAlert.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe s"newMessageAlert_$t"
      }
    }

    "match on form ids with extra details message alerts" in {
      emailTemplateFromMessageFormId("SA309A July 2017") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316 (Batch 5)") mustBe "newMessageAlert_SA316"
    }

    "use a standard template for other message alerts" in {
      emailTemplateFromMessageFormId("SAXXX") mustBe "newMessageAlert"
      emailTemplateFromMessageFormId("SAYYY") mustBe "newMessageAlert"
    }

    "not override the alert template only when alert template is different from newMessageAlert" in {
      emailTemplateFromMessageFormId("SA309A", Some("myTemplateId")) mustBe "myTemplateId"
      emailTemplateFromMessageFormId("SA309A", Some("newMessageAlert")) mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA309A") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("LPP4") mustBe "newMessageAlert_LPP4"
      emailTemplateFromMessageFormId("LPI1") mustBe "newMessageAlert_LPI1"
    }

    "map all the ITSA not custom templates to `new_message_alert_itsa`" in {
      val itsaFormId = Map(
        "ITSAQU1"    -> "new_message_alert_itsaqu1",
        "ITSAQU2"    -> "new_message_alert_itsaqu2",
        "ITSAEOPS1"  -> "new_message_alert_itsaeops1",
        "ITSAEOPS2"  -> "new_message_alert_itsaeops2",
        "ITSAEOPSF"  -> "new_message_alert_itsaeopsf",
        "ITSAPOA1-1" -> "new_message_alert_itsapoa1-1",
        "ITSAPOA1-2" -> "new_message_alert_itsapoa1-2",
        "ITSAPOA2-1" -> "new_message_alert_itsapoa2-1",
        "ITSAPOA2-2" -> "new_message_alert_itsapoa2-2",
        "ITSAFD1"    -> "new_message_alert_itsafd1",
        "ITSAFD2"    -> "new_message_alert_itsafd2",
        "ITSAFD3"    -> "new_message_alert_itsafd3",
        "ITSAPOA-CN" -> "new_message_alert_itsapoa-cn",
        "ITSAUC1"    -> "new_message_alert_itsauc1"
      )
      itsaFormId.foreach { t =>
        emailTemplateFromMessageFormId(t._1) mustBe t._2
      }
    }

    "map new ITSA formIds to default template `new_message_alert_itsa`" in {
      val formIds = List(
        "LSP1_ITSA",
        "LSP2_ITSA",
        "LSP3_ITSA",
        "LSP4_ITSA",
        "ITSAMIG1",
        "LPP1A_ITSA",
        "LPP1B_ITSA",
        "LPP2_ITSA",
        "LPP4_ITSA",
        "PAR1_ITSA",
        "ITSAORM1"
      )
      formIds.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_itsa"
      }

      val welshFormIds = List(
        "LSP1_ITSA_cy",
        "LSP2_ITSA_cy",
        "LSP3_ITSA_cy",
        "LSP4_ITSA_cy",
        "ITSAMIG1_cy",
        "LPP1A_ITSA_cy",
        "LPP1B_ITSA_cy",
        "LPP2_ITSA_cy",
        "LPP4_ITSA_cy",
        "PAR1_ITSA_cy",
        "ITSAORM1_CY"
      )
      welshFormIds.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_itsa_cy"
      }
    }

    "map all the IOSS templates to `new_message_alert_*ioss`" in {
      val itsaFormId = Map(
        "M01ioss"  -> "new_message_alert_m01_ioss",
        "M01aioss" -> "new_message_alert_m01a_ioss",
        "M02aioss" -> "new_message_alert_m02a_ioss",
        "M02ioss"  -> "new_message_alert_m02_ioss",
        "M04ioss"  -> "new_message_alert_m04_ioss",
        "M05ioss"  -> "new_message_alert_m05_ioss",
        "M05aioss" -> "new_message_alert_m05a_ioss",
        "M07aioss" -> "new_message_alert_m07a_ioss",
        "M06ioss"  -> "new_message_alert_m06_ioss",
        "M06aioss" -> "new_message_alert_m06a_ioss",
        "M07ioss"  -> "new_message_alert_m07_ioss",
        "M08aioss" -> "new_message_alert_m08a_ioss",
        "M08ioss"  -> "new_message_alert_m08_ioss"
      )
      itsaFormId.foreach { t =>
        emailTemplateFromMessageFormId(t._1) mustBe t._2
      }
    }

    "map all the OSS templates to `new_message_alert_ioss`" in {
      val ossFormIds = List("M01OSS", "M04OSS", "M05OSS", "M05aOSS", "M06OSS", "M06aOSS", "M07OSS", "M07aOSS")
      val ossFormIdsWelsh =
        List("M01OSS_CY", "M04OSS_CY", "M05OSS_CY", "M05aOSS_CY", "M06OSS_CY", "M06aOSS_CY", "M07OSS_CY", "M07aOSS_CY")
      ossFormIds.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_ioss"
      }
      ossFormIdsWelsh.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_ioss_cy"
      }
    }

    "map all the GIOSS templates to `new_message_alert_gioss`" in {
      val gIossFormIds = List(
        "M01GIOSS",
        "M02GIOSS",
        "M03GIOSS",
        "M04GIOSS",
        "M05GIOSS",
        "M06GIOSS",
        "M07GIOSS",
        "M08GIOSS",
        "M09GIOSS",
        "M10GIOSS",
        "M11GIOSS",
        "M12GIOSS"
      )
      gIossFormIds.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_gioss"
      }
    }

    "map the IOSS NETP templates to `new_message_alert_ioss_netp`" in {
      val gIossFormIds = List(
        "M05aGIOSS",
        "M08aGIOSS",
        "M09aGIOSS",
        "M10aGIOSS",
        "M11aGIOSS",
        "M12aGIOSS"
      )
      gIossFormIds.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_ioss_netp"
      }
    }
  }

  "The alert email template mapper - Welsh" must {

    "use custom templates for atsv2, SA309A, SA316, SA300, SS300, P800, PA302, AD message alerts" in {
      emailTemplateFromMessageFormId("atsv2_cy") mustBe "annual_tax_summaries_message_alert_cy"
      emailTemplateFromMessageFormId("SA309A_CY") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316_CY") mustBe "newMessageAlert_SA316"
      emailTemplateFromMessageFormId("SA300_CY") mustBe "newMessageAlert_SA300"
      emailTemplateFromMessageFormId("SS300_CY") mustBe "newMessageAlert_SS300"
      emailTemplateFromMessageFormId("P800 2032_CY") mustBe "newMessageAlert_P800_cy"
      emailTemplateFromMessageFormId("PA302 2032_CY") mustBe "newMessageAlert_PA302_cy"
      emailTemplateFromMessageFormId("LPP4_CY") mustBe "newMessageAlert_LPP4_cy"
      emailTemplateFromMessageFormId("LPI1_CY") mustBe "newMessageAlert_LPI1_cy"
      emailTemplateFromMessageFormId("AD2_CY") mustBe "newMessageAlert_AD_cy"
      emailTemplateFromMessageFormId("AD3_CY") mustBe "newMessageAlert_AD_cy"
    }

    "map all the SA not custom templates to `newMessageAlert_formId`" in {
      templatesToMapToNewMessageAlert.foreach { t =>
        val welshFormId = s"${t}_CY"
        emailTemplateFromMessageFormId(welshFormId) mustBe s"newMessageAlert_$t"
      }
    }

    "match on form ids with extra details message alerts" in {
      emailTemplateFromMessageFormId("SA309A July 2017_CY") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316 (Batch 5)_CY") mustBe "newMessageAlert_SA316"
    }

    "use a standard template for other message alerts" in {
      emailTemplateFromMessageFormId("SAXXX_CY") mustBe "newMessageAlert_cy"
      emailTemplateFromMessageFormId("SAYYY_CY") mustBe "newMessageAlert_cy"
    }

    "not override the alert template only when alert template is different from newMessageAlert" in {
      emailTemplateFromMessageFormId("SA309A_CY", Some("myTemplateId")) mustBe "myTemplateId"
      emailTemplateFromMessageFormId("SA309A_CY", Some("newMessageAlert")) mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA309A_CY") mustBe "newMessageAlert_SA309"
    }

    "map all the ITSA not custom templates to `new_message_alert_itsa_cy`" in {
      val itsaFormId = Map(
        "ITSAQU1"    -> "new_message_alert_itsaqu1",
        "ITSAQU2"    -> "new_message_alert_itsaqu2",
        "ITSAEOPS1"  -> "new_message_alert_itsaeops1",
        "ITSAEOPS2"  -> "new_message_alert_itsaeops2",
        "ITSAEOPSF"  -> "new_message_alert_itsaeopsf",
        "ITSAPOA1-1" -> "new_message_alert_itsapoa1-1",
        "ITSAPOA1-2" -> "new_message_alert_itsapoa1-2",
        "ITSAPOA2-1" -> "new_message_alert_itsapoa2-1",
        "ITSAPOA2-2" -> "new_message_alert_itsapoa2-2",
        "ITSAFD1"    -> "new_message_alert_itsafd1",
        "ITSAFD2"    -> "new_message_alert_itsafd2",
        "ITSAFD3"    -> "new_message_alert_itsafd3",
        "ITSAPOA-CN" -> "new_message_alert_itsapoa-cn",
        "ITSAUC1"    -> "new_message_alert_itsauc1"
      )
      itsaFormId.foreach { t =>
        val welshFormId = s"${t._1}_CY"
        emailTemplateFromMessageFormId(welshFormId) mustBe s"${t._2}_cy"
      }
    }
    "map all the ioss templates having _cy to `new_message_alert_*_ioss_cy`" in {
      val iossFormId = Map(
        "M01ioss"  -> "new_message_alert_m01_ioss",
        "M01aioss" -> "new_message_alert_m01a_ioss",
        "M02aioss" -> "new_message_alert_m02a_ioss",
        "M02ioss"  -> "new_message_alert_m02_ioss",
        "M04ioss"  -> "new_message_alert_m04_ioss",
        "M05ioss"  -> "new_message_alert_m05_ioss",
        "M05aioss" -> "new_message_alert_m05a_ioss",
        "M07aioss" -> "new_message_alert_m07a_ioss",
        "M06ioss"  -> "new_message_alert_m06_ioss",
        "M06aioss" -> "new_message_alert_m06a_ioss",
        "M07ioss"  -> "new_message_alert_m07_ioss",
        "M08aioss" -> "new_message_alert_m08a_ioss",
        "M08ioss"  -> "new_message_alert_m08_ioss"
      )
      iossFormId.foreach { t =>
        val welshFormId = s"${t._1}_CY"
        emailTemplateFromMessageFormId(welshFormId) mustBe s"${t._2}_cy"
      }
    }
  }
}
