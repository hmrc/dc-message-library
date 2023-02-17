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

import play.api.libs.json._
import play.api.libs.functional.syntax._

final case class MessagesCount(total: Int, unread: Int) {
  def +(m: MessagesCount): MessagesCount = MessagesCount(total + m.total, unread + m.unread)
  def -(m: MessagesCount): MessagesCount = MessagesCount(total + m.total, unread + m.unread)
}

object MessagesCount {
  implicit val messagesCountWrites: Writes[MessagesCount] = (
    (__ \ "total").write[Int] and
      (__ \ "unread").write[Int]
  )(m => (m.total, m.unread))

  def empty: MessagesCount = MessagesCount(0, 0)
}
