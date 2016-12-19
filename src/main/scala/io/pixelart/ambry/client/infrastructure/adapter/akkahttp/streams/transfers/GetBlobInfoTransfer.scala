package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryResponseHandler
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.GetBlobInfoTransfer.GetBlobInfoRequestData

object GetBlobInfoTransfer {

  case class GetBlobInfoRequestData(ambryUri: AmbryUri, ambryId: AmbryId)

}

trait GetBlobInfoTransfer extends AkkaHttpAmbryResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowGetBlobInfo: Flow[GetBlobInfoRequestData, AmbryBlobInfoResponse, NotUsed] =
    Flow[GetBlobInfoRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.getBlobInfoHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryBlobInfoResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
