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

import uk.gov.hmrc.common.message.model.FormIds.{ ITSA_FORM_IDS_CY, ITSA_FORM_IDS_EN }

trait AlertEmailTemplateMapper extends TemplateId {

  val templatesToMapToNewMessageAlert: Seq[String] = Seq(
    "R002",
    "SA251",
    "SA326D",
    "SA328D",
    "SA359",
    "SA370",
    "SA371",
    "SA372",
    "SA373",
    "IgnorePaperFiling",
    "2WSM-question",
    "2WSM-reply",
    "CA001"
  )

  lazy val itsaTemplates: Map[String, String] = Map(
    "itsaqu1"       -> "new_message_alert_itsaqu1",
    "itsaqu1_cy"    -> "new_message_alert_itsaqu1_cy",
    "itsaqu2"       -> "new_message_alert_itsaqu2",
    "itsaqu2_cy"    -> "new_message_alert_itsaqu2_cy",
    "itsaeops1"     -> "new_message_alert_itsaeops1",
    "itsaeops1_cy"  -> "new_message_alert_itsaeops1_cy",
    "itsaeops2"     -> "new_message_alert_itsaeops2",
    "itsaeops2_cy"  -> "new_message_alert_itsaeops2_cy",
    "itsaeopsf"     -> "new_message_alert_itsaeopsf",
    "itsaeopsf_cy"  -> "new_message_alert_itsaeopsf_cy",
    "itsapoa1-1"    -> "new_message_alert_itsapoa1-1",
    "itsapoa1-1_cy" -> "new_message_alert_itsapoa1-1_cy",
    "itsapoa1-2"    -> "new_message_alert_itsapoa1-2",
    "itsapoa1-2_cy" -> "new_message_alert_itsapoa1-2_cy",
    "itsapoa2-1"    -> "new_message_alert_itsapoa2-1",
    "itsapoa2-1_cy" -> "new_message_alert_itsapoa2-1_cy",
    "itsapoa2-2"    -> "new_message_alert_itsapoa2-2",
    "itsapoa2-2_cy" -> "new_message_alert_itsapoa2-2_cy",
    "itsafd1"       -> "new_message_alert_itsafd1",
    "itsafd1_cy"    -> "new_message_alert_itsafd1_cy",
    "itsafd2"       -> "new_message_alert_itsafd2",
    "itsafd2_cy"    -> "new_message_alert_itsafd2_cy",
    "itsafd3"       -> "new_message_alert_itsafd3",
    "itsafd3_cy"    -> "new_message_alert_itsafd3_cy",
    "itsapoa-cn"    -> "new_message_alert_itsapoa-cn",
    "itsapoa-cn_cy" -> "new_message_alert_itsapoa-cn_cy",
    "itsauc1"       -> "new_message_alert_itsauc1",
    "itsauc1_cy"    -> "new_message_alert_itsauc1_cy"
  )

  lazy val iossTemplates: Map[String, String] = Map(
    "m01ioss"     -> "new_message_alert_m01_ioss",
    "m01ioss_cy"  -> "new_message_alert_m01_ioss_cy",
    "m01aioss"    -> "new_message_alert_m01a_ioss",
    "m01aioss_cy" -> "new_message_alert_m01a_ioss_cy",
    "m02aioss"    -> "new_message_alert_m02a_ioss",
    "m02aioss_cy" -> "new_message_alert_m02a_ioss_cy",
    "m02ioss"     -> "new_message_alert_m02_ioss",
    "m02ioss_cy"  -> "new_message_alert_m02_ioss_cy",
    "m04ioss"     -> "new_message_alert_m04_ioss",
    "m04ioss_cy"  -> "new_message_alert_m04_ioss_cy",
    "m05ioss"     -> "new_message_alert_m05_ioss",
    "m05ioss_cy"  -> "new_message_alert_m05_ioss_cy",
    "m05aioss"    -> "new_message_alert_m05a_ioss",
    "m05aioss_cy" -> "new_message_alert_m05a_ioss_cy",
    "m07aioss"    -> "new_message_alert_m07a_ioss",
    "m07aioss_cy" -> "new_message_alert_m07a_ioss_cy",
    "m06ioss"     -> "new_message_alert_m06_ioss",
    "m06ioss_cy"  -> "new_message_alert_m06_ioss_cy",
    "m06aioss"    -> "new_message_alert_m06a_ioss",
    "m06aioss_cy" -> "new_message_alert_m06a_ioss_cy",
    "m07ioss"     -> "new_message_alert_m07_ioss",
    "m07ioss_cy"  -> "new_message_alert_m07_ioss_cy",
    "m08aioss"    -> "new_message_alert_m08a_ioss",
    "m08aioss_cy" -> "new_message_alert_m08a_ioss_cy",
    "m08ioss"     -> "new_message_alert_m08_ioss",
    "m08ioss_cy"  -> "new_message_alert_m08_ioss_cy"
  )

  // scalastyle:off
  def emailTemplateFromMessageFormId(formId: String, requestAlertTemplateId: Option[String] = None): String =
    (formId.toLowerCase, requestAlertTemplateId) match {
      case (_, Some(templateId)) if templateId != "newMessageAlert"        => templateId
      case ("atsv2_cy", _)                                                 => "annual_tax_summaries_message_alert_cy"
      case ("atsv2", _)                                                    => "annual_tax_summaries_message_alert"
      case (form, _) if form.startsWith("sa316")                           => "newMessageAlert_SA316"
      case (form, _) if form.startsWith("sa309")                           => "newMessageAlert_SA309"
      case (form, _) if form.startsWith("sa300")                           => "newMessageAlert_SA300"
      case (form, _) if form.startsWith("ss300")                           => "newMessageAlert_SS300"
      case (form, _) if form.startsWith("p800") && form.endsWith("_cy")    => "newMessageAlert_P800_cy"
      case (form, _) if form.startsWith("p800")                            => "newMessageAlert_P800"
      case (form, _) if form.startsWith("pa302") && form.endsWith("_cy")   => "newMessageAlert_PA302_cy"
      case (form, _) if form.startsWith("pa302")                           => "newMessageAlert_PA302"
      case (form, _) if form.startsWith("lpi1") && form.endsWith("_cy")    => "newMessageAlert_LPI1_cy"
      case (form, _) if form.startsWith("lpi1")                            => "newMessageAlert_LPI1"
      case (form, _) if ITSA_FORM_IDS_EN.contains(form)                    => ITSA_DEFAULT_TEMPLATE_EN
      case (form, _) if ITSA_FORM_IDS_CY.contains(form)                    => ITSA_DEFAULT_TEMPLATE_CY
      case (form, _) if form.startsWith("lpp4") && form.endsWith("_cy")    => "newMessageAlert_LPP4_cy"
      case (form, _) if form.startsWith("lpp4")                            => "newMessageAlert_LPP4"
      case (form, _) if form.startsWith("ad") && form.endsWith("_cy")      => "newMessageAlert_AD_cy"
      case (form, _) if form.startsWith("ad")                              => "newMessageAlert_AD"
      case (form, _) if form.startsWith("itsa")                            => getTemplateId(form, itsaTemplates, "itsa")
      case (form, _) if form.startsWith("lsp") && form.endsWith("itsa_cy") => ITSA_DEFAULT_TEMPLATE_CY
      case (form, _) if form.startsWith("lsp") && form.endsWith("itsa")    => ITSA_DEFAULT_TEMPLATE_EN
      case (form, _) if form.endsWith("agioss")                            => "new_message_alert_ioss_netp"
      case (form, _) if form.endsWith("gioss")                             => "new_message_alert_gioss"
      case (form, _) if form.endsWith("ioss") || form.endsWith("ioss_cy")  => getTemplateId(form, iossTemplates, "ioss")
      case (form, _) if form.endsWith("oss")                               => "new_message_alert_ioss"
      case (form, _) if form.endsWith("oss_cy")                            => "new_message_alert_ioss_cy"
      case (form, _) =>
        templatesToMapToNewMessageAlert.find(fId => form.startsWith(fId.toLowerCase)) match {
          case Some(formId)              => s"newMessageAlert_$formId"
          case _ if form.endsWith("_cy") => "newMessageAlert_cy"
          case _                         => "newMessageAlert"
        }
    }

  private def getTemplateId(formId: String, templates: Map[String, String], default: String): String =
    templates.find(r => r._1.equals(formId)) match {
      case Some((_, templateId))       => templateId
      case _ if formId.endsWith("_cy") => s"new_message_alert_${default}_cy"
      case _                           => s"new_message_alert_$default"
    }
  // scalastyle:on
}
