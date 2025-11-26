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

package uk.gov.hmrc.common.message.util

import java.time.temporal.ChronoUnit.DAYS
import uk.gov.hmrc.common.message.model.LifecycleStatusType.Submitted
import uk.gov.hmrc.common.message.model.{ Lifecycle, LifecycleStatus, Notification }

import java.time.Instant

object TestDataSample {
  val FIVE = 5

  val TEST_TEMPLATE_ID = "test_template"
  val TEST_TEMPLATE_ID_MAP: Map[String, String] = Map("SA309A_CY" -> "myTemplateId_cy", "SA309A" -> "myTemplateId")

  private val TEST_EPOCH_SECONDS = 112345678912334L
  val TEST_TIME_INSTANT: Instant = Instant.ofEpochSecond(TEST_EPOCH_SECONDS)

  val TEST_NOTIFICATION: Notification = Notification(FIVE, TEST_TIME_INSTANT)

  val TEST_LIFECYCLE_STATUS: LifecycleStatus =
    LifecycleStatus(name = Submitted, updated = TEST_TIME_INSTANT.plus(1, DAYS))

  val TEST_LIFECYCLE: Lifecycle =
    Lifecycle(status = TEST_LIFECYCLE_STATUS, startedAt = TEST_TIME_INSTANT, notification = Some(TEST_NOTIFICATION))
}
