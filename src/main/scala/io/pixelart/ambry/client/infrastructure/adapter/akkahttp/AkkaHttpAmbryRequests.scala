package io.pixelart.ambry.client.infrastructure.adapter.akkahttp

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.ActorImplicits
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model.httpModel._

/**
 * Created by rabzu on 11/12/2016.
 */
protected[client] trait AkkaHttpAmbryRequests extends StrictLogging with ActorImplicits {

  private val healthCheckAddress = "healthCheck"

  def healthStatusHttpRequest(ambryUri: AmbryUri) =
    HttpRequest(uri = s"${ambryUri.uri}/$healthCheckAddress", method = HttpMethods.GET)

  //todo: 1. add user metadata support
  //todo: 2. make non required header fields Optional
  def uploadBlobHttpRequest(ambryUri: AmbryUri, uploadBlobData: UploadBlobRequestData): HttpRequest = {

    val sizeHeader = AmbryBlobSizeHeader(uploadBlobData.size)
    val serviceIdHeader = AmbryServiceIdHeader(uploadBlobData.serviceId)
    val contentTypeHeader = AmbryContentTypeHeader(uploadBlobData.contentType.mediaType.toString)
    val ttlHeader = AmbryTtlHeader(uploadBlobData.ttl)
    val privateHeader = AmbryPrivateHeader(uploadBlobData.prvt)
    val ownerIdHeader = AmbryOwnerIdHeader(uploadBlobData.ownerId)

    //fixme: for some reason Ambry does not recognise Custom Headers so I created Manually. its wiered because Spec shows equivalece of the two
    //val headersList = List(sizeHeader, serviceIdHeader, contentTypeHeader, ttlHeader, privateHeader, ownerIdHeader)

    val headersList = List(
      RawHeader(sizeHeader.name, sizeHeader.size.toString),
      RawHeader(serviceIdHeader.name(), serviceIdHeader.id.value),
      RawHeader(contentTypeHeader.name(), contentTypeHeader.value),
      RawHeader(ttlHeader.name(), ttlHeader.value),
      RawHeader(privateHeader.name(), privateHeader.value),
      RawHeader(ownerIdHeader.name(), ownerIdHeader.value)
    )

    val r = HttpRequest(
      uri = s"${ambryUri.uri}/",
      method = HttpMethods.POST,
      entity = HttpEntity.Default(uploadBlobData.contentType, uploadBlobData.size, uploadBlobData.blobSource)
    ).withHeaders(headersList)
    logger.info(r.toString())
    r

  }

  def getBlobHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"${ambryUri.uri}/${ambryId.value}", method = HttpMethods.GET)

  def getBlobInfoHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"${ambryUri.uri}/${ambryId.value}/BlobInfo", method = HttpMethods.GET)

  /*todo: complete
    def getUserMetadataHttpRequest(ambryUri: AmbryUri, ambryId: String @@ AmbryId ): HttpRequest =
    HttpRequest(uri = s"$ambryUri/$ambryId/BlobInfo")
  */
  //todo: complete: data format is not specidied in the Ambry docs
  //def modifiedSinceReuqest(ambryUri: AmbryUri, date: Date)

  //todo: not impleneted in the client side
  def getBlobPropertiesHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"$ambryUri/${ambryId.value}", method = HttpMethods.GET)

  def deleteBlobHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest =
    HttpRequest(uri = s"${ambryUri.uri}/${ambryId.value}", method = HttpMethods.DELETE)

}
