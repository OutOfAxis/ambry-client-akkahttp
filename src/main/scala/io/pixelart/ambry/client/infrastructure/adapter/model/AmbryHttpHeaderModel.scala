package io.pixelart.ambry.client.infrastructure.adapter.model

import akka.http.scaladsl.model.headers.{ModeledCustomHeaderCompanion, ModeledCustomHeader}
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
    **/
  final class BlobSizeHeader(size: Long) extends ModeledCustomHeader[BlobSizeHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryServiceIdHeader

    override def value: String = size.toString
  }

  object BlobSizeHeader extends ModeledCustomHeaderCompanion[BlobSizeHeader] {
    override val name = "x-ambry-blob-size"

    override def parse(value: String) = Try(new BlobSizeHeader(value.toLong))
  }


  /**
    * header:
    * "x-ambry-service-id": String
    *
    * The ID of the service that is uploading the blob
    **/
  final class AmbryServiceIdHeader(id: String) extends ModeledCustomHeader[AmbryServiceIdHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryServiceIdHeader

    override def value: String = id
  }

  object AmbryServiceIdHeader extends ModeledCustomHeaderCompanion[AmbryServiceIdHeader] {
    override val name = "x-ambry-service-id"

    override def parse(value: String) = Try(new AmbryServiceIdHeader(value))
  }


  /**
    * header:
    *
    * "x-ambry-content-type": String
    *
    * The type of content in the blob
    *
    * todo: replace string content type with akka-http content type
    **/
  final class AmbryContentTypeHeader(contentType: String) extends ModeledCustomHeader[AmbryContentTypeHeader] {
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
    * "x-ambry-ttl": Long
    *
    * The time in seconds for which the blob is valid. Defaults to -1 (infinite validity)
    **/
  final class AmbryTtllHeader(ttl: Long) extends ModeledCustomHeader[AmbryTtllHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryTtllHeader

    override def value: String = ttl.toString
  }

  object AmbryTtllHeader extends ModeledCustomHeaderCompanion[AmbryTtllHeader] {
    override val name = "x-ambry-ttl"

    override def parse(value: String) = Try(new AmbryTtllHeader(value.toLong))
  }


  /**
    * header:
    * "x-ambry-private": Boolean
    *
    * Makes the blob private if set to true. Defaults to false (blob is public)
    **/
  final class AmbryPrivatelHeader(ttl: Long) extends ModeledCustomHeader[AmbryPrivatelHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryPrivatelHeader

    override def value: String = ttl.toString
  }

  object AmbryPrivatelHeader extends ModeledCustomHeaderCompanion[AmbryPrivatelHeader] {
    override val name = "x-ambry-private"

    override def parse(value: String) = Try(new AmbryPrivatelHeader(value.toLong))
  }

  /**
    * header:
    * "x-ambry-owner-id": String
    *
    * The owner of the blob.
    **/
  final class AmbryOwnerIdHeader(ownerId: String) extends ModeledCustomHeader[AmbryOwnerIdHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryOwnerIdHeader

    override def value: String = ownerId
  }

  object AmbryOwnerIdHeader extends ModeledCustomHeaderCompanion[AmbryOwnerIdHeader] {
    override val name = "x-ambry-owner-id:"

    override def parse(value: String) = Try(new AmbryOwnerIdHeader(value))
  }


  /**
    * header:
    * "x-ambry-um-": String
    *
    * User metadata headers prefix. Any number of headers with this prefix are allowed.1
    **/
  final class AmbryUserMetadataHeader(ownerId: String) extends ModeledCustomHeader[AmbryUserMetadataHeader] {
    override def renderInRequests = false

    override def renderInResponses = false

    override val companion = AmbryUserMetadataHeader

    override def value: String = ownerId
  }

  object AmbryUserMetadataHeader extends ModeledCustomHeaderCompanion[AmbryUserMetadataHeader] {
    override val name = "x-ambry-um-"

    override def parse(value: String) = Try(new AmbryUserMetadataHeader(value))
  }

}
