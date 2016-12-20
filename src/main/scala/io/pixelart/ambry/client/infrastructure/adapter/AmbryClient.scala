package io.pixelart.ambry.client.infrastructure.adapter

import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.domain.model.httpModel._

import scala.concurrent.Future

/**
 * Created by rabzu on 17/12/2016.
 */

//todo: make return type more general
private[client] trait AmbryClient {

  private[client] def healthCheckRequest: Future[AmbryHealthStatusResponse]

  private[client] def uploadBlobRequest(uploadData: UploadBlobRequestData): Future[AmbryBlobUploadResponse]

  private[client] def getBlobRequest(ambryId: AmbryId): Future[AmbryGetBlobResponse]

  private[client] def getBlobInfoRequest(ambryId: AmbryId): Future[AmbryBlobInfoResponse]

  private[client] def deleteBlobRequest(ambryId: AmbryId): Future[Boolean]

}
