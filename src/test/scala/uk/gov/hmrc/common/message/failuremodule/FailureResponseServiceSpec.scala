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

import org.scalatestplus.play.PlaySpec
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.common.message.failuremodule.FailureResponseService.{ errorResponseJson, errorResponseResult }

class FailureResponseServiceSpec extends PlaySpec {
  "The FailureResponseService" must {
    "for Quadient return NO_ERROR_MAPPING_FOUND ErrorMessage if now mapping found and don't show error ID" in {
      Json.stringify(errorResponseJson("Invalid Data", showErrorID = true)) mustEqual """{"failureId":"SERVER_ERROR","reason":"Invalid Data"}"""
    }

    "for non Quadient not return NO_ERROR_MAPPING_FOUND ErrorMessage if now mapping found and don't show error ID" in {
      Json.stringify(errorResponseJson("Invalid Data")) mustEqual """"Invalid Data""""
    }

    "for Quadient return INVALID_PAYLOAD for Submission has not passed validation. Invalid payload." in {
      Json.stringify(errorResponseJson("Submission has not passed validation. Invalid payload.", showErrorID = true)) must include(
        "INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for Submission has not passed validation. Invalid payload." in {
      Json.stringify(errorResponseJson("Submission has not passed validation. Invalid payload.")) mustEqual """"Submission has not passed validation. Invalid payload.""""
    }

    "for Quadient return INVALID_CORRELATIONID for Submission has not passed validation. Invalid header CorrelationId." in {
      Json.stringify(errorResponseJson(
        "Submission has not passed validation. Invalid header CorrelationId.",
        showErrorID = true)) must include("INVALID_CORRELATIONID")
    }

    "for non Quadient not return INVALID_CORRELATIONID for Submission has not passed validation. Invalid header CorrelationId." in {
      Json.stringify(errorResponseJson("Submission has not passed validation. Invalid header CorrelationId.")) mustEqual """"Submission has not passed validation. Invalid header CorrelationId.""""
    }

    "for Quadient return UNKNOWN_TAX_IDENTIFIER for The backend has rejected the message due to an unknown tax identifier." in {
      Json.stringify(errorResponseJson(
        "The backend has rejected the message due to an unknown tax identifier.",
        showErrorID = true)) must include("UNKNOWN_TAX_IDENTIFIER")
    }

    "for non Quadient not return UNKNOWN_TAX_IDENTIFIER for The backend has rejected the message due to an unknown tax identifier." in {
      Json.stringify(errorResponseJson("The backend has rejected the message due to an unknown tax identifier.")) mustEqual """"The backend has rejected the message due to an unknown tax identifier.""""
    }

    "for Quadient return MISSING_DETAILS for details not provided where it is required" in {
      Json.stringify(errorResponseJson(
        """The "details" object was not provided when the request to the Messages service had "source" set to "gmc"""",
        showErrorID = true)) must include("MISSING_DETAILS")
    }

    "for Quadient return EMAIL_NOT_VERIFIED for The backend has rejected the message due to the email address being undeliverable." in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to the email address being undeliverable.",
          showErrorID = true)) must include("EMAIL_NOT_VERIFIED")
    }

    "for non Quadient not return EMAIL_NOT_VERIFIED for The backend has rejected the message due to the email address being undeliverable." in {
      Json.stringify(errorResponseJson(
        "The backend has rejected the message due to the email address being undeliverable.")) mustEqual """"The backend has rejected the message due to the email address being undeliverable.""""
    }

    "for Quadient return MISSING_DETAILS for details: details not provided where it is required" in {
      Json.stringify(errorResponseJson("details: details not provided where it is required", showErrorID = true)) must include(
        "MISSING_DETAILS")
    }

    "for non Quadient not return MISSING_DETAILS for details: details not provided where it is required" in {
      Json.stringify(errorResponseJson("details: details not provided where it is required")) mustEqual """"details: details not provided where it is required""""
    }

    "for Quadient return INVALID_PAYLOAD for sourceData: invalid source data provided" in {
      Json.stringify(errorResponseJson("sourceData: invalid source data provided", showErrorID = true)) must include(
        "INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for sourceData: invalid source data provided" in {
      Json.stringify(errorResponseJson("sourceData: invalid source data provided")) mustEqual """"sourceData: invalid source data provided""""
    }

    "for Quadient return INVALID_PAYLOAD for Invalid Message" in {
      Json.stringify(errorResponseJson("Invalid Message", showErrorID = true)) must include("INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for Invalid Message" in {
      Json.stringify(errorResponseJson("Invalid Message")) mustEqual """"Invalid Message""""
    }

    "for Quadient return EMAIL_NOT_VERIFIED for email: invalid email address provided" in {
      Json.stringify(errorResponseJson("email: invalid email address provided", showErrorID = true)) must include(
        "EMAIL_NOT_VERIFIED")
    }

    "for non Quadient not return EMAIL_NOT_VERIFIED for email: invalid email address provided" in {
      Json.stringify(errorResponseJson("email: invalid email address provided")) mustEqual """"email: invalid email address provided""""
    }

    "for Quadient return INVALID_PAYLOAD for alertQueue: invalid alert queue provided" in {
      Json.stringify(errorResponseJson("alertQueue: invalid alert queue provided", showErrorID = true)) must include(
        "INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for alertQueue: invalid alert queue provided" in {
      Json.stringify(errorResponseJson("alertQueue: invalid alert queue provided")) mustEqual """"alertQueue: invalid alert queue provided""""
    }

    "for Quadient return INVALID_PAYLOAD for Issue date after the valid from date" in {
      Json.stringify(errorResponseJson("Issue date after the valid from date", showErrorID = true)) must include(
        "INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for Issue date after the valid from date" in {
      Json.stringify(errorResponseJson("Issue date after the valid from date")) mustEqual """"Issue date after the valid from date""""
    }

    "for Quadient return EMAIL_NOT_VERIFIED for email address not provided" in {
      Json.stringify(errorResponseJson("email: email address not provided", showErrorID = true)) must include(
        "EMAIL_NOT_VERIFIED")
    }

    "for non Quadient not return EMAIL_NOT_VERIFIED for email address not provided" in {
      Json.stringify(errorResponseJson("email: email address not provided")) mustEqual """"email: email address not provided""""
    }

    "for Quadient return INVALID_PAYLOAD for Invalid alert queue submitted" in {
      Json.stringify(errorResponseJson("Invalid alert queue submitted", showErrorID = true)) must include(
        "INVALID_PAYLOAD")
    }

    "for non Quadient not return INVALID_PAYLOAD for Invalid alert queue submitted" in {
      Json.stringify(errorResponseJson("Invalid alert queue submitted")) mustEqual """"Invalid alert queue submitted""""
    }

    "for Quadient return TAXPAYER_NOT_FOUND for The backend has rejected the message due to not being able to find the tax payer" in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to not being able to find the tax payer",
          NOT_FOUND,
          showErrorID = true)) must include("TAXPAYER_NOT_FOUND")
    }

    "for non Quadient not return TAXPAYER_NOT_FOUND for The backend has rejected the message due to not being able to find the tax payer" in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to not being able to find the tax payer",
          NOT_FOUND)) mustEqual """"The backend has rejected the message due to not being able to find the tax payer""""
    }

    "for Quadient return EMAIL_NOT_VERIFIED for The backend has rejected the message due to not being able to verify the email address." in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to not being able to verify the email address.",
          NOT_FOUND,
          showErrorID = true)) must include("EMAIL_NOT_VERIFIED")
    }

    "for non Quadient not return EMAIL_NOT_VERIFIED for The backend has rejected the message due to not being able to verify the email address." in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to not being able to verify the email address.",
          NOT_FOUND)) mustEqual """"The backend has rejected the message due to not being able to verify the email address.""""
    }

    "for Quadient return SERVER_ERROR for Unauthorised" in {
      Json.stringify(errorResponseJson("Unauthorised", UNAUTHORIZED, showErrorID = true)) must include("SERVER_ERROR")
    }

    "for non Quadient not return SERVER_ERROR for Unauthorised" in {
      Json.stringify(errorResponseJson("Unauthorised", UNAUTHORIZED)) mustEqual """"Unauthorised""""
    }

    "for Quadient return CONFLICT for The backend has rejected the message due to duplicated message content or external reference ID." in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to duplicated message content or external reference ID.",
          CONFLICT,
          showErrorID = true)) must include("CONFLICT")
    }

    "for non Quadient not return CONFLICT for The backend has rejected the message due to duplicated message content or external reference ID." in {
      Json.stringify(
        errorResponseJson(
          "The backend has rejected the message due to duplicated message content or external reference ID.",
          CONFLICT)) mustEqual """"The backend has rejected the message due to duplicated message content or external reference ID.""""
    }

    "for Quadient return SERVER_ERROR for IF is currently experiencing problems that require live service intervention." in {
      Json.stringify(
        errorResponseJson(
          "IF is currently experiencing problems that require live service intervention.",
          INTERNAL_SERVER_ERROR,
          showErrorID = true)) must include("SERVER_ERROR")
    }

    "for non Quadient not return SERVER_ERROR for IF is currently experiencing problems that require live service intervention." in {
      Json.stringify(
        errorResponseJson(
          "IF is currently experiencing problems that require live service intervention.",
          INTERNAL_SERVER_ERROR)) mustEqual """"IF is currently experiencing problems that require live service intervention.""""
    }

    "for Quadient return SERVICE_UNAVAILABLE for Dependent systems are currently not responding." in {
      Json.stringify(errorResponseJson(
        "Dependent systems are currently not responding.",
        INTERNAL_SERVER_ERROR,
        showErrorID = true)) must include("SERVICE_UNAVAILABLE")
    }

    "for Non Quadient not return SERVICE_UNAVAILABLE for Dependent systems are currently not responding." in {
      Json.stringify(errorResponseJson("Dependent systems are currently not responding.", INTERNAL_SERVER_ERROR)) mustEqual """"Dependent systems are currently not responding.""""
    }
    "for Submission has not passed validation. Invalid payload. return BAD_REQUEST status code " in {
      val t: Result = errorResponseResult("Submission has not passed validation. Invalid payload.")
      t.header.status mustEqual BAD_REQUEST
    }

    "for invalid regime. Invalid payload. return BAD_REQUEST status code " in {
      val t: Result = errorResponseResult("could not parse body due to unmatched regimes")
      t.header.status mustEqual BAD_REQUEST
    }

    "for The backend has rejected the message due to not being able to find the tax payer. return NOT_FOUND status code " in {
      val t: Result = errorResponseResult(
        "The backend has rejected the message due to not being able to find the tax payer",
        NOT_FOUND)
      t.header.status mustEqual NOT_FOUND
    }
    "for Unauthorised. return status code UNAUTHORIZED" in {
      val t: Result = errorResponseResult("Unauthorised", UNAUTHORIZED)
      t.header.status mustEqual UNAUTHORIZED
    }
    "for The backend has rejected the message due to duplicated message content or external reference ID. return status code CONFLICT" in {
      val t: Result = errorResponseResult(
        "The backend has rejected the message due to duplicated message content or external reference ID.",
        CONFLICT)
      t.header.status mustEqual CONFLICT
    }
    "for SERVICE_UNAVAILABLE for Dependent systems are currently not responding. return INTERNAL_SERVER_ERROR status code" in {
      val t: Result = errorResponseResult("Dependent systems are currently not responding.", INTERNAL_SERVER_ERROR)
      t.header.status mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
