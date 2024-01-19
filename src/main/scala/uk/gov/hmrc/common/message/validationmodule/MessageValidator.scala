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

package uk.gov.hmrc.common.message.validationmodule

import org.apache.commons.codec.binary.Base64
import uk.gov.hmrc.common.message.emailaddress.EmailAddress
import uk.gov.hmrc.common.message.model._
import uk.gov.hmrc.domain.HmrcMtdVat

import scala.util.{ Failure, Success, Try }

object MessageValidator {

  def isValidMessage(message: Message): Try[Message] =
    for {
      _ <- checkDetailsIsPresent(message)
      _ <- checkValidSourceData(message)
      _ <- checkEmptyEmailAddress(message)
      _ <- checkEmptyAlertQueue(message)
      _ <- checkValidIssueDate(message)
      _ <- checkInvalidEmailAddress(message)
      _ <- checkEmailAbsentIfInvalidTaxId(message)
      _ <- checkValidAlertQueue(message)
      _ <- checkEmailPresentForVat(message)
    } yield message

  def checkDetailsIsPresent(message: Message): Try[Message] = message match {
    case m if isGmc(m) && m.body.exists(_.form.isDefined) => Success(m)
    case m if !isGmc(m)                                   => Success(m)
    case _                                                => Failure(MessageValidationException("details: details not provided where it is required"))
  }

  def checkValidSourceData(message: Message): Try[Message] = message.sourceData match {
    case Some(data) if (data.trim.isEmpty || !Base64.isBase64(data)) =>
      Failure(new IllegalArgumentException("sourceData: invalid source data provided"))
    case Some(data) if (!data.trim.isEmpty || Base64.isBase64(data)) => Success(message)
    case None if !isGmc(message)                                     => Success(message)
    case _                                                           => Failure(MessageValidationException("Invalid Message"))
  }

  def checkEmptyEmailAddress(message: Message): Try[Message] = message.alertDetails.data.get("email") match {
    case Some(email) if email.trim.isEmpty =>
      Failure(MessageValidationException("email: invalid email address provided"))
    case _ => Success(message)
  }

  def checkEmptyAlertQueue(message: Message): Try[Message] = message.alertQueue match {
    case Some(queue) if queue.trim.isEmpty =>
      Failure(MessageValidationException("alertQueue: invalid alert queue provided"))
    case _ => Success(message)
  }

  def checkValidIssueDate(message: Message): Try[Message] = {
    val validFrom = message.validFrom
    val issueDate = message.body.flatMap(_.issueDate).getOrElse(validFrom)
    if (validFrom == issueDate || validFrom.isAfter(issueDate)) {
      Success(message)
    } else {
      Failure(MessageValidationException("Issue date after the valid from date"))
    }
  }

  def checkInvalidEmailAddress(message: Message): Try[Message] = message.recipient.email match {
    case Some(email) if EmailAddress.isValid(email) => Success(message)
    case Some(_)                                    => Failure(MessageValidationException("email: invalid email address provided"))
    case None                                       => Success(message)
  }

  def checkEmailAbsentIfInvalidTaxId(message: Message): Try[Message] = message.recipient.email match {
    case None if !isValidTaxIdentifier(message.recipient.identifier.name) =>
      Failure(MessageValidationException("email: email address not provided"))
    case _ => Success(message)
  }

  def checkEmailPresentForVat(message: Message): Try[Message] = message.recipient.email match {
    case None if message.recipient.identifier.isInstanceOf[HmrcMtdVat] =>
      Failure(MessageValidationException("email: email address not provided"))
    case _ => Success(message)
  }

  def isGmc(message: Message): Boolean = message.externalRef match {
    case Some(ExternalRef(_, source)) if source.toLowerCase == "gmc" => true
    case _                                                           => false
  }

  def isValidTaxIdentifier(taxId: String): Boolean = {
    // Refer "domain" library for valid Tax Identifier names
    val taxIdentifiers =
      List(
        "nino",
        "sautr",
        "ctutr",
        "HMRC-OBTDS-ORG",
        "HMRC-MTD-VAT",
        "empRef",
        "HMCE-VATDEC-ORG",
        "HMRC-CUS-ORG",
        "HMRC-PPT-ORG",
        "HMRC-MTD-IT")
    taxIdentifiers.contains(taxId)
  }

  def checkValidAlertQueue(message: Message): Try[Message] = message.alertQueue match {
    case Some(alertQueue) if (AlertQueueTypes.alertQueueTypes.contains(alertQueue)) => Success(message)
    case Some(_)                                                                    => Failure(MessageValidationException("Invalid alert queue submitted"))
    case _                                                                          => Success(message)
  }
}

case class MessageValidationException(message: String) extends RuntimeException(message)
