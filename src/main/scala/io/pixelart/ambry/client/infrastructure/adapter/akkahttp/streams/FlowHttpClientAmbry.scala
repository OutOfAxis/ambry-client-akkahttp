package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.application.ActorImplicits
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ RequestsSuperPoolExecutor, AkkaHttpAmbryRequests }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ Execution, RequestsExecutor }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.streams.transfers._
import scala.concurrent.ExecutionContext

//todo: not used
private[client] object FlowHttpClientAmbry {
  def apply(requestsEx: RequestsExecutor, httpReqs: AkkaHttpAmbryRequests)(implicit sys: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext): FlowHttpClientAmbry =

    new FlowHttpClientAmbry with ActorImplicits with Execution {
      override implicit val actorSystem: ActorSystem = sys
      override implicit val executionContext: ExecutionContext = ec
      override implicit val materializer: ActorMaterializer = mat

    }
}

private[client] trait FlowHttpClientAmbry
  extends RequestsSuperPoolExecutor
  with AkkaHttpAmbryRequests
  with UploadBlobTransfer
  with DeleteBlobTransfer
  with GetBlobInfoTransfer
  with GetBlobTransfer
  with HealthCheckTransfer
  with Execution

