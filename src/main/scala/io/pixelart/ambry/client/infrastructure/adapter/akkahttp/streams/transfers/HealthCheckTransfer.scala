package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryResponseHandler
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers.HealthCheckTransfer.HealthCheckRequestData

object HealthCheckTransfer {
  case class HealthCheckRequestData(ambryUri: AmbryUri)
}

trait HealthCheckTransfer extends AkkaHttpAmbryResponseHandler {
  self: Execution =>

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  def flowHealthCheck: Flow[HealthCheckRequestData, AmbryHealthStatusResponse, NotUsed] =
    Flow[HealthCheckRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.healthStatusHttpRequest(data.ambryUri)
      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryHealthStatusResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
