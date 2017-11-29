package io.pixelart.ambry.client.application

import akka.actor.ActorSystem
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures.AkkaHttpAmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{AkkaHttpAmbryRequests, RequestsPoolExecutor}
import io.pixelart.ambry.client.infrastructure.service.AmbryService

import scala.concurrent.ExecutionContext

/**
 * Created by rabzu on 17/12/2016.
 */
//todo replace it with akka Uri model
class AmbryAkkaHttpClient(host: String, port: Int = 1174, connectionPoolSettings: ConnectionPoolSettings)(implicit val actorSystem: ActorSystem, val executionContext: ExecutionContext, val materializer: ActorMaterializer)
    extends AkkaHttpAmbryClient
    with AkkaHttpAmbryRequests
    with AmbryService {

  private[client] override val ambryUri: AmbryUri = AmbryUri(host + ":" + port.toString)

  val client = new RequestsPoolExecutor(host, port, connectionPoolSettings)

}

