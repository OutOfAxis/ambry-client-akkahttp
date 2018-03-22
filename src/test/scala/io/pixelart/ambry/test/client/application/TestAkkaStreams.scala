package io.pixelart.ambry.test.client.application

import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.AmbryAkkaHttpClient
import io.pixelart.ambry.client.application.test.AkkaSpec
import io.pixelart.ambry.client.domain.model.httpModel._
import io.pixelart.ambry.client.domain.model.{AmbryHttpBadRequestException, AmbryHttpFileNotFoundException}
import io.pixelart.ambry.client.model.test.MockData._
import org.joda.time.DateTime
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by rabzu on 18/12/2016.
 */
class TestAkkaStreams extends AkkaSpec("akka") with ScalaFutures with StrictLogging {

  "Akka streams" should  {
    "1.test" in {


//      val s1 = Source(1 to 3).initialDelay(3 seconds).to(Sink.foreach(println)).run()
//      val s2 = Source(4 to 7)
//      val s3 = Source(7 to 10)
//
//            Source(1 to 10).groupBy(3, _ % 3).concatSubstreams.to(Sink.foreach(println)).run()
//            Source(1 to 10).groupBy(3, _ % 3).mergeSubstreamsWithParallelism(2).to(Sink.foreach(println)).run()
      Source(1 to 10)
        .map(Source.single(_))
          .flatMapConcat{s => s}
        .map(a =>a )
      .runForeach(println)

      Thread.sleep(1000000)





    }
   }
  }

