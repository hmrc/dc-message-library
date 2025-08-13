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

import play.api.libs.json.*

enum LifecycleStatusType(val entryName: String) {
  case Submitted extends LifecycleStatusType("SUBMITTED")
  case SubmissionFailed extends LifecycleStatusType("SUBMISSION_FAILED")
  case Delivered extends LifecycleStatusType("DELIVERED")
  case DeliveryFailed extends LifecycleStatusType("DELIVERY_FAILED")
  case Responded extends LifecycleStatusType("RESPONDED")
}

object LifecycleStatusType {
  implicit val format: Format[LifecycleStatusType] = new Format[LifecycleStatusType] {
    def reads(json: JsValue): JsResult[LifecycleStatusType] = json match {
      case JsString("SUBMITTED")         => JsSuccess(Submitted)
      case JsString("SUBMISSION_FAILED") => JsSuccess(SubmissionFailed)
      case JsString("DELIVERED")         => JsSuccess(Delivered)
      case JsString("DELIVERY_FAILED")   => JsSuccess(DeliveryFailed)
      case JsString("RESPONDED")         => JsSuccess(Responded)
      case _                             => JsError("Invalid Lifecycle Status Type")
    }

    def writes(status: LifecycleStatusType): JsValue = JsString(status.entryName)
  }
}
