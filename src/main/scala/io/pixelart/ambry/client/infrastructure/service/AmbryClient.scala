package io.pixelart.ambry.client.infrastructure.service

import java.io.File

import com.softwaremill.tagging.@@
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.config.{ AbstractAmbryClient, AmbryPort, AmbryHostname }
import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.domain.model.response._

import scala.concurrent.Future

/**
 * Created by rabzu on 11/12/2016.
 */

class AmbryClient(hostname: String @@ AmbryHostname, port: String @@ AmbryPort = 1174) extends StrictLogging {
  //with AbstractAmbryClient

  /**
   * Check the status of the frontend
   * @return the response which contains code and status
   */
  def healthCheck: Future[AmbryHealthStatusResponse] = ???

  /**
   * Get the file properties represented by the ambryId
   * @param ambryId the file's ambryId
   * @return file's info of the given resource id
   */
  def getFileProperty(ambryId: AmbryId): AmbryBlobInfoResponse = ???

  /**
   * Get the file's user metadata represented by the ambryId
   * @param ambryId the file's ambryId
   * @return file's user metadata of the given resource id
   */
//  def getFileUserMetadata(ambryId: AmbryId): AmbryUMResponse = ???

  /**
   * Get the file with the given resource id
   * @param ambryId the file's ambryId
   * @return the file object
   */
  def getFile(ambryId: AmbryId): AmbryGetBlobResponse = ???

  /**
   * Get the file with given resource id, and put into the local file object.
   * @param ambryId the resource id
   * @param localFile save the download file as local
   * @return the file object and result
   */
  def getFile(ambryId: String, localFile: File): AmbryGetBlobResponse = ???

  /**
   * Upload a file with the file's path.
   * @param filePath file's path at local.
   * @param fileType file type string, e.g.: "text/plain", "image/jpg"
   * @return information with save status and the resource id, i.e. ambryId, response from Amby
   */
  def postFile(filePath: String, fileType: String): AmbryPostFileResponse = ???

  /**
   * Upload a file
   * @param file the file which will be uploaded
   * @param fileType file type string, e.g.: "text/plain", "image/jpg"
   * @return save status and the resource id response from Ambry
   */
  def postFile(file: File, fileType: String): AmbryPostFileResponse = ???

  /**
   * Upload a file with bytes
   * @param blob the file bytes which will be uploaded
   * @param fileType file type string, e.g.: "text/plain", "image/jpg"
   * @return save status and the resource id response from Ambry
   */

  def postFile(blob: Blob, fileType: String): AmbryPostFileResponse = ???

  /**
   * Delete a file with a given resource Id
   * @param ambryId resource id of the target file
   * @return remove status
   */
  def deleteFile(ambryId: AmbryId): Future[Boolean] = ???

}
