package io.pixelart.ambry.test.client.application

import com.typesafe.scalalogging.StrictLogging
import helpers.AkkaSpec
import io.pixelart.ambry.client.application.AmbryAkkaHttpClient
import io.pixelart.ambry.client.domain.model.httpModel._
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
  var ambryId: Option[AmbryId] = None

  "Ambry service" should {

    "1. return  HealthCheck Good from real Ambry server" in {
      val healthCheckFuture = client.healthCheck

      whenReady(healthCheckFuture, timeout(10 seconds)) { r =>
        r shouldEqual AmbryHealthStatusResponse("GOOD")
      }
    }

    "2. should upload file" in {
      val uploadRequest = client.postFile(uploadData)
      whenReady(uploadRequest, timeout(10 seconds)) { r =>
        ambryId = Some(r.ambryId)
        logger.info(r.ambryId.value)
      }
    }
    "3. should get file" in {
      val uploadRequest = client.getFileProperty(ambryId.get)
      whenReady(uploadRequest, timeout(10 seconds)) { r =>
        r.serviceId.value shouldEqual "ServiceId"
      }
    }
    "4. delete fine in ambyr" in {
      val uploadRequest = client.deleteFile(ambryId.get)
      whenReady(uploadRequest, timeout(10 seconds)) { r =>
        r shouldEqual true
      }
    }

  }

}
