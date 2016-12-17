package io.pixelart.ambry.client.application.config

/**
  * Created by rabzu on 11/12/2016.
  */

import java.nio.file.Path
import akka.http.scaladsl.model.ContentType
import akka.stream.IOResult
import io.pixelart.ambry.client.domain.model._
import scala.concurrent.Future

trait AbstractAmbryClientService {

  /**
    * Check the status of the frontend
    * @return the response which contains code and status
    */
  def healthCheck: Future[AmbryHealthStatusResponse]

  /**
    * Get the file properties represented by the ambryId
    * @param ambryId the file's ambryId
    * @return file's info of the given resource id
    */
  def getFileProperty(ambryId: AmbryId): Future[AmbryBlobInfoResponse]

  /**
    * Get the file's user metadata represented by the ambryId
    * @param ambryId the file's ambryId
    * @return file's user metadata of the given resource id
    */
  //  def getFileUserMetadata(ambryId: AmbryId): AmbryUMResponse

  /**
    * Get the file with the given resource id
    * @param ambryId the file's ambryId
    * @return the file object
    */
  def getFile(ambryId: AmbryId): Future[AmbryGetBlobResponse]

  /**
    * Get the file with given resource id, and put into the local file object.
    * @param ambryId the resource id
    * @param localPath save the download file as local
    * @return the file object and result
    */
  def getFile(ambryId: AmbryId, localPath: Path): Future[IOResult]

  /**
    *
    * @param localPath
    * @param serviceId
    * @param contentType
    * @param ttl
    * @param prvt
    * @param ownerId
    * @return
    */
  def postFileFromPath(localPath: Path,
                       serviceId: AmbryServiceId,
                       contentType: ContentType,
                       ttl: Long = -1,
                       prvt: Boolean = false,
                       ownerId: AmbryOwnerId): Future[AmbryBlobUploadResponse]

  /**
    *
    * @param uploadBlobRequestData
    * @return
    */
  def postFile(uploadBlobRequestData: UploadBlobRequestData): Future[AmbryBlobUploadResponse]


  /**
    *
    * @param ambryId
    * @return
    */
  def deleteFile(ambryId: AmbryId): Future[Boolean]

}
