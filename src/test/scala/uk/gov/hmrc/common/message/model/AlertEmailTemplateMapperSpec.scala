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

class AlertEmailTemplateMapperSpec extends PlaySpec with AlertEmailTemplateMapper {

  "The alert email template mapper" must {

    "use custom templates for atsv2, SA309A, SA316, SA300, SS300, P800, PA302 message alerts" in {
      emailTemplateFromMessageFormId("atsv2") mustBe "annual_tax_summaries_message_alert"
      emailTemplateFromMessageFormId("SA309A") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316") mustBe "newMessageAlert_SA316"
      emailTemplateFromMessageFormId("SA300") mustBe "newMessageAlert_SA300"
      emailTemplateFromMessageFormId("SS300") mustBe "newMessageAlert_SS300"
      emailTemplateFromMessageFormId("P800 2032") mustBe "newMessageAlert_P800"
      emailTemplateFromMessageFormId("PA302 2032") mustBe "newMessageAlert_PA302"
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
    }

    "map all the ITSA not custom templates to `new_message_alert_itsa`" in {
      val itsaFormId = List(
        "ITSAQU1",
        "ITSAQU2",
        "ITSAEOPS1",
        "ITSAEOPS2",
        "ITSAEOPSF",
        "ITSAPOA1-1",
        "ITSAPOA1-2",
        "ITSAPOA2-1",
        "ITSAPOA2-2",
        "ITSAFD1",
        "ITSAFD2",
        "ITSAFD3",
        "ITSAPOA-CN",
        "ITSAUC1"
      )
      itsaFormId.foreach { t =>
        emailTemplateFromMessageFormId(t) mustBe "new_message_alert_itsa"
      }
    }
  }

  "The alert email template mapper - Welsh" must {

    "use custom templates for atsv2, SA309A, SA316, SA300, SS300, P800, PA302 message alerts" in {
      emailTemplateFromMessageFormId("atsv2_cy") mustBe "annual_tax_summaries_message_alert_cy"
      emailTemplateFromMessageFormId("SA309A_CY") mustBe "newMessageAlert_SA309"
      emailTemplateFromMessageFormId("SA316_CY") mustBe "newMessageAlert_SA316"
      emailTemplateFromMessageFormId("SA300_CY") mustBe "newMessageAlert_SA300"
      emailTemplateFromMessageFormId("SS300_CY") mustBe "newMessageAlert_SS300"
      emailTemplateFromMessageFormId("P800 2032_CY") mustBe "newMessageAlert_P800_cy"
      emailTemplateFromMessageFormId("PA302 2032_CY") mustBe "newMessageAlert_PA302_cy"
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
      val itsaFormId = List(
        "ITSAQU1",
        "ITSAQU2",
        "ITSAEOPS1",
        "ITSAEOPS2",
        "ITSAEOPSF",
        "ITSAPOA1-1",
        "ITSAPOA1-2",
        "ITSAPOA2-1",
        "ITSAPOA2-2",
        "ITSAFD1",
        "ITSAFD2",
        "ITSAFD3",
        "ITSAPOA-CN",
        "ITSAUC1"
      )
      itsaFormId.foreach { t =>
        val welshFormId = s"${t}_CY"
        emailTemplateFromMessageFormId(welshFormId) mustBe "new_message_alert_itsa_cy"
      }
    }
  }
}
