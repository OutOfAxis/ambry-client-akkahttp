package io.pixelart.ambry.client.infrastructure.service

import java.nio.file.{ Files, Path }

import akka.NotUsed
import akka.http.scaladsl.model.ContentType
import akka.stream.IOResult
import akka.stream.scaladsl.{ FileIO, Source }
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.{ AbstractAmbryClientService, ActorImplicits }
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.AmbryClient

import scala.concurrent.Future

/**
 * Created by rabzu on 11/12/2016.
 */
protected[client] trait AmbryService extends AbstractAmbryClientService with StrictLogging with ActorImplicits {
  this: AmbryClient =>

  private[client] val ambryUri: AmbryUri

  override def healthCheck: Future[AmbryHealthStatusResponse] =
    healthCheckRequest

  override def getFileProperty(ambryId: AmbryId): Future[AmbryBlobInfoResponse] =
    getBlobInfoRequest(ambryId)

  //todo: override def getFileUserMetadata(ambryId: AmbryId): AmbryUMResponse = ???

  override def getFile(ambryId: AmbryId): Future[AmbryGetBlobResponse] =
    getBlobRequest(ambryId)

  override def getStreamedFile(ambryId: AmbryId, chunkSize: Long = 100000): Future[Source[AmbryGetBlobResponse, NotUsed]] =
    getBlobRequestStreamed(ambryId, chunkSize)

  override def getFile(ambryId: AmbryId, localPath: Path): Future[IOResult] =
    getBlobRequest(ambryId)
      .flatMap { result =>
        result
          .blob
          .runWith(FileIO.toPath(localPath))
      }

  //todo: ttl to DateTime
  override def postFileFromPath(
    localPath: Path,
    serviceId: AmbryServiceId,
    contentType: ContentType,
    ttl: Long = -1,
    prvt: Boolean = false,
    ownerId: AmbryOwnerId
  ): Future[AmbryBlobUploadResponse] = {

    val fileSource = FileIO.fromPath(localPath)
    val size = Files.size(localPath)
    uploadBlobRequest(UploadBlobRequestData(fileSource, size, serviceId, contentType, ttl, prvt, ownerId))
  }

  override def postFile(uploadBlobRequestData: UploadBlobRequestData): Future[AmbryBlobUploadResponse] =
    uploadBlobRequest(uploadBlobRequestData)

  override def deleteFile(ambryId: AmbryId): Future[Boolean] =
    deleteBlobRequest(ambryId)
}
