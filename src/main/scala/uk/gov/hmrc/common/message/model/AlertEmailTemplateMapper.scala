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

trait AlertEmailTemplateMapper {

  val templatesToMapToNewMessageAlert: Seq[String] = Seq(
    "R002A",
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

  // scalastyle:off
  def emailTemplateFromMessageFormId(formId: String, requestAlertTemplateId: Option[String] = None): String =
    (formId, requestAlertTemplateId) match {
      case (_, Some(templateId)) if templateId != "newMessageAlert"                  => templateId
      case (form, _) if form.toLowerCase == "atsv2_cy"                               => "annual_tax_summaries_message_alert_cy"
      case (form, _) if form.toLowerCase == "atsv2"                                  => "annual_tax_summaries_message_alert"
      case (form, _) if form.startsWith("SA316")                                     => "newMessageAlert_SA316"
      case (form, _) if form.startsWith("SA309")                                     => "newMessageAlert_SA309"
      case (form, _) if form.startsWith("SA300")                                     => "newMessageAlert_SA300"
      case (form, _) if form.startsWith("SS300")                                     => "newMessageAlert_SS300"
      case (form, _) if form.startsWith("P800") && form.toLowerCase.endsWith("_cy")  => "newMessageAlert_P800_cy"
      case (form, _) if form.startsWith("P800")                                      => "newMessageAlert_P800"
      case (form, _) if form.startsWith("PA302") && form.toLowerCase.endsWith("_cy") => "newMessageAlert_PA302_cy"
      case (form, _) if form.startsWith("PA302")                                     => "newMessageAlert_PA302"
      case (form, _) if form.startsWith("LPI1") && form.toLowerCase.endsWith("_cy")  => "newMessageAlert_LPI1_cy"
      case (form, _) if form.startsWith("LPI1")                                      => "newMessageAlert_LPI1"
      case (form, _) if form.startsWith("LPP4") && form.toLowerCase.endsWith("_cy")  => "newMessageAlert_LPP4_cy"
      case (form, _) if form.startsWith("LPP4")                                      => "newMessageAlert_LPP4"
      case (form, _) if form.toLowerCase.startsWith("itsa")                          => getItsaTemplateId(form)
      case (form, _) =>
        templatesToMapToNewMessageAlert.find(fId => form.startsWith(fId)) match {
          case Some(formId)                          => s"newMessageAlert_$formId"
          case _ if form.toLowerCase.endsWith("_cy") => "newMessageAlert_cy"
          case _                                     => "newMessageAlert"
        }
    }

  private def getItsaTemplateId(formId: String): String =
    itsaTemplates.toList.find(r => r._1.equals(formId.toLowerCase)) match {
      case Some((_, templateId))       => templateId
      case _ if formId.endsWith("_cy") => "new_message_alert_itsa_cy"
      case _                           => "new_message_alert_itsa"
    }

  lazy val itsaTemplates = Map(
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
  // scalastyle:on
}
