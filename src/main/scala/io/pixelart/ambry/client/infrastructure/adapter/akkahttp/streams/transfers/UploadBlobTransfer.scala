package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.{AmbryUri, UploadBlobRequestData}
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryResponseHandler
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.UploadBlobTransfer.UploadBloabRequestData

object UploadBlobTransfer {
  case class UploadBloabRequestData(ambryUri: AmbryUri, uploadData: UploadBlobRequestData)
}
trait UploadBlobTransfer extends AkkaHttpAmbryResponseHandler {
  self: Execution =>

  def flowUpload: Flow[UploadBloabRequestData, AmbryPostFileResponse, NotUsed] =
    Flow[UploadBloabRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.uploadBlobHttpRequest(data.ambryUri, data.uploadData)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryPostFileResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
