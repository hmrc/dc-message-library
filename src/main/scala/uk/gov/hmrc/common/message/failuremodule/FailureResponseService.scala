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

package uk.gov.hmrc.common.message.failuremodule

import play.api.http.Status._
import play.api.http.Status
import play.api.libs.json.{ JsValue, Json, Writes }
import play.api.mvc.{ Result, Results }
import play.api.mvc.Results.{ BadRequest, InternalServerError, NotFound, Ok, Unauthorized }

final case class FailureResponse(failureId: String, reason: String)

object FailureResponse {
  implicit val errorWrites: Writes[FailureResponse] = Json.writes[FailureResponse]
}

object FailureResponseService {

  val INVALID_PAYLOAD = "INVALID_PAYLOAD"
  val INVALID_CORRELATIONID = "INVALID_CORRELATIONID"
  val INVALID_REQUEST = "INVALID_REQUEST"
  val UNKNOWN_TAX_IDENTIFIER = "UNKNOWN_TAX_IDENTIFIER"
  val MISSING_DETAILS = "MISSING_DETAILS"
  val EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"
  val TAXPAYER_NOT_FOUND = "TAXPAYER_NOT_FOUND"
  val SERVER_ERROR = "SERVER_ERROR"
  val CONFLICT = "CONFLICT"
  val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"

  val errorMessageMapping: Map[(Int, String), ErrorMessage] =
    Map(
      (OK, "Submission has not passed validation. Invalid payload.") -> ErrorMessage(
        OK,
        "Submission has not passed validation. Invalid payload.",
        INVALID_PAYLOAD),
      (OK, "Submission has not passed validation. Invalid header CorrelationId.") -> ErrorMessage(
        OK,
        "Submission has not passed validation. Invalid header CorrelationId.",
        INVALID_CORRELATIONID),
      (OK, "The remote endpoint has indicated that the request is invalid.") -> ErrorMessage(
        OK,
        "The remote endpoint has indicated that the request is invalid.",
        INVALID_REQUEST),
      (OK, "Unknown eis error") -> ErrorMessage(OK, "Unknown eis error", SERVER_ERROR),
      (BAD_REQUEST, "Submission has not passed validation. Invalid payload.") -> ErrorMessage(
        BAD_REQUEST,
        "Submission has not passed validation. Invalid payload.",
        INVALID_PAYLOAD),
      (BAD_REQUEST, "Submission has not passed validation. Invalid header CorrelationId.") -> ErrorMessage(
        BAD_REQUEST,
        "Submission has not passed validation. Invalid header CorrelationId.",
        INVALID_CORRELATIONID),
      (BAD_REQUEST, "The remote endpoint has indicated that the request is invalid.") -> ErrorMessage(
        BAD_REQUEST,
        "The remote endpoint has indicated that the request is invalid.",
        INVALID_REQUEST),
      (BAD_REQUEST, "Unknown eis error") -> ErrorMessage(BAD_REQUEST, "Unknown eis error", SERVER_ERROR),
      (BAD_REQUEST, "The backend has rejected the message due to an unknown tax identifier.") -> ErrorMessage(
        BAD_REQUEST,
        "The backend has rejected the message due to an unknown tax identifier.",
        UNKNOWN_TAX_IDENTIFIER),
      (BAD_REQUEST, "Missing or empty externalRef id or source") -> ErrorMessage(
        BAD_REQUEST,
        "Missing or empty externalRef id or source",
        INVALID_PAYLOAD),
      (
        BAD_REQUEST,
        """The "details" object was not provided when the request to the Messages service had "source" set to "gmc"""") ->
        ErrorMessage(
          BAD_REQUEST,
          """The "details" object was not provided when the request to the Messages service had "source" set to "gmc"""",
          MISSING_DETAILS),
      (BAD_REQUEST, "The backend has rejected the message due to the email address being undeliverable.") ->
        ErrorMessage(
          BAD_REQUEST,
          "The backend has rejected the message due to the email address being undeliverable.",
          EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "details: details not provided where it is required") -> ErrorMessage(
        BAD_REQUEST,
        "details: details not provided where it is required",
        MISSING_DETAILS),
      (BAD_REQUEST, "sourceData: invalid source data provided") -> ErrorMessage(
        BAD_REQUEST,
        "sourceData: invalid source data provided",
        INVALID_PAYLOAD),
      (BAD_REQUEST, "Invalid Message") -> ErrorMessage(BAD_REQUEST, "Invalid Message", INVALID_PAYLOAD),
      (BAD_REQUEST, "alertQueue: invalid alert queue provided") -> ErrorMessage(
        BAD_REQUEST,
        "alertQueue: invalid alert queue provided",
        INVALID_PAYLOAD),
      (BAD_REQUEST, "Issue date after the valid from date") -> ErrorMessage(
        BAD_REQUEST,
        "Issue date after the valid from date",
        INVALID_PAYLOAD),
      (BAD_REQUEST, "Invalid date format provided") -> ErrorMessage(
        BAD_REQUEST,
        "Invalid date format provided",
        INVALID_PAYLOAD),
      (BAD_REQUEST, "email: invalid email address provided") -> ErrorMessage(
        BAD_REQUEST,
        "email: invalid email address provided",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "email: email address not provided") -> ErrorMessage(
        BAD_REQUEST,
        "email: email address not provided",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "email: not verified or bounced") -> ErrorMessage(
        BAD_REQUEST,
        "email: not verified or bounced",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "email: not verified as user not opted in") -> ErrorMessage(
        BAD_REQUEST,
        "email: not verified as user not opted in",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "email: not verified as preferences not found") -> ErrorMessage(
        BAD_REQUEST,
        "email: not verified as preferences not found",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "email: not verified for unknown reason") -> ErrorMessage(
        BAD_REQUEST,
        "email: not verified for unknown reason",
        EMAIL_NOT_VERIFIED),
      (BAD_REQUEST, "Invalid alert queue submitted") -> ErrorMessage(
        BAD_REQUEST,
        "Invalid alert queue submitted",
        INVALID_PAYLOAD),
      (NOT_FOUND, "The backend has rejected the message due to not being able to find the tax payer") ->
        ErrorMessage(
          NOT_FOUND,
          "The backend has rejected the message due to not being able to find the tax payer",
          TAXPAYER_NOT_FOUND),
      (NOT_FOUND, "The backend has rejected the message due to not being able to verify the email address.") ->
        ErrorMessage(
          NOT_FOUND,
          "The backend has rejected the message due to not being able to verify the email address.",
          EMAIL_NOT_VERIFIED),
      (UNAUTHORIZED, "Unauthorised") -> ErrorMessage(UNAUTHORIZED, "Unauthorised", SERVER_ERROR),
      (
        Status.CONFLICT,
        "The backend has rejected the message due to duplicated message content or external reference ID.") ->
        ErrorMessage(
          Status.CONFLICT,
          "The backend has rejected the message due to duplicated message content or external reference ID.",
          CONFLICT),
      (INTERNAL_SERVER_ERROR, "IF is currently experiencing problems that require live service intervention.") ->
        ErrorMessage(
          INTERNAL_SERVER_ERROR,
          "IF is currently experiencing problems that require live service intervention.",
          SERVER_ERROR),
      (INTERNAL_SERVER_ERROR, "Dependent systems are currently not responding.") -> ErrorMessage(
        INTERNAL_SERVER_ERROR,
        "Dependent systems are currently not responding.",
        SERVICE_UNAVAILABLE),
      (INTERNAL_SERVER_ERROR, "Failed to parse message") -> ErrorMessage(
        INTERNAL_SERVER_ERROR,
        "Failed to parse message",
        SERVER_ERROR),
      (INTERNAL_SERVER_ERROR, "Unknown eis error") -> ErrorMessage(
        INTERNAL_SERVER_ERROR,
        "Unknown eis error",
        SERVER_ERROR)
    )

  def errorResponseJson(
    errorMessage: String,
    responseCode: Int = BAD_REQUEST,
    showErrorID: Boolean = false): JsValue = {
    def helper(err: ErrorMessage): JsValue =
      if (showErrorID) Json.toJson(FailureResponse(err.errorID, err.errorDescription))
      else Json.toJson(err.errorDescription)
    helper(getFailureId(responseCode, errorMessage))
  }

  def errorResponseResult(
    errorMessage: String,
    responseCode: Int = BAD_REQUEST,
    showErrorID: Boolean = false): Result = {
    def getJson(err: ErrorMessage): JsValue =
      if (showErrorID) Json.toJson(FailureResponse(err.errorID, err.errorDescription))
      else Json.toJson((err.errorDescription))

    getFailureId(responseCode, errorMessage) match {
      case e @ ErrorMessage(OK, _, _)                    => Ok(getJson(e))
      case e @ ErrorMessage(BAD_REQUEST, _, _)           => BadRequest(getJson(e))
      case e @ ErrorMessage(NOT_FOUND, _, _)             => NotFound(getJson(e))
      case e @ ErrorMessage(UNAUTHORIZED, _, _)          => Unauthorized(getJson(e))
      case e @ ErrorMessage(Status.CONFLICT, _, _)       => Results.Conflict(getJson(e))
      case e @ ErrorMessage(INTERNAL_SERVER_ERROR, _, _) => InternalServerError(getJson(e))
    }

  }

  private def getFailureId(responseCode: Int, message: String): ErrorMessage =
    errorMessageMapping
      .collectFirst {
        case ((x, y), value)
            if x == responseCode
              && message.toLowerCase.matches(s".*${y.toLowerCase}.*") =>
          value
      }
      .getOrElse(ErrorMessage(INTERNAL_SERVER_ERROR, message, SERVER_ERROR))
}

case class ErrorMessage(errorCode: Int, errorDescription: String, errorID: String)
