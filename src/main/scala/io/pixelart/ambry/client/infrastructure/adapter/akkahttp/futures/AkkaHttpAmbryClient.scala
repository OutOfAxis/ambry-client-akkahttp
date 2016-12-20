package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.AmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryRequests
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ RequestsExecutor, Execution }
import scala.concurrent.Future
import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

/**
 * Created by rabzu on 11/12/2016.
 */
private[client] trait AkkaHttpAmbryClient extends StrictLogging with AmbryClient {
  this: AkkaHttpAmbryRequests with RequestsExecutor with Execution =>

  private[client] val ambryUri: AmbryUri

  private[client] override def healthCheckRequest: Future[AmbryHealthStatusResponse] = {
    val httpReq = healthStatusHttpRequest(ambryUri)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryHealthStatusResponse]
    executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def uploadBlobRequest(uploadData: UploadBlobRequestData): Future[AmbryBlobUploadResponse] = {
    logger.info("Posting file with owner" + uploadData.ownerId.value)
    val httpReq = uploadBlobHttpRequest(ambryUri, uploadData)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobUploadResponse]
    executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def getBlobRequest(ambryId: AmbryId): Future[AmbryGetBlobResponse] = {
    val httpReq = getBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]
    executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def getBlobInfoRequest(ambryId: AmbryId): Future[AmbryBlobInfoResponse] = {
    val httpReq = getBlobInfoHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobInfoResponse](fromGetBlobInfoResponse, executionContext, materializer)
    executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def deleteBlobRequest(ambryId: AmbryId): Future[Boolean] = {
    val httpReq = deleteBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[Boolean]
    executeRequest(httpReq, unmarshalFunc)
  }

}
