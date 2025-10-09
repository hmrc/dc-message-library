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

import uk.gov.hmrc.domain.{ Nino, NinoGenerator, SaUtr, SaUtrGenerator }

import java.util.UUID

object GenerateRandom {
  val ninoGenerator: NinoGenerator = NinoGenerator()
  val utrGenerator: SaUtrGenerator = SaUtrGenerator()

  def email(): String = s"${UUID.randomUUID()}@TEST.com"

  def utr(): SaUtr = utrGenerator.nextSaUtr

  def nino(): Nino = ninoGenerator.nextNino

}
