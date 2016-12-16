package io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.{ AmbryPostFileResponse, UploadBlobRequestData, AmbryUri }
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.UploadBlobTransfer.{ UploadBloabRequestData }
import io.pixelart.ambry.client.infrastructure.adapter.client.{ AmbryHttpClientResponseHandler, Execution }

object UploadBlobTransfer {
  case class UploadBloabRequestData(ambryUri: AmbryUri, uploadData: UploadBlobRequestData)
}
trait UploadBlobTransfer extends AmbryHttpClientResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowUpload: Flow[UploadBloabRequestData, AmbryPostFileResponse, NotUsed] =
    Flow[UploadBloabRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.uploadBlobHttpRequest(data.ambryUri, data.uploadData)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryPostFileResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
