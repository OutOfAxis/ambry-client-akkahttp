package io.pixelart.ambry.client.infrastructure.adapter.akkahttp.executor

import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import io.pixelart.ambry.client.application.ActorImplicits
import io.pixelart.ambry.client.infrastructure.adapter.akkahttp.AkkaHttpAmbryRequests
import scala.concurrent.Future

trait Execution extends ActorImplicits {
  def requestsExecutor: RequestsExecutor
  def httpRequests: AkkaHttpAmbryRequests
}

trait RequestsExecutor {
  protected[akkahttp] def executeRequest[T](httpReq: HttpRequest, unmarshalFunc: HttpResponse => Future[T]): Future[T]

}
