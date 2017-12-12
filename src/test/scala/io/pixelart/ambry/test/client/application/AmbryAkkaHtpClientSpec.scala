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

  val client = new AmbryAkkaHttpClient("http://b.pixelart.ge",connectionPoolSettings = ConnectionPoolSettings(system))
  var ambryId: Option[AmbryId] = None

  "Ambry service" should {
    "1. return  HealthCheck Good from real Ambry server" in {
      val healthCheckFuture = client.healthCheck

      whenReady(healthCheckFuture, timeout(10 seconds)) { r =>
        r shouldEqual AmbryHealthStatusResponse("GOOD")
      }
    }

    "2. should upload file" in {
      val request = client.postFile(uploadData)
      whenReady(request, timeout(10 seconds)) { r =>
        ambryId = Some(r.ambryId)
        logger.info(r.ambryId.value)
      }
    }
    "3. should get file prop" in {
      val request = client.getFileProperty(ambryId.get)
      whenReady(request, timeout(10 seconds)) { r =>
        r.serviceId.value shouldEqual "ServiceId"
      }
    }
    "4.should get file " in {
      def request = for {
        resp <- client.getFile(ambryId.get)
        bs <-resp.blob.runWith(Sink.fold(ByteString.empty)(_ ++ _))
      } yield bs

      whenReady(request, timeout(10 seconds)) { r =>
        r.length shouldEqual testFileSize
      }
    }

    "5.should get stream file " in {
      def bsF = for {
        resp <- client.getBlobRequestStreamed(ambryId.get)
//        bs <-resp.blob.runWith(Sink.ignore)
        bs <-resp.blob.runWith(Sink.fold(ByteString.empty)(_ ++ _))
      } yield {
        logger.info(DateTime.now.toString())
        bs
      }

      val F = Future.traverse((List.fill(1)(Unit)))(x=>bsF)

      whenReady(F, timeout(260 seconds)) {  bs =>
//           bs.size shouldEqual testFileSize
        bs.length shouldEqual 1
        bs.head.length shouldEqual testFileSize
      }
    }



    "6. delete fine in ambyr" in {
      val request = client.deleteFile(ambryId.get)
      whenReady(request, timeout(10 seconds)) { r =>
        r shouldEqual true
      }
    }

      "7. get non existant file " in {
        val request = client.getFile(ambryId.get)
        whenReady(request.failed, timeout(10 seconds)) {
          case e:AmbryHttpFileNotFoundException =>
            true shouldEqual true

        }
    }


    "8. get non existant file " in {
      val request = client.getFile(AmbryId("fake_Id"))
      whenReady(request.failed, timeout(10 seconds)) {
        case e:AmbryHttpBadRequestException =>
          true shouldEqual true

      }
    }
  }
}
