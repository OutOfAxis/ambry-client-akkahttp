package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ AkkaHttpAmbryRequests, AkkaHttpAmbryResponseHandler }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ RequestsExecutor, Execution }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.UploadBlobTransfer.UploadBloabRequestData
import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

object UploadBlobTransfer {
  case class UploadBloabRequestData(ambryUri: AmbryUri, uploadData: UploadBlobRequestData)
}
trait UploadBlobTransfer extends AkkaHttpAmbryResponseHandler {
  self: AkkaHttpAmbryRequests with RequestsExecutor with Execution =>

  def flowUpload: Flow[UploadBloabRequestData, AmbryBlobUploadResponse, NotUsed] =
    Flow[UploadBloabRequestData].mapAsync(1) { data =>
      val httpReq = uploadBlobHttpRequest(data.ambryUri, data.uploadData)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobUploadResponse]

      executeRequest(httpReq, unmarshalFunc)
    }
}
