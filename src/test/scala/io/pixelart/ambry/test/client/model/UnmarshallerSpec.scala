package io.pixelart.ambry.test.client.model

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import helpers.AkkaSpec
import io.pixelart.ambry.client.domain.model.{AmbryBlobInfoResponse, Good, AmbryHealthStatusResponse, AmbryPostFileResponse}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.concurrent.duration._

/**
  * Created by rabzu on 15/12/2016.
  */
class UnmarshallerSpec extends AkkaSpec("unmarshal") with ScalaFutures with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {
  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._
  import akka.http.scaladsl.unmarshalling._

  val ambryBlobInfo = AmbryPostFileResponse("ambryId")
  val healthCheck = AmbryHealthStatusResponse(Good)

  val s = 123
  val serviceId = "serviceId"
  val creationTime = "creationTime"
  val privateHeader = false

  val contentTypeHeader = "image/png"
  val ownerIdHeader = "onwerId"

  val getBlobInfoCheck = AmbryBlobInfoResponse(s, serviceId, creationTime, privateHeader, contentTypeHeader, ownerIdHeader)

  //  object postUnmarshaller extends FromResponseUnmarshaller[AmbryPostFileResponse]

  val postFileHttpResponse = HttpResponse(status = StatusCodes.OK, headers = List(Location("ambryId")))
  val healthCheckHttpResponse = HttpResponse(status = StatusCodes.OK, entity = "GOOD")

  "Unmarshaler" should {
    "unmarshal HttpResponse to AmbryPostFileResponse" in {
      val result = Unmarshal(postFileHttpResponse).to[AmbryPostFileResponse]
      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual ambryBlobInfo
      }
    }

    "unmarshal HttpResponse to HealthCheck" in {
      val result = Unmarshal(healthCheckHttpResponse).to[AmbryHealthStatusResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual healthCheck
      }
    }

    "unmarshal HttpResponse to AmbryBlobInfoResponse" in {
      val result = Unmarshal(getBlobInfoCheck).to[AmbryBlobInfoResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual getBlobInfoCheck
      }
    }
  }
}
