package io.pixelart.ambry.client.application

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.domain.model.AmbryUri
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
class AmbryAkkaHttpClient(host: String, port: Int = 1174)(implicit sys: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer) {

  val ambryEndpoint = host + ":" + port.toString

  private val ambryC = new AkkaHttpAmbryClient with Execution {

    implicit override lazy val actorSystem = sys
    implicit override lazy val executionContext = ec
    implicit override lazy val materializer = mat

    val ambryUri = AmbryUri(ambryEndpoint)
    val requestsExecutor: RequestsExecutor = new RequestsSuperPoolExecutor {
      implicit override lazy val actorSystem = sys
      implicit override lazy val executionContext = ec
      implicit override lazy val materializer = mat

    }
    val httpRequests: AkkaHttpAmbryRequests = new AkkaHttpAmbryRequests {
      implicit override lazy val actorSystem = sys
      implicit override lazy val executionContext = ec
      implicit override lazy val materializer = mat
    }
  }

  val ambryService = new AmbryService {

    implicit override lazy val actorSystem = sys
    implicit override lazy val executionContext = ec
    implicit override lazy val materializer = mat

    val ambryClient: AmbryClient = ambryC
    val ambryUri: AmbryUri = AmbryUri(ambryEndpoint)

  }

}
