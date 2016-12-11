package io.pixelart.ambry.client.infrastructure.adapter.client.stream.executor

import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }

import scala.concurrent.Future

trait RequestsExecutor {

  protected[stream] def executeRequest[T](httpReq: HttpRequest, unmarshalFunc: HttpResponse => Future[T]): Future[T]


}
