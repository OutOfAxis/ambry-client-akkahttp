package io.pixelart.ambry.test.client.application

import java.nio.file.{ Files, Paths }

import akka.http.scaladsl.model.Uri
import akka.stream.scaladsl.FileIO
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import helpers.AkkaSpec
import io.pixelart.ambry.client.application.AmbryAkkaHttpClient
import io.pixelart.ambry.client.domain.model.{ AmbryId, UploadBlobRequestData, AmbryHealthStatusResponse, Good }
import io.pixelart.ambry.test.client.model.MockData
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._
import scala.language.postfixOps
import MockData._

/**
 * Created by rabzu on 18/12/2016.
 */
class AmbryAkkaHtpClientSpec extends AkkaSpec("ambry-client") with ScalaFutures with StrictLogging {

  val client = new AmbryAkkaHttpClient("http://pixelart.ge")

  "Ambry service" should {

    "1. return  HealthCheck Good from real Ambry server" in {
      val healthCheckFuture = client.ambryService.healthCheck

      whenReady(healthCheckFuture, timeout(10 seconds)) { r =>
        r shouldEqual AmbryHealthStatusResponse(Good)
      }
    }

    "2. should upload file" in {
      val uploadRequest = client.ambryService.postFile(uploadData)
      whenReady(uploadRequest, timeout(10 seconds)) { r =>
        logger.info(r.ambryId.value)
      }
    }
    "3. should get file" in {
      val uploadRequest = client.ambryService.getFileProperty(AmbryId("AAEAAQAAAAAAAAAAAAAAJDRhYzgyZjRjLWVjMWItNDRhMC04ZDdhLTcxZDY1ZmJmYzJmZA"))
      whenReady(uploadRequest, timeout(10 seconds)) { r =>
        r.serviceId.value shouldEqual "CUrlUpload"
      }
    }

  }

}
