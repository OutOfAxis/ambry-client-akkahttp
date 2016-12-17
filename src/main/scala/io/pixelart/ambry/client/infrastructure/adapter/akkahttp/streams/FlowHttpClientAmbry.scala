package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.application.config.ActorImplicits
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryRequests
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{Execution, RequestsExecutor}
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers._
import scala.concurrent.ExecutionContext

//todo: not used
object FlowHttpClientAmbry {
  def apply(requestsEx: RequestsExecutor, httpReqs: AkkaHttpAmbryRequests)
           (implicit sys: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext): FlowHttpClientAmbry =

    new FlowHttpClientAmbry with ActorImplicits with Execution {
      override implicit val actorSystem: ActorSystem = sys
      override implicit val executionContext: ExecutionContext = ec
      override implicit val materializer: ActorMaterializer = mat

      override val requestsExecutor: RequestsExecutor = requestsEx
      override val httpRequests: AkkaHttpAmbryRequests = httpReqs
    }
}

trait FlowHttpClientAmbry
    extends UploadBlobTransfer
    with DeleteBlobTransfer
    with GetBlobInfoTransfer
    with GetBlobTransfer
    with HealthCheckTransfer
    with Execution

