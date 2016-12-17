package io.pixelart.ambry.client.infrastructure.service

import java.nio.file.{Files, Path}
import akka.http.scaladsl.model.ContentType
import akka.stream.IOResult
import akka.stream.scaladsl.{Source, FileIO}
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.config.AbstractAmbryClientService
import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.infrastructure.adapter.{AmbryClient}
import scala.concurrent.Future

/**
  * Created by rabzu on 11/12/2016.
  */
class AmbryService(ambryClient: AmbryClient, ambryUri: AmbryUri) extends AbstractAmbryClientService with StrictLogging {


  override def healthCheck: Future[AmbryHealthStatusResponse] =
    ambryClient.healthCheckRequest


  override def getFileProperty(ambryId: AmbryId): Future[AmbryBlobInfoResponse] =
    ambryClient.getBlobInfoRequest(ambryId)


  //todo: override def getFileUserMetadata(ambryId: AmbryId): AmbryUMResponse = ???


  override def getFile(ambryId: AmbryId): Future[AmbryGetBlobResponse] =
    ambryClient.getBlobRequest(ambryId)


  override def getFile(ambryId: AmbryId, localPath: Path): Future[IOResult] =
    ambryClient
      .getBlobRequest(ambryId)
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
                                 ownerId: AmbryOwnerId): Future[AmbryBlobUploadResponse] = {

    val fileSource = FileIO.fromPath(localPath)
    val size = Files.size(localPath)
    ambryClient.uploadBlobRequest(UploadBlobRequestData(fileSource, size, serviceId, contentType, ttl, prvt, ownerId))
  }


  override def postFile(uploadBlobRequestData: UploadBlobRequestData): Future[AmbryBlobUploadResponse] =
    ambryClient.uploadBlobRequest(uploadBlobRequestData)


  override def deleteFile(ambryId: AmbryId): Future[Boolean] =
    ambryClient.deleteBlobRequest(ambryId)
}
