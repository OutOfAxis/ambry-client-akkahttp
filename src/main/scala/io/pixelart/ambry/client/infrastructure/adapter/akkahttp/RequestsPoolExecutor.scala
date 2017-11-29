package io.pixelart.ambry.client.infrastructure.adapter.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy, QueueOfferResult, StreamTcpException}
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.{AmbryHttpConnectionException, AmbryHttpFileNotFoundException}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

class RequestsPoolExecutor(host: String, port: Int = 1174, connectionPoolSettings: ConnectionPoolSettings )(implicit val actorSystem: ActorSystem, val executionContext: ExecutionContext, val materializer: ActorMaterializer)
    extends AkkaHttpAmbryResponseHandler
    with StrictLogging {

  //todo: Move everythign to Akka URI
  private lazy val poolFlow: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] =

    Http().cachedHostConnectionPool[Promise[HttpResponse]](host.split("http[s]?://").tail.head, port,connectionPoolSettings)

  private lazy val queueSize = 50

  private val (queueSource, connectionPool) = Source.queue[(HttpRequest, Promise[HttpResponse])](queueSize, OverflowStrategy.backpressure)
    .viaMat(poolFlow)(Keep.both)
    .toMat(
      Sink.foreach({
        case ((Success(resp), p)) =>
          logger.trace("ambry/request/§success/message={}", resp.toString())
          p.success(resp)
        case ((Failure(e), p)) => p.failure(e)
      })
    )(Keep.left)
    .run()

  logger.info("ambry/connectionPool/setting={}/host={}/port={}", connectionPool.setup.toString)

  protected[akkahttp] def executeRequest[T](httpRequest: HttpRequest, unmarshal: HttpResponse => Future[T]): Future[T] = {

    val responsePromise = Promise[HttpResponse]()
    queueSource.offer(httpRequest -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued    => responsePromise.future.flatMap(handleHttpResponse(_, unmarshal))
      case QueueOfferResult.Dropped     => Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed => Future.failed(new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }.recoverWith {
    case e: StreamTcpException =>
      throw new AmbryHttpConnectionException(e.getMessage, nestedException = e)
    //    case e: NoSuchElementException =>

  }

}
