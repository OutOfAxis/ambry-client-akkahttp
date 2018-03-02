package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.AmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ AkkaHttpAmbryRequests, RequestsPoolExecutor }
import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._
import scala.concurrent.Future

/**
 * Created by rabzu on 11/12/2016.
 */
private[client] trait AkkaHttpAmbryClient extends StrictLogging with AmbryClient {
  this: AkkaHttpAmbryRequests =>

  private[client] val ambryUri: AmbryUri

  private[client] val client: RequestsPoolExecutor

  private[client] override def healthCheckRequest: Future[AmbryHealthStatusResponse] = {
    val httpReq = healthStatusHttpRequest(ambryUri)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryHealthStatusResponse]
    client.executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def uploadBlobRequest(uploadData: UploadBlobRequestData): Future[AmbryBlobUploadResponse] = {
    logger.info("Posting file with owner" + uploadData.ownerId.value)
    val httpReq = uploadBlobHttpRequest(ambryUri, uploadData)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobUploadResponse]
    client.executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def getBlobRequest(ambryId: AmbryId): Future[AmbryGetBlobResponse] = {
    val httpReq = getBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]
    client.executeRequest(httpReq, unmarshalFunc)
  }
  //all sizes are in bytes
  private[client] override def getBlobRequestStreamed(ambryId: AmbryId, chunkSize: Long = 100000): Future[AmbryGetBlobResponse] = {
    logger.debug("ambry/getBlobRequestStreamed/ambryId={}/chunkSize={}/", ambryId.value, chunkSize.toString)
    getBlobInfoRequest(ambryId).map { info =>
      require(chunkSize > 0, "Chunk size cannot be negative")
      val s = (info.blobSize.toFloat / chunkSize.toFloat).ceil
      val l = List.tabulate[Option[Long]](s.toInt)(n => Some(n * chunkSize))
      val r = l.drop(1).map(_.map(_ - 1)) ::: List(None)
      val ranges = l.zip(r)
      val source = Source(ranges.map { tuple =>
        getBlobHttpRequestWithRange(ambryUri, ambryId, tuple._1, tuple._2)
      })
      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]

      val mergedBlobSource = source.mapAsync(1) { request =>
        logger.info("ambry/HttpRequest={}:", request.toString())
        client.executeRequest(request, unmarshalFunc)
      }.flatMapConcat { response =>
        response.blob
      }
      AmbryGetBlobResponse(mergedBlobSource, info.blobSize, info.contentType)
    }
  }

  private[client] override def getBlobInfoRequest(ambryId: AmbryId): Future[AmbryBlobInfoResponse] = {
    val httpReq = getBlobInfoHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobInfoResponse](fromGetBlobInfoResponse, executionContext, materializer)
    client.executeRequest(httpReq, unmarshalFunc)
  }

  private[client] override def deleteBlobRequest(ambryId: AmbryId): Future[Boolean] = {
    val httpReq = deleteBlobHttpRequest(ambryUri, ambryId)
    val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[Boolean]
    client.executeRequest(httpReq, unmarshalFunc)
  }
}
