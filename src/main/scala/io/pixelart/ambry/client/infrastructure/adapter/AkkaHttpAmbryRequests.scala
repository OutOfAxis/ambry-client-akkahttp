package io.pixelart.ambry.client.infrastructure.adapter

import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.tagging._
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.config.AmbryId
import io.pixelart.ambry.client.application.config._
import io.pixelart.ambry.client.domain.model.{Blob, AmbryId}
import io.pixelart.ambry.client.infrastructure.adapter.model.AmbryHttpHeaderModel._

import scala.collection.parallel.immutable


/**
  * Created by rabzu on 11/12/2016.
  */
trait AkkaHttpAmbryRequests extends StrictLogging with ActorImplicits {

  private val healthCheckAddress = "healthCheck"


  def healthStatusHttpRequest(ambryUri: String @@ AmbryUri) =
    HttpRequest(uri = s"$ambryUri/$healthCheckAddress", method = HttpMethods.GET)

  //todo: 1. add user metadata support
  //todo: 2. make non required header fields Optional
  def uploadBlobHttpRequest(ambryUri: String @@ AmbryUri,
                            blobSource: Blob,
                            size: Long @@ AmbryBlobSize,
                            serviceId: String @@ AmbryServiceId,
                            contentType: ContentType,
                            ttl: Long @@ AmbryTtl,
                            prvt: Boolean = false,
                            ownerId: String @@ AmbryOwnerId): HttpRequest = {


    val sizeHeader = new AmbryBlobSizeHeader(size)
    val serviceIdHeader = new AmbryServiceIdHeader(serviceId)
    val contentTypeHeader = new AmbryContentTypeHeader(contentType.mediaType.toString
    val ttlHeader = new AmbryTtlHeader(ttl)
    val privateHeader = new AmbryPrivatelHeader(prvt)
    val ownerIdHeader = new AmbryOwnerIdHeader(ownerId)

    HttpRequest(uri = s"$ambryUri/",
      headers = List(sizeHeader, serviceIdHeader, contentTypeHeader, ttlHeader, privateHeader, ownerIdHeader),
      method = HttpMethods.POST,
      //todo:can we use HttpEntity.Chunked?
      entity = HttpEntity.Default(contentType, size, blobSource.data))
  }

  def getBlobHttpRequest(ambryUri: String @@ AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"$ambryUri/${ambryId.value}", method = HttpMethods.GET)

  def getBlobInfoHttpRequest(ambryUri: String @@ AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"$ambryUri/${ambryId.value}/BlobInfo", method = HttpMethods.GET)

  /*todo: complete
    def getUserMetadataHttpRequest(ambryUri: String @@ AmbryUri, ambryId: String @@ AmbryId ): HttpRequest =
    HttpRequest(uri = s"$ambryUri/$ambryId/BlobInfo")
  */
  //todo: complete: data format is not specidied in the Ambry docs
  //def modifiedSinceReuqest(ambryUri: String @@ AmbryUri, date: Date)


  def getBlobProperties(ambryUri: String @@ AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"$ambryUri/${ambryId.value}", method = HttpMethods.GET)

  def deleteBlob(ambryUri: String @@ AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"$ambryUri/${ambryId.value}", method = HttpMethods.DELETE)


}
