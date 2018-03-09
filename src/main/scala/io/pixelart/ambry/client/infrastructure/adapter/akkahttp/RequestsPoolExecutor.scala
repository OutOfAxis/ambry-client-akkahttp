package io.pixelart.ambry.client.infrastructure.adapter.akkahttp

import java.io.{ PrintWriter, StringWriter }

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Keep, MergeHub, Sink, Source }
import com.typesafe.scalalogging.StrictLogging

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success, Try }


class RequestsPoolExecutor(host: String, port: Int = 1174, connectionPoolSettings: ConnectionPoolSettings)(implicit val actorSystem: ActorSystem, val executionContext: ExecutionContext, val materializer: ActorMaterializer)
    extends AkkaHttpAmbryResponseHandler
    with StrictLogging {

  //todo: Move everything to Akka URI
  private lazy val poolFlow: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] =
    Http().cachedHostConnectionPool[Promise[HttpResponse]](host.split("http[s]?://").tail.head, port, connectionPoolSettings)

  private val ServerSink: Sink[(HttpRequest, Promise[HttpResponse]), (Http.HostConnectionPool, Future[Done])] =
    poolFlow
      .toMat(Sink.foreach({
        case ((Success(resp), p)) =>
          logger.debug("ambry/host={}/port={}/response={}", host, port.toString, resp.toString())
          p.success(resp)
        case ((Failure(e), p)) =>
          logger.debug("ambry/host={}/port={}/cause={}", host, port.toString, e.getCause)
          val sw = new StringWriter
          e.printStackTrace(new PrintWriter(sw))
          logger.error(sw.toString)
          p.failure(e)
      }))(Keep.both)

  // Attach a MergeHub Source to the consumer. This will materialize to a corresponding Sink.
  private val runnableGraph =
    MergeHub.source[(HttpRequest, Promise[HttpResponse])](perProducerBufferSize = 16).toMat(ServerSink)(Keep.both)

  val (toConsumer, (conPool, _)) = runnableGraph.run()
  logger.info("ambry/connectionPool/setting={}/host={}/port={}", conPool.setup.toString)

  protected[akkahttp] def executeRequest[T](httpRequest: HttpRequest, unmarshal: HttpResponse => Future[T]): Future[T] = {
    val responsePromise = Promise[HttpResponse]()
    val p = Source.single((httpRequest -> responsePromise)).mapMaterializedValue(_ => responsePromise).toMat(toConsumer)(Keep.left).run()
    p.future.flatMap(_.toStrict(5 minutes)).flatMap(handleHttpResponse(_, unmarshal))
  }
}
