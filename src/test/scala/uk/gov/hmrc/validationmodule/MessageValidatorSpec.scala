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

package uk.gov.hmrc.validationmodule

import junit.framework.TestCase
import org.joda.time.LocalDate
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.model._

class MessageValidatorSpec extends AnyWordSpecLike with MockitoSugar with Matchers with ScalaFutures {

  "isGmc" must {

    val message = Message(
      id = BSONObjectID.generate(),
      recipient = TaxEntity(Regime.sa, SaUtr("1234567890"), Some("test@test.com")),
      subject = "RE: Subject",
      body = None,
      validFrom = LocalDate.now(),
      alertFrom = None,
      alertDetails = AlertDetails("template-id", None, Map()),
      lastUpdated = None,
      hash = "*hash*",
      statutory = true,
      renderUrl = RenderUrl(service = "my-service", url = "service-url"),
      sourceData = None,
      externalRef = None
    )

    "return true if the message payload has 'gmc' as the external reference's source in a case-insensitive way" in new TestCase {
      val messageGmc = message.copy(externalRef = Some(ExternalRef("some-id", "gMc")))
      MessageValidator.isGmc(messageGmc) mustBe true
    }

    "return false if the message payload doesn't have 'gmc' as the external reference" in new TestCase {
      val messageNotGmc = message.copy(externalRef = Some(ExternalRef("another-id", "not-gmc")))
      MessageValidator.isGmc(messageNotGmc) mustBe false
    }
  }
}
