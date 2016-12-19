package io.pixelart.ambry.client.domain.model

import akka.http.scaladsl.model.headers.{ ModeledCustomHeader, ModeledCustomHeaderCompanion }
import com.github.nscala_time.time.Imports.DateTime
import io.pixelart.ambry.client.domain.model.httpModel._
import scala.util.Try

/**
 * Created by rabzu on 11/12/2016.
 */
object AmbryHttpHeaderModel {

  /**
   * header:
   * "x-ambry-blob-size": Long
   *
   * The size of the blob being uploaded
   */
  final case class AmbryBlobSizeHeader(size: Long) extends ModeledCustomHeader[AmbryBlobSizeHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryBlobSizeHeader

    override def value: String = size.toString
  }

  object AmbryBlobSizeHeader extends ModeledCustomHeaderCompanion[AmbryBlobSizeHeader] {
    override val name = "x-ambry-blob-size"

    override def parse(value: String) = Try(AmbryBlobSizeHeader(value.toLong))
  }

  /**
   * header:
   * "x-ambry-service-id": String
   *
   * The ID of the service that is uploading the blob
   */
  final case class AmbryServiceIdHeader(id: AmbryServiceId) extends ModeledCustomHeader[AmbryServiceIdHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryServiceIdHeader

    override def value: String = id.value
  }

  object AmbryServiceIdHeader extends ModeledCustomHeaderCompanion[AmbryServiceIdHeader] {
    override val name = "x-ambry-service-id"

    override def parse(value: String) = Try(AmbryServiceIdHeader(AmbryServiceId(value)))
  }

  final case class AmbryCreationTimeHeader(date: DateTime) extends ModeledCustomHeader[AmbryCreationTimeHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryCreationTimeHeader

    override def value: String = date.toString
    //    override def value: String = date.getMillis.toString
  }

  object AmbryCreationTimeHeader extends ModeledCustomHeaderCompanion[AmbryCreationTimeHeader] {
    override val name = "x-ambry-creation-time"

    override def parse(value: String) = Try(AmbryCreationTimeHeader(new DateTime(value.toLong)))
  }

  /**
   * header:
   *
   * "x-ambry-content-type": String
   *
   * The type of content in the blob
   *
   * todo: replace string content type with akka-http content type
   */
  final case class AmbryContentTypeHeader(contentType: String) extends ModeledCustomHeader[AmbryContentTypeHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryContentTypeHeader

    override def value: String = contentType
  }

  object AmbryContentTypeHeader extends ModeledCustomHeaderCompanion[AmbryContentTypeHeader] {
    override val name = "x-ambry-content-type"

    override def parse(value: String) = Try(new AmbryContentTypeHeader(value))
  }

  /**
   * header:
   * "x-ambry-ttl": DatTime
   *
   * The time in seconds for which the blob is valid. Defaults to -1 (infinite validity)
   */
  final case class AmbryTtlHeader(ttl: Long) extends ModeledCustomHeader[AmbryTtlHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryTtlHeader

    override def value: String = ttl.toString
  }

  object AmbryTtlHeader extends ModeledCustomHeaderCompanion[AmbryTtlHeader] {
    override val name = "x-ambry-ttl"

    override def parse(value: String) = Try(AmbryTtlHeader(value.toLong))
  }

  /**
   * header:
   * "x-ambry-private": Boolean
   *
   * Makes the blob private if set to true. Defaults to false (blob is public)
   */
  final case class AmbryPrivateHeader(prvt: Boolean) extends ModeledCustomHeader[AmbryPrivateHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryPrivateHeader

    override def value: String = prvt.toString
  }

  object AmbryPrivateHeader extends ModeledCustomHeaderCompanion[AmbryPrivateHeader] {
    override val name = "x-ambry-private"

    override def parse(value: String) = Try(new AmbryPrivateHeader(value.toBoolean))
  }

  /**
   * header:
   * "x-ambry-owner-id": String
   *
   * The owner of the blob.
   */
  final case class AmbryOwnerIdHeader(ownerId: AmbryOwnerId) extends ModeledCustomHeader[AmbryOwnerIdHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryOwnerIdHeader

    override def value: String = ownerId.value
  }

  object AmbryOwnerIdHeader extends ModeledCustomHeaderCompanion[AmbryOwnerIdHeader] {
    override val name = "x-ambry-owner-id"

    override def parse(value: String) = Try(new AmbryOwnerIdHeader(AmbryOwnerId(value)))
  }

  /**
   * header:
   * "x-ambry-um-": String
   *
   * User metadata headers prefix. Any number of headers with this prefix are allowed
   *
   */
  //todo: Add this. needs changing
  /*final case class AmbryUserMetadataHeader(k: String, v: String) extends ModeledCustomHeader[AmbryUserMetadataHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryUserMetadataHeader

    override def value: String = v
  }

  object AmbryUserMetadataHeader extends ModeledCustomHeaderCompanion[AmbryUserMetadataHeader] {
    override val name = "x-ambry-um-"

    override def parse(value: String) = Try(new AmbryUserMetadataHeader(value))
  }
*/

  /**
   * header:
   * "x-ambry-owner-id": String
   *
   * The owner of the blob.
   */
  final case class AmbryFailureIdHeader(failure: Boolean) extends ModeledCustomHeader[AmbryFailureIdHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryFailureIdHeader

    override def value: String = failure.toString
  }

  object AmbryFailureIdHeader extends ModeledCustomHeaderCompanion[AmbryFailureIdHeader] {
    override val name = "x-ambry-failure-reason"

    override def parse(value: String) = Try(new AmbryFailureIdHeader(value.toBoolean))
  }

}
