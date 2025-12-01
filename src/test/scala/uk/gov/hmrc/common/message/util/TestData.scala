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
import uk.gov.hmrc.common.message.model.{ ExternalRef, Lifecycle, LifecycleStatus, LifecycleStatusType, Notification, RenderUrl }
import uk.gov.hmrc.domain.SaUtr

import java.time.{ Instant, LocalDate }

object TestData {
  val EMPTY_STRING = ""

  val COUNT_TWO = 2
  val COUNT_THREE = 3
  val COUNT_FIVE = 5
  val COUNT_SIX = 6

  val TEST_ID = "test_id"
  val TEST_TEMPLATE_ID = "test_template"
  val TEST_TITLE = "test_title"
  val TEST_TEMPLATE_ID_MAP: Map[String, String] = Map("SA309A_CY" -> "myTemplateId_cy", "SA309A" -> "myTemplateId")
  val TEST_ENVELOP_ID = "test_envelopeId"
  val TEST_SUBJECT = "test_subject"
  val TEST_BODY = "test_body"

  private val TEST_EPOCH_SECONDS = 112345678912334L
  val TEST_TIME_INSTANT: Instant = Instant.ofEpochSecond(TEST_EPOCH_SECONDS)

  val TEST_NOTIFICATION: Notification = Notification(COUNT_FIVE, TEST_TIME_INSTANT)

  val TEST_LIFECYCLE_STATUS: LifecycleStatus =
    LifecycleStatus(name = Submitted, updated = TEST_TIME_INSTANT.plus(1, DAYS))

  val TEST_LIFECYCLE: Lifecycle =
    Lifecycle(status = TEST_LIFECYCLE_STATUS, startedAt = TEST_TIME_INSTANT, notification = Some(TEST_NOTIFICATION))

  val TEST_FORM_TYPE = "test_type"
  val TEST_BATCH_ID = "123456"

  val TEST_YEAR = 2025
  val TEST_MONTH = 11
  val TEST_DAY = 28

  val TEST_LOCAL_DATE: LocalDate = LocalDate.of(TEST_YEAR, TEST_MONTH, TEST_DAY)

  val TEST_SAUTR: SaUtr = SaUtr("1234567890")
  val TEST_EMAIL = "test@test.com"
  val TEST_MAIL_SUBJECT = "RE: Subject"
  val TEST_THREAD_ID = "5c85a5000000000000000000"
  val TEST_HASH = "*hash*"
  val TEST_DETAILS_ID = "5c85a50000"
  val TEST_SOURCE_DATA = "ew0KICAgIm5hbWUiOiAiRGFuaWVsIiwNCiAgICJzZWF0IiA6ICJ5ZXMiDQp9"
  val TEST_REF_ID = "883412342899"
  val TEST_SOURCE_MDTP = "mdtp"
  val TEST_SOURCE_GMC = "gmc"

  val TEST_RENDER_URL: RenderUrl = RenderUrl(service = "my-service", url = "service-url")
  val TEST_EXTERNAL_REF: ExternalRef = ExternalRef(TEST_REF_ID, TEST_SOURCE_MDTP)
}
