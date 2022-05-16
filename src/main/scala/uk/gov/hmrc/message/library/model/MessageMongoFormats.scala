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

package uk.gov.hmrc.message.library.model

import org.joda.time.{ DateTime, LocalDate }
import play.api.libs.functional.syntax._
import play.api.libs.json.{ Json, _ }
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.domain._
import TaxEntity.{ Epaye, HmceVatdecOrg, HmrcCusOrg, HmrcPptOrg }
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites.{ JodaDateTimeWrites => _, _ }

object MessageMongoFormats {

  import MongoTaxIdentifierFormats._

  implicit val dateTimeFormats: Format[DateTime] = ReactiveMongoFormats.dateTimeFormats
  implicit val objectIdFormats: Format[BSONObjectID] = ReactiveMongoFormats.objectIdFormats

  implicit val messageMongoFormat: Format[Message] = ReactiveMongoFormats.mongoEntity {
    val legacyStatutoryForms = Seq("SA309A", "SA309C", "SA326D", "SA328D", "SA370", "SA371")

    def determineStatutoryFromForm = (__ \ "body" \ "form").readNullable[String].map {
      case Some(form) => legacyStatutoryForms.contains(form) || form.startsWith("SA316")
      case None       => false
    }

    // To Do: needs to check the value is a SaUtr
    def generateLegacyMessageHeaderDetail: Reads[RenderUrl] =
      for {
        messageId <- (__ \ "id").read[BSONObjectID]
        taxEntity <- (__ \ "recipient").read[TaxEntity]
      } yield RenderUrl("sa-message-renderer", s"/messages/sa/${taxEntity.identifier.value}/${messageId.stringify}")

    val reads1to21: Reads[
      (
        BSONObjectID,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[DateTime],
        Option[DateTime],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[DateTime],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      )
    ] = (
      (__ \ "id").read[BSONObjectID] and
        (__ \ "recipient").read[TaxEntity] and
        (__ \ "subject").read[String] and
        (__ \ "body").readNullable[Details] and
        (__ \ "validFrom").read[LocalDate] and
        (__ \ "alertFrom").readNullable[LocalDate] and
        (__ \ "alertDetails").read[AlertDetails].orElse(Reads.pure(AlertDetails("newMessageAlert", None, Map()))) and
        (__ \ "alerts").readNullable[EmailAlert] and
        (__ \ "alertQueue").readNullable[String] and
        (__ \ "readTime").readNullable[DateTime] and
        (__ \ "archiveTime").readNullable[DateTime] and
        (__ \ "contentParameters").readNullable[MessageContentParameters] and
        (__ \ "status").read[ProcessingStatus] and
        (__ \ "rescindment").readNullable[Rescindment] and
        (__ \ "lastUpdated").readNullable[DateTime] and
        (__ \ "hash").read[String] and
        (__ \ "statutory").read[Boolean].orElse(determineStatutoryFromForm) and
        (__ \ "renderUrl").read[RenderUrl].orElse(generateLegacyMessageHeaderDetail) and
        (__ \ "sourceData").readNullable[String] and
        (__ \ "externalRef").readNullable[ExternalRef] and
        (__ \ "content").readNullable[String]
    ).tupled

    val reads22to26
      : Reads[(Option[String], Option[Boolean], Option[Lifecycle], Option[Map[String, String]], Option[DateTime])] = (
      (__ \ "emailAlertEventUrl").readNullable[String] and
        (__ \ "verificationBrake").readNullable[Boolean] and
        (__ \ "lifecycle").readNullable[Lifecycle] and
        (__ \ "tags").readNullable[Map[String, String]] and
        (__ \ "deliveredOn").readNullable[DateTime]
    ).tupled

    val tupleToMessage: (
      (
        BSONObjectID,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[DateTime],
        Option[DateTime],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[DateTime],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      ),
      (Option[String], Option[Boolean], Option[Lifecycle], Option[Map[String, String]], Option[DateTime])
    ) => Message = {
      case (
          (
            id,
            recipient,
            subject,
            body,
            validFrom,
            alertFrom,
            alertDetails,
            alerts,
            alertQueue,
            readTime,
            archiveTime,
            contentParameters,
            status,
            rescindment,
            lastUpdated,
            hash,
            statutory,
            renderUrl,
            sourceData,
            externalRef,
            content
          ),
          (emailAlertEventUrl, verificationBrake, lifecycle, tags, deliveredOn)
          ) =>
        Message(
          id,
          recipient,
          subject,
          body,
          validFrom,
          alertFrom,
          alertDetails,
          alerts,
          alertQueue,
          readTime,
          archiveTime,
          contentParameters,
          status,
          rescindment,
          lastUpdated,
          hash,
          statutory,
          renderUrl,
          sourceData,
          externalRef,
          content,
          emailAlertEventUrl,
          verificationBrake,
          lifecycle,
          tags,
          deliveredOn
        )
    }

    val messageToTuple: Message => (
      (
        BSONObjectID,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[DateTime],
        Option[DateTime],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[DateTime],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      ),
      (Option[String], Option[Boolean], Option[Lifecycle], Option[Map[String, String]], Option[DateTime])
    ) = { message =>
      (
        (
          message.id,
          message.recipient,
          message.subject,
          message.body,
          message.validFrom,
          message.alertFrom,
          message.alertDetails,
          message.alerts,
          message.alertQueue,
          message.readTime,
          message.archiveTime,
          message.contentParameters,
          message.status,
          message.rescindment,
          message.lastUpdated,
          message.hash,
          message.statutory,
          message.renderUrl,
          message.sourceData,
          message.externalRef,
          message.content
        ),
        (message.emailAlertEventUrl, message.verificationBrake, message.lifecycle, message.tags, message.deliveredOn)
      )
    }

    val reads: Reads[Message] = (reads1to21 and reads22to26) { tupleToMessage }

    val writes1to21: OWrites[
      (
        BSONObjectID,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[DateTime],
        Option[DateTime],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[DateTime],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      )
    ] = (
      (__ \ "id").write[BSONObjectID] and
        (__ \ "recipient").write[TaxEntity] and
        (__ \ "subject").write[String] and
        (__ \ "body").writeNullable[Details] and
        (__ \ "validFrom").write[LocalDate] and
        (__ \ "alertFrom").writeNullable[LocalDate] and
        (__ \ "alertDetails").write[AlertDetails] and
        (__ \ "alerts").writeNullable[EmailAlert] and
        (__ \ "alertQueue").writeNullable[String] and
        (__ \ "readTime").writeNullable[DateTime] and
        (__ \ "archiveTime").writeNullable[DateTime] and
        (__ \ "contentParameters").writeNullable[MessageContentParameters] and
        (__ \ "status").write[ProcessingStatus] and
        (__ \ "rescindment").writeNullable[Rescindment] and
        (__ \ "lastUpdated").writeNullable[DateTime] and
        (__ \ "hash").write[String] and
        (__ \ "statutory").write[Boolean] and
        (__ \ "renderUrl").write[RenderUrl] and
        (__ \ "sourceData").writeNullable[String] and
        (__ \ "externalRef").writeNullable[ExternalRef] and
        (__ \ "content").writeNullable[String]
    ).tupled

    val writes22to26
      : OWrites[(Option[String], Option[Boolean], Option[Lifecycle], Option[Map[String, String]], Option[DateTime])] = (
      (__ \ "emailAlertEventUrl").writeNullable[String] and
        (__ \ "verificationBrake").writeNullable[Boolean] and
        (__ \ "lifecycle").writeNullable[Lifecycle] and
        (__ \ "tags").writeNullable[Map[String, String]] and
        (__ \ "deliveredOn").writeNullable[DateTime]
    ).tupled

    val writes: OWrites[Message] = (writes1to21 ~ writes22to26) { messageToTuple }

    Format(reads, writes)
  }

  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]] {
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) => implicitly[Writes[T]].writes(t)
      case None    => JsNull
    }
  }
}

object MongoTaxIdentifierFormats {

  val taxIdentifierReads: Reads[TaxIdWithName] =
    ((__ \ "name").read[String] and (__ \ "value").read[String]).tupled.flatMap[TaxIdWithName] {
      case (name, value) =>
        (TaxIds.defaultSerialisableIds :+ SerialisableTaxId("empRef", Epaye.apply)
          :+ SerialisableTaxId("HMCE-VATDEC-ORG", HmceVatdecOrg.apply)
          :+ SerialisableTaxId("HMRC-CUS-ORG", HmrcCusOrg.apply)
          :+ SerialisableTaxId("ETMPREGISTRATIONNUMBERG", HmrcPptOrg.apply))
          .find(_.taxIdName == name)
          .map { _.build(value) } match {
          case Some(taxIdWithName) =>
            Reads[TaxIdWithName] { _ =>
              JsSuccess(taxIdWithName)
            }
          case None =>
            Reads[TaxIdWithName] { _ =>
              JsError(s"could not determine tax id with name = $name and value = $value")
            }
        }
    }

  val taxIdentifierWrites: Writes[TaxIdWithName] = Writes[TaxIdWithName] { taxId =>
    Json.obj("name" -> taxId.name, "value" -> taxId.value)
  }

  implicit val mongoTaxIdentifierFormat: Format[TaxIdWithName] =
    Format(taxIdentifierReads, taxIdentifierWrites)
}
