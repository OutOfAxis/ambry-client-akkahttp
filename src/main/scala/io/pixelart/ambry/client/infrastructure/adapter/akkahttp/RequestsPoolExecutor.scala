package io.pixelart.ambry.client.infrastructure.adapter.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Keep, MergeHub, Sink, Source }
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success, Try }

class RequestsPoolExecutor(host: String, port: Int = 1174, connectionPoolSettings: ConnectionPoolSettings)(implicit val actorSystem: ActorSystem, val executionContext: ExecutionContext, val materializer: ActorMaterializer)
    extends AkkaHttpAmbryResponseHandler
    with StrictLogging {

  //todo: Move everythign to Akka URI
  private lazy val poolFlow: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] =
    Http().cachedHostConnectionPool[Promise[HttpResponse]](host.split("http[s]?://").tail.head, port, connectionPoolSettings)

  private val hub =
    MergeHub.source[(HttpRequest, Promise[HttpResponse])](perProducerBufferSize = 16)

  private val ServerSink =
    poolFlow.async
      .toMat(Sink.foreach({
      case ((Success(resp), p)) => p.success(resp)
      case ((Failure(e), p))    => p.failure(e)
    }))(Keep.both)

  // Attach a MergeHub Source to the consumer. This will materialize to a corresponding Sink.
  private val runnableGraph =
    MergeHub.source[(HttpRequest, Promise[HttpResponse])](perProducerBufferSize = 16).toMat(ServerSink)(Keep.both)

  val (toConsumer, (conPool, _)) = runnableGraph.run()

  logger.info("ambry/connectionPool/setting={}/host={}/port={}", conPool.setup.toString)

  protected[akkahttp] def executeRequest[T](httpRequest: HttpRequest, unmarshal: HttpResponse => Future[T]): Future[T] = {
    val responsePromise = Promise[HttpResponse]()
    Source.single((httpRequest -> responsePromise))
      .runWith(toConsumer)
    responsePromise.future.flatMap(handleHttpResponse(_, unmarshal))
  }
}
