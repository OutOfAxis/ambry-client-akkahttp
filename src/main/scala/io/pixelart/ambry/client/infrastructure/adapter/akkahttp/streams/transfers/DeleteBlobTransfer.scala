package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ AkkaHttpAmbryRequests, AkkaHttpAmbryResponseHandler }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ RequestsExecutor, Execution }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.DeleteBlobTransfer.DeleteBlobRequestData

private[client] object DeleteBlobTransfer {
  case class DeleteBlobRequestData(ambryUri: AmbryUri, ambryId: AmbryId)
}

private[client] trait DeleteBlobTransfer extends AkkaHttpAmbryResponseHandler {
  self: AkkaHttpAmbryRequests with RequestsExecutor with Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowDelete: Flow[DeleteBlobRequestData, Boolean, NotUsed] =
    Flow[DeleteBlobRequestData].mapAsync(1) { data =>
      val httpReq = deleteBlobHttpRequest(data.ambryUri, data.ambryId)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[Boolean]

      executeRequest(httpReq, unmarshalFunc)
    }
}
