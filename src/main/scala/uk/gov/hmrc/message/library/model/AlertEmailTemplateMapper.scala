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

trait AlertEmailTemplateMapper {

  val templatesToMapToNewMessageAlert = Seq(
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
      case (form, _) if form.startsWith("ITSA") && form.toLowerCase.endsWith("_cy")  => "new_message_alert_itsa_cy"
      case (form, _) if form.startsWith("ITSA")                                      => "new_message_alert_itsa"
      case (form, _) =>
        templatesToMapToNewMessageAlert.find(fId => form.startsWith(fId)) match {
          case Some(formId)                          => s"newMessageAlert_$formId"
          case _ if form.toLowerCase.endsWith("_cy") => "newMessageAlert_cy"
          case _                                     => "newMessageAlert"
        }
    }

}
