package io.pixelart.ambry.client.infrastructure.adapter.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.stream.{ ActorMaterializer, OverflowStrategy, QueueOfferResult, StreamTcpException }
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.AmbryHttpConnectionException
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success }

class RequestsPoolExecutor(host: String, port: Int = 1174)(implicit val actorSystem: ActorSystem, val executionContext: ExecutionContext, val materializer: ActorMaterializer)
    extends AkkaHttpAmbryResponseHandler
    with StrictLogging {
//todo: Move everythign to Akka URI
  private lazy val poolFlow = Http().cachedHostConnectionPool[Promise[HttpResponse]](host.split("http[s]?://").tail.head, port)

  private lazy val queueSize = 50

  private lazy val (queueSource, poolFuture) = Source.queue[(HttpRequest, Promise[HttpResponse])](queueSize, OverflowStrategy.backpressure)
    .via(poolFlow)
    .toMat(
      Sink.foreach({
        case ((Success(resp), p)) =>
          logger.trace("ambry/request/success/message={}", resp.toString())
          p.success(resp)
        case ((Failure(e), p)) => p.failure(e)
      })
    )(Keep.both)
    .run()


  poolFuture.onComplete{
      case Success(_) =>
        logger.debug("ambry/connection/pool/success/closed ")
      case Failure(e) =>
        logger.warn("ambry/request/failure/message={}", e.getMessage)
        e.printStackTrace()
    }



  protected[akkahttp] def executeRequest[T](httpRequest: HttpRequest, unmarshal: HttpResponse => Future[T]): Future[T] = {

    val responsePromise = Promise[HttpResponse]()
    queueSource.offer(httpRequest -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued    => responsePromise.future.flatMap(unmarshal)
      case QueueOfferResult.Dropped     => Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed => Future.failed(new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }.recoverWith {
    case e: StreamTcpException =>
      throw new AmbryHttpConnectionException(e.getMessage, nestedException = e)
    case e: Throwable =>
      throw new AmbryHttpConnectionException(e.getMessage, nestedException = e)
  }

}
