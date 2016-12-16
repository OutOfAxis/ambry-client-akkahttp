package io.pixelart.ambry.client.infrastructure.adapter.client.stream.executor

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.stream.StreamTcpException
import akka.stream.scaladsl.{ Keep, Sink, Source }
import io.pixelart.ambry.client.application.config.ActorImplicits
import io.pixelart.ambry.client.infrastructure.adapter.client.AmbryHttpClientResponseHandler

import scala.concurrent.Future

trait RequestsSuperPoolExecutor extends RequestsExecutor with AmbryHttpClientResponseHandler with ActorImplicits {

  private lazy val poolFlow = Http()(actorSystem).superPool[Unit]()(materializer)

  protected[stream] def executeRequest[T](
    httpReq: HttpRequest,
    unmarshalFunc: HttpResponse => Future[T]
  ): Future[T] =
    Source
      .single((httpReq, ()))
      .viaMat(poolFlow)(Keep.right)
      .map(_._1)
      .mapAsync(1)(t => handleHttpResponse(t, unmarshalFunc))
      .toMat(Sink.head)(Keep.right).run()
      .recoverWith {
        case e: StreamTcpException => {
          throw new XMPHttpConnectionException(e.getMessage, nestedException = e)
        }
      }
}
