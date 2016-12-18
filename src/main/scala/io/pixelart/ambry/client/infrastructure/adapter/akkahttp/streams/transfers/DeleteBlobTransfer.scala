package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.{ AmbryId, AmbryUri }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryResponseHandler
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.DeleteBlobTransfer.DeleteBlobRequestData

object DeleteBlobTransfer {
  case class DeleteBlobRequestData(ambryUri: AmbryUri, ambryId: AmbryId)
}

trait DeleteBlobTransfer extends AkkaHttpAmbryResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowDelete: Flow[DeleteBlobRequestData, Boolean, NotUsed] =
    Flow[DeleteBlobRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.deleteBlobHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[Boolean]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
