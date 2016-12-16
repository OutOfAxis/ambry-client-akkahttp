package io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.{ AmbryHealthStatusResponse, AmbryUri }
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.HealthCheckTransfer.HealthCheckRequestData
import io.pixelart.ambry.client.infrastructure.adapter.client.{ AmbryHttpClientResponseHandler, Execution }

object HealthCheckTransfer {
  case class HealthCheckRequestData(ambryUri: AmbryUri)
}

trait HealthCheckTransfer extends AmbryHttpClientResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowAuthenticate: Flow[HealthCheckRequestData, AmbryHealthStatusResponse, NotUsed] =
    Flow[HealthCheckRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.healthStatusHttpRequest(data.ambryUri)
      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryHealthStatusResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
