package io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model._
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.GetBlobInfoTransfer.GetBlobInfoRequestData
import io.pixelart.ambry.client.infrastructure.adapter.client.{AmbryHttpClientResponseHandler, Execution}

object GetBlobInfoTransfer {

  case class GetBlobInfoRequestData(ambryUri: AmbryUri, ambryId: AmbryId)

}

trait GetBlobInfoTransfer extends AmbryHttpClientResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowGetBlobInfo: Flow[GetBlobInfoRequestData, AmbryBlobInfoResponse, NotUsed] =
    Flow[GetBlobInfoRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.getBlobInfoHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobInfoResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
