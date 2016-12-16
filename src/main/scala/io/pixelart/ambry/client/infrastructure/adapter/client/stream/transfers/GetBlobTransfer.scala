package io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.GetBlobTransfer.GetBlobTransferRequestData
import io.pixelart.ambry.client.infrastructure.adapter.client.{ AmbryHttpClientResponseHandler, Execution }

object GetBlobTransfer {
  case class GetBlobTransferRequestData(ambryUri: AmbryUri, ambryId: AmbryId)
}

trait GetBlobTransferTransfer extends AmbryHttpClientResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowDelete: Flow[GetBlobTransferRequestData, AmbryGetBlobResponse, NotUsed] =
    Flow[GetBlobTransferRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.getBlobHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryGetBlobResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
