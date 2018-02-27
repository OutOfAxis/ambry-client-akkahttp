package io.pixelart.ambry.client.application.test

import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.AmbryAkkaHttpClient
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
class AmbryAkkaHtpClientSpec extends AkkaSpec("ambry-client") with ScalaFutures with StrictLogging {

//    val client = new AmbryAkkaHttpClient("http://pixelart.ge",connectionPoolSettings = ConnectionPoolSettings(system).withMaxConnections(100))
    val client = new AmbryAkkaHttpClient("http://b.pixelart.ge", connectionPoolSettings = ConnectionPoolSettings(system))
//  val client = new AmbryAkkaHttpClient("http://b.pixelart.ge", connectionPoolSettings = ConnectionPoolSettings(system).withMaxOpenRequests(256))
  var ambryId: Option[AmbryId] = None

  "Ambry service" should {
    "1. return  HealthCheck Good from real Ambry server" ignore {
      val healthCheckFuture = client.healthCheck
      whenReady(healthCheckFuture, timeout(10 seconds)) { r =>
        r shouldEqual AmbryHealthStatusResponse("GOOD")
      }
    }

    "2. should upload file" ignore {
      val request = client.postFile(uploadData)
      whenReady(request, timeout(10 seconds)) { r =>
        ambryId = Some(r.ambryId)
        logger.info(r.ambryId.value)
      }
    }

    "3. should get file prop" ignore {
      val request = client.getFileProperty(ambryId.get)
      whenReady(request, timeout(10 seconds)) { r =>
        r.serviceId.value shouldEqual "ServiceId"
      }
    }


    /**
      * make sure you are draining the bytes, eitherwise akka-http will block the connection
      * and tests below will not be successfull
      */
    "4.should get small file " ignore {
      def request = for {
        resp <- client.getFile(ambryId.get)
        bs <- resp.blob.runWith(Sink.fold(ByteString.empty)(_ ++ _))
      } yield bs

      whenReady(request, timeout(10 seconds)) { r =>
        r.length shouldEqual testFileSize
      }
    }

    //already uploaded to b.pixelart
    val large_ambryId = AmbryId("AAIA____AAAAAQAAAAAAAAAAAAAAJDVhYTU0MTAzLWEwZmItNDNhYi1iYWY5LWZjYmVjZmM1YzI4MQ")

    "5.1 should get large file " ignore {
      def request = for {
        resp <- client.getFile(large_ambryId)
        bs <- resp.blob.runWith(Sink.fold(ByteString.empty)(_ ++ _))
      } yield bs

      whenReady(request, timeout(100000 seconds)) { r =>
        r.length shouldEqual testFileVideoSize
      }
    }

    /**
      * When chunk size is large 10.1.0-RC2 throws
      *
      * The future returned an exception of type: java.lang.IllegalStateException, with message: Substream Source cannot be materialized more than once.
      *
      * and logs
      *
      * Response entity was not subscribed after 1 second. Make sure to read the response entity body or call `discardBytes()` on it
      *
      *  because it cannot consume data in time in my opinion
      */
    "5.2 should test concurrent GET requests" in {
      val request = client.postFile(uploadDataVideo)

      def bsF(id: AmbryId) = for {
        resp <- client.getBlobRequestStreamed(id, 1000000)
        bs <- resp.blob.runWith(Sink.fold(ByteString.empty)(_ ++ _))
      } yield {
        logger.info(DateTime.now.toString())
        bs
      }

      val numberOfParallelRequests = 1

      def T(id: AmbryId) = Future.traverse((List.fill(numberOfParallelRequests)(Unit)))(_ => bsF(id))

      //      val F = request.flatMap(r => T(r.ambryId))
      val F = T(large_ambryId)
      whenReady(F, timeout(2600 seconds)) { bs =>
        bs.length shouldEqual numberOfParallelRequests
        bs.head.length shouldEqual testFileVideoSize
      }
    }

    "6. delete fine in ambry" ignore {
      val request = client.deleteFile(ambryId.get)
      whenReady(request, timeout(10 seconds)) { r =>
        r shouldEqual true
      }
    }

    "7. get non existant file " ignore {
      val request = client.getFile(ambryId.get)
      whenReady(request.failed, timeout(10 seconds)) {
        case e: AmbryHttpFileNotFoundException =>
          true shouldEqual true
      }
    }

    "8. get non existant file " ignore {
      val request = client.getFile(AmbryId("fake_Id"))
      whenReady(request.failed, timeout(10 seconds)) {
        case e: AmbryHttpBadRequestException =>
          true shouldEqual true
      }
    }
  }
}
