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

import java.time.{ Instant, LocalDate }
import org.mongodb.scala.bson.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.common.message.model.TaxEntity.{ Epaye, HmceVatdecOrg, HmrcCusOrg, HmrcIossOrg, HmrcPodsOrg, HmrcPodsPpOrg, HmrcPptOrg }
import uk.gov.hmrc.domain.TaxIds.TaxIdWithName
import uk.gov.hmrc.domain._
import uk.gov.hmrc.mongo.workitem.ProcessingStatus
import uk.gov.hmrc.mongo.workitem.ProcessingStatus.Implicits.format
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.Implicits.objectIdFormat

object MessageMongoFormats {

  import MongoTaxIdentifierFormats._
  object DetailsFormatter {
    implicit val format: OFormat[Details] = Json.format[Details]
  }

  object LocalDateFormatter {

    val localDateReads = Reads.DefaultLocalDateReads
    val localDateWrites = Writes.DefaultLocalDateWrites

    val localDateFormat: Format[LocalDate] =
      Format(localDateReads, localDateWrites)
  }

  implicit val messageMongoFormat: Format[Message] = {
    val legacyStatutoryForms = Seq("SA309A", "SA309C", "SA326D", "SA328D", "SA370", "SA371")

    def determineStatutoryFromForm = (__ \ "body" \ "form").readNullable[String].map {
      case Some(form) => legacyStatutoryForms.contains(form) || form.startsWith("SA316")
      case None       => false
    }

    // To Do: needs to check the value is a SaUtr
    def generateLegacyMessageHeaderDetail: Reads[RenderUrl] =
      for {
        messageId <- (__ \ "_id").read[ObjectId]
        taxEntity <- (__ \ "recipient").read[TaxEntity]
      } yield RenderUrl("sa-message-renderer", s"/messages/sa/${taxEntity.identifier.value}/${messageId.toString}")

    import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats.Implicits.jatInstantFormat
    import uk.gov.hmrc.common.message.model.EmailAlert._

    val reads1to21: Reads[
      (
        ObjectId,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[Instant],
        Option[Instant],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[Instant],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      )
    ] = (
      (__ \ "_id").read[ObjectId] and
        (__ \ "recipient").read[TaxEntity] and
        (__ \ "subject").read[String] and
        (__ \ "body").readNullable[Details](DetailsFormatter.format) and
        (__ \ "validFrom").read[LocalDate](LocalDateFormatter.localDateFormat) and
        (__ \ "alertFrom").readNullable[LocalDate](LocalDateFormatter.localDateFormat) and
        (__ \ "alertDetails").read[AlertDetails].orElse(Reads.pure(AlertDetails("newMessageAlert", None, Map()))) and
        (__ \ "alerts").readNullable[EmailAlert] and
        (__ \ "alertQueue").readNullable[String] and
        (__ \ "readTime").readNullable[Instant] and
        (__ \ "archiveTime").readNullable[Instant] and
        (__ \ "contentParameters").readNullable[MessageContentParameters] and
        (__ \ "status").read[ProcessingStatus] and
        (__ \ "rescindment").readNullable[Rescindment] and
        (__ \ "lastUpdated").readNullable[Instant] and
        (__ \ "hash").read[String] and
        (__ \ "statutory").read[Boolean].orElse(determineStatutoryFromForm) and
        (__ \ "renderUrl").read[RenderUrl].orElse(generateLegacyMessageHeaderDetail) and
        (__ \ "sourceData").readNullable[String] and
        (__ \ "externalRef").readNullable[ExternalRef] and
        (__ \ "content").readNullable[String]
    ).tupled
    val reads22to27: Reads[
      (
        Option[String],
        Option[Boolean],
        Option[Lifecycle],
        Option[Map[String, String]],
        Option[Instant],
        Option[MailgunStatus]
      )
    ] = (
      (__ \ "emailAlertEventUrl").readNullable[String] and
        (__ \ "verificationBrake").readNullable[Boolean] and
        (__ \ "lifecycle").readNullable[Lifecycle] and
        (__ \ "tags").readNullable[Map[String, String]] and
        (__ \ "deliveredOn").readNullable[Instant] and
        (__ \ "mailgunStatus").readNullable[MailgunStatus]
    ).tupled
    val tupleToMessage: (
      (
        ObjectId,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[Instant],
        Option[Instant],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[Instant],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      ),
      (
        Option[String],
        Option[Boolean],
        Option[Lifecycle],
        Option[Map[String, String]],
        Option[Instant],
        Option[MailgunStatus]
      )
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
            (emailAlertEventUrl, verificationBrake, lifecycle, tags, deliveredOn, mailgunStatus)
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
          deliveredOn,
          mailgunStatus
        )
    }

    val messageToTuple: Message => (
      (
        ObjectId,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[Instant],
        Option[Instant],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[Instant],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      ),
      (
        Option[String],
        Option[Boolean],
        Option[Lifecycle],
        Option[Map[String, String]],
        Option[Instant],
        Option[MailgunStatus]
      )
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
        (
          message.emailAlertEventUrl,
          message.verificationBrake,
          message.lifecycle,
          message.tags,
          message.deliveredOn,
          message.mailgunStatus
        )
      )
    }

    val reads: Reads[Message] = (reads1to21 and reads22to27) {
      tupleToMessage
    }

    val writes1to21: OWrites[
      (
        ObjectId,
        TaxEntity,
        String,
        Option[Details],
        LocalDate,
        Option[LocalDate],
        AlertDetails,
        Option[EmailAlert],
        Option[String],
        Option[Instant],
        Option[Instant],
        Option[MessageContentParameters],
        ProcessingStatus,
        Option[Rescindment],
        Option[Instant],
        String,
        Boolean,
        RenderUrl,
        Option[String],
        Option[ExternalRef],
        Option[String]
      )
    ] = (
      (__ \ "_id").write[ObjectId] and
        (__ \ "recipient").write[TaxEntity] and
        (__ \ "subject").write[String] and
        (__ \ "body").writeNullable[Details](DetailsFormatter.format) and
        (__ \ "validFrom").write[LocalDate](LocalDateFormatter.localDateWrites) and
        (__ \ "alertFrom").writeNullable[LocalDate](LocalDateFormatter.localDateWrites) and
        (__ \ "alertDetails").write[AlertDetails] and
        (__ \ "alerts").writeNullable[EmailAlert] and
        (__ \ "alertQueue").writeNullable[String] and
        (__ \ "readTime").writeNullable[Instant] and
        (__ \ "archiveTime").writeNullable[Instant] and
        (__ \ "contentParameters").writeNullable[MessageContentParameters] and
        (__ \ "status").write[ProcessingStatus] and
        (__ \ "rescindment").writeNullable[Rescindment] and
        (__ \ "lastUpdated").writeNullable[Instant] and
        (__ \ "hash").write[String] and
        (__ \ "statutory").write[Boolean] and
        (__ \ "renderUrl").write[RenderUrl] and
        (__ \ "sourceData").writeNullable[String] and
        (__ \ "externalRef").writeNullable[ExternalRef] and
        (__ \ "content").writeNullable[String]
    ).tupled

    val writes22to27: OWrites[
      (
        Option[String],
        Option[Boolean],
        Option[Lifecycle],
        Option[Map[String, String]],
        Option[Instant],
        Option[MailgunStatus]
      )
    ] = (
      (__ \ "emailAlertEventUrl").writeNullable[String] and
        (__ \ "verificationBrake").writeNullable[Boolean] and
        (__ \ "lifecycle").writeNullable[Lifecycle] and
        (__ \ "tags").writeNullable[Map[String, String]] and
        (__ \ "deliveredOn").writeNullable[Instant] and
        (__ \ "mailgunStatus").writeNullable[MailgunStatus]
    ).tupled

    val writes: OWrites[Message] = (writes1to21 ~ writes22to27) {
      messageToTuple
    }

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
    ((__ \ "name").read[String] and (__ \ "value").read[String]).tupled.flatMap[TaxIdWithName] { case (name, value) =>
      (TaxIds.defaultSerialisableIds :+ SerialisableTaxId("EMPREF", Epaye.apply)
        :+ SerialisableTaxId("HMCE-VATDEC-ORG", HmceVatdecOrg.apply)
        :+ SerialisableTaxId("HMRC-CUS-ORG", HmrcCusOrg.apply)
        :+ SerialisableTaxId("ETMPREGISTRATIONNUMBER", HmrcPptOrg.apply)
        :+ SerialisableTaxId("HMRC-IOSS-ORG", HmrcIossOrg.apply)
        :+ SerialisableTaxId("PSAID", HmrcPodsOrg.apply)
        :+ SerialisableTaxId("PSPID", HmrcPodsPpOrg.apply))
        .find(_.taxIdName == name)
        .map(_.build(value)) match {
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
