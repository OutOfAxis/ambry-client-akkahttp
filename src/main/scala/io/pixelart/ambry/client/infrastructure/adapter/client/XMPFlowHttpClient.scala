package io.pixelart.ambry.client.infrastructure.adapter.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.application.config.ActorImplicits
import io.pixelart.ambry.client.infrastructure.adapter.AmbryHttpRequests
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.executor.RequestsExecutor
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.UploadBloabTransfer
import scala.concurrent.ExecutionContext

trait Execution extends ActorImplicits {
  def requestsExecutor: RequestsExecutor
  def httpRequests: AmbryHttpRequests
}

object XMPFlowHttpClient {
  def apply(requestsEx: RequestsExecutor, httpReqs: AmbryHttpRequests)(implicit sys: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext): XMPFlowHttpClient =
    new XMPFlowHttpClient with ActorImplicits with Execution {
      override implicit val actorSystem: ActorSystem = sys
      override implicit val executionContext: ExecutionContext = ec
      override implicit val materializer: ActorMaterializer = mat

      override val requestsExecutor: RequestsExecutor = requestsEx
      override val httpRequests: AmbryHttpRequests = httpReqs
    }
}

trait XMPFlowHttpClient
    extends UploadBloabTransfer
    with Execution {
}
