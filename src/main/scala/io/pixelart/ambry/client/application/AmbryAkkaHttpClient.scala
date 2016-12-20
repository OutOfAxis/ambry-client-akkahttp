package io.pixelart.ambry.client.application

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.AmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ RequestsSuperPoolExecutor, AkkaHttpAmbryRequests }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ RequestsExecutor, Execution }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures.AkkaHttpAmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import io.pixelart.ambry.client.infrastructure.service.AmbryService
import scala.concurrent.ExecutionContext

/**
 * Created by rabzu on 17/12/2016.
 */
//todo replace it with akka Uri model
class AmbryAkkaHttpClient(host: String, port: Int = 1174)(implicit sys: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer)
    extends AkkaHttpAmbryClient
    with Execution
    with RequestsSuperPoolExecutor
    with AkkaHttpAmbryRequests
    with AmbryService
    with ActorImplicits {

  implicit override lazy val actorSystem = sys
  implicit override lazy val executionContext = ec
  implicit override lazy val materializer = mat

  private[client] override val ambryUri: AmbryUri = AmbryUri(host + ":" + port.toString)

}

