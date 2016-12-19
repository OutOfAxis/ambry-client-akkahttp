package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryResponseHandler
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.GetBlobTransfer.GetBlobTransferRequestData

object GetBlobTransfer {
  case class GetBlobTransferRequestData(ambryUri: AmbryUri, ambryId: AmbryId)
}

trait GetBlobTransfer extends AkkaHttpAmbryResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowGetBlob: Flow[GetBlobTransferRequestData, AmbryGetBlobResponse, NotUsed] =
    Flow[GetBlobTransferRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.getBlobHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
