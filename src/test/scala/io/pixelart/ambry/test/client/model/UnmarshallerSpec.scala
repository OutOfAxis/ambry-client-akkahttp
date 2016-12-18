package io.pixelart.ambry.test.client.model

import akka.http.scaladsl.model
import akka.http.scaladsl.model.{ HttpEntity, StatusCodes, HttpResponse, ContentTypes }
import akka.http.scaladsl.model.headers.{ Expires, Location }
import akka.stream.scaladsl.Source
import akka.util.ByteString
import helpers.AkkaSpec
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike }
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import com.github.nscala_time.time.Imports.DateTime
import scala.language.postfixOps

/**
 * Created by rabzu on 15/12/2016.
 */
class UnmarshallerSpec extends AkkaSpec("unmarshal") with ScalaFutures with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {
  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  import akka.http.scaladsl.unmarshalling._

  /** Mock Response data */

  //1.health Check Response data
  val healthCheck = AmbryHealthStatusResponse(Good)

  //2.posted blob Response data
  val ambryBlobInfo = AmbryBlobUploadResponse(AmbryId("ambryId"))

  //3.get blob info response data
  val nowMillis = DateTime.now
  val blobSizeHeader = AmbryBlobSizeHeader("213")
  val serviceIdHeader = AmbryServiceIdHeader("serviceId")
  val creationTimeHeader = AmbryCreationTimeHeader(nowMillis)
  val privateHeader = AmbryPrivateHeader(false)
  val contentTypeHeader = AmbryContentTypeHeader("image/png")
  val ttlHeader = AmbryTtlHeader((nowMillis.getMillis * 0.001).toLong)
  val ownerIdHeader = AmbryOwnerIdHeader("onwerId")

  val getBlobInfoResponse = AmbryBlobInfoResponse(
    blobSizeHeader.size,
    serviceIdHeader.id,
    creationTimeHeader.date,
    privateHeader.prvt,
    contentTypeHeader.contentType,
    (nowMillis.getMillis * 0.001).toLong,
    Some(ownerIdHeader.ownerId)
  )

  //4.get blob response data
  val TestLines = {
    val b = ListBuffer[String]()
    b.append("a" * 1000 + "\n")
    b.append("b" * 1000 + "\n")
    b.append("c" * 1000 + "\n")
    b.append("d" * 1000 + "\n")
    b.append("e" * 1000 + "\n")
    b.append("f" * 1000 + "\n")
    b.toList
  }

  val TestByteStrings = TestLines.map(ByteString(_))

  val foldedBS = TestByteStrings.fold(ByteString.empty) { (acc, in) ⇒ acc ++ in }

  val expires = DateTime.now
  val getBlobResponse = AmbryGetBlobResponse(Source(TestByteStrings), foldedBS.size.toLong, ContentTypes.`text/xml(UTF-8)`, expires)

  /** Mock HttpRepsonse construction */

  // 1.
  val healthCheckHttpResponse = HttpResponse(status = StatusCodes.OK, entity = "GOOD")
  // 2.
  val postFileHttpResponse = HttpResponse(status = StatusCodes.Created, headers = List(Location("ambryId")))
  //3.
  val getBlobInfoHttpResponse = HttpResponse(
    status = StatusCodes.OK,
    headers = List(blobSizeHeader, serviceIdHeader, creationTimeHeader, privateHeader, contentTypeHeader, ttlHeader, ownerIdHeader)
  )
  //4.
  val deleteHttpResponse = HttpResponse(status = StatusCodes.Accepted)
  //5.
  val falseDeleteHttpResponse = HttpResponse(status = StatusCodes.NotFound)
  //6.
  val akkadate = model.DateTime(expires.getMillis)
  val expiresHeader = Expires(akkadate)
  val getBlobHttpResponse = HttpResponse(
    status = StatusCodes.OK,
    headers = List(AmbryBlobSizeHeader(foldedBS.size.toString), expiresHeader),
    entity = HttpEntity.Chunked.fromData(ContentTypes.`text/xml(UTF-8)`, Source(TestByteStrings))
  )

  "Unmarshaler" should {
    "1. unmarshal  HealthCheck HttpResponse to HealthCheck" in {
      val result = Unmarshal(healthCheckHttpResponse).to[AmbryHealthStatusResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual healthCheck
      }
    }
    "2. unmarshal PostFile HttpResponse to AmbryPostFileResponse" in {
      val result = Unmarshal(postFileHttpResponse).to[AmbryBlobUploadResponse]
      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual ambryBlobInfo
      }
    }

    "3. unmarshal BlobInfo HttpResponse to AmbryBlobInfoResponse" in {
      val result = Unmarshal(getBlobInfoHttpResponse).to[AmbryBlobInfoResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual getBlobInfoResponse
      }
    }
    "4.1 unmarshalTrue Delete HttpResponse to delete true boolean" in {
      val result = Unmarshal(deleteHttpResponse).to[Boolean]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual true
      }
    }

    "4.2 unmarshal False Delete HttpResponse to delete false boolean" in {
      val result = Unmarshal(falseDeleteHttpResponse).to[Boolean]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual false
      }
    }

    "6. unmarshal Get Blob HttpResponse as  AmbryBlobResponse" in {
      val result = Unmarshal(getBlobHttpResponse).to[AmbryGetBlobResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r.blobSize shouldEqual getBlobResponse.blobSize
        r.contentType shouldEqual getBlobResponse.contentType
        //todo test source equality
      }
    }
  }
}
