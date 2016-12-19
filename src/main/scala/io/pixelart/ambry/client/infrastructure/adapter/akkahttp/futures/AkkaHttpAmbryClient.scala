package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.infrastructure.adapter.AmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import scala.concurrent.Future
import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

/**
 * Created by rabzu on 11/12/2016.
 */
trait AkkaHttpAmbryClient extends StrictLogging with AmbryClient {
  this: Execution =>

  val ambryUri: AmbryUri

  override def healthCheckRequest: Future[AmbryHealthStatusResponse] = {
    val httpReq = httpRequests.healthStatusHttpRequest(ambryUri)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryHealthStatusResponse]
    requestsExecutor.executeRequest(httpReq, unmarshalFunc)
  }

  override def uploadBlobRequest(uploadData: UploadBlobRequestData): Future[AmbryBlobUploadResponse] = {
    logger.info("Posting file with owner" + uploadData.ownerId.value)
    val httpReq = httpRequests.uploadBlobHttpRequest(ambryUri, uploadData)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobUploadResponse]
    requestsExecutor.executeRequest(httpReq, unmarshalFunc)
  }

  override def getBlobRequest(ambryId: AmbryId): Future[AmbryGetBlobResponse] = {
    val httpReq = httpRequests.getBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]
    requestsExecutor.executeRequest(httpReq, unmarshalFunc)
  }

  override def getBlobInfoRequest(ambryId: AmbryId): Future[AmbryBlobInfoResponse] = {
    val httpReq = httpRequests.getBlobInfoHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobInfoResponse](fromGetBlobInfoResponse, executionContext, materializer)
    requestsExecutor.executeRequest(httpReq, unmarshalFunc)
  }

  override def deleteBlobRequest(ambryId: AmbryId): Future[Boolean] = {
    val httpReq = httpRequests.deleteBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[Boolean]
    requestsExecutor.executeRequest(httpReq, unmarshalFunc)
  }

}
