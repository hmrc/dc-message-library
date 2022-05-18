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

package uk.gov.hmrc.message2.library.model

import enumeratum.EnumEntry.UpperSnakecase
import enumeratum.{ Enum, EnumEntry, PlayJsonEnum }

import scala.collection.immutable

sealed trait LifecycleStatusType extends EnumEntry with UpperSnakecase

object LifecycleStatusType extends Enum[LifecycleStatusType] with PlayJsonEnum[LifecycleStatusType] {
  case object Submitted extends LifecycleStatusType
  case object SubmissionFailed extends LifecycleStatusType
  case object Delivered extends LifecycleStatusType
  case object DeliveryFailed extends LifecycleStatusType
  case object Responded extends LifecycleStatusType

  override def values: immutable.IndexedSeq[LifecycleStatusType] = findValues
}
