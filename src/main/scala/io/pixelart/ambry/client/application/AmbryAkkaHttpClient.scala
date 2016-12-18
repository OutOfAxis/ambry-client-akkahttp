package io.pixelart.ambry.client.application

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import io.pixelart.ambry.client.domain.model.AmbryUri
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.{ RequestsSuperPoolExecutor, AkkaHttpAmbryRequests }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.{ RequestsExecutor, Execution }
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.futures.AkkaHttpAmbryClient
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor.Execution
import scala.concurrent.ExecutionContext

/**
 * Created by rabzu on 17/12/2016.
 */
class AmbryAkkaHttpClient(uri: Uri)(implicit sys: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer) {

  val ambryClient = new AkkaHttpAmbryClient with Execution {

    implicit override lazy val actorSystem = sys
    implicit override lazy val executionContext = ec
    implicit override lazy val materializer = mat

    val ambryUri = AmbryUri(uri)
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

}
