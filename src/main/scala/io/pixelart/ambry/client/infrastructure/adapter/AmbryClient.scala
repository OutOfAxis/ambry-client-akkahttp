package io.pixelart.ambry.client.infrastructure.adapter

import io.pixelart.ambry.client.domain.model._

import scala.concurrent.Future

/**
 * Created by rabzu on 17/12/2016.
 */

//todo: make return type more general
trait AmbryClient {

  def healthCheckRequest: Future[AmbryHealthStatusResponse]

  def uploadBlobRequest(uploadData: UploadBlobRequestData): Future[AmbryBlobUploadResponse]

  def getBlobRequest(ambryId: AmbryId): Future[AmbryGetBlobResponse]

  def getBlobInfoRequest(ambryId: AmbryId): Future[AmbryBlobInfoResponse]

  def deleteBlobRequest(ambryId: AmbryId): Future[Boolean]

}
