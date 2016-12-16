package io.pixelart.ambry.test.client.model

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Expires, Location}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import helpers.AkkaSpec
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

/**
  * Created by rabzu on 15/12/2016.
  */
class UnmarshallerSpec extends AkkaSpec("unmarshal") with ScalaFutures with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {
  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._
  import akka.http.scaladsl.unmarshalling._

  import akka.http.scaladsl.unmarshalling._

  //
  val ambryBlobInfo = AmbryPostFileResponse(AmbryId("ambryId"))
  val healthCheck = AmbryHealthStatusResponse(Good)

  val s = AmbryBlobSizeHeader("213")
  val serviceId = AmbryServiceIdHeader("serviceId")
  val creationTime = AmbryCreationTimeHeader("creationTime")
  val privateHeader = AmbryPrivateHeader(false)
  val contentTypeHeader = AmbryContentTypeHeader("image/png")
  val ownerIdHeader = AmbryOwnerIdHeader("onwerId")

  val getBlobInfoResponse = AmbryBlobInfoResponse(
    s.size,
    serviceId.id,
    creationTime.time,
    privateHeader.prvt,
    contentTypeHeader.contentType,
    ownerIdHeader.ownerId
  )

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

  val postFileHttpResponse = HttpResponse(status = StatusCodes.Created, headers = List(Location("ambryId")))

  val getBlobInfoHttpResponse = HttpResponse(
    status = StatusCodes.OK,
    headers = List(s, serviceId, creationTime, privateHeader, contentTypeHeader, ownerIdHeader)
  )

  val healthCheckHttpResponse = HttpResponse(status = StatusCodes.OK, entity = "GOOD")
  val deleteHttpResponse = HttpResponse(status = StatusCodes.Accepted)
  val falseDeleteHttpResponse = HttpResponse(status = StatusCodes.NotFound)

  val getFileBlobHttpResponse = HttpResponse(
    status = StatusCodes.OK,
    headers = List(AmbryBlobSizeHeader(foldedBS.size.toString), Expires(expires)),
    entity = HttpEntity.Chunked.fromData(ContentTypes.`text/xml(UTF-8)`, Source(TestByteStrings))
  )

  "Unmarshaler" should {
    "unmarshal PostFile HttpResponse to AmbryPostFileResponse" in {
      val result = Unmarshal(postFileHttpResponse).to[AmbryPostFileResponse]
      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual ambryBlobInfo
      }
    }

    "unmarshal  HealthCheck HttpResponse to HealthCheck" in {
      val result = Unmarshal(healthCheckHttpResponse).to[AmbryHealthStatusResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual healthCheck
      }
    }

    "unmarshal BlobInfo HttpResponse to AmbryBlobInfoResponse" in {
      val result = Unmarshal(getBlobInfoHttpResponse).to[AmbryBlobInfoResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual getBlobInfoResponse
      }
    }
    "unmarshalTrue Delete HttpResponse to delete true boolean" in {
      val result = Unmarshal(deleteHttpResponse).to[Boolean]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual true
      }
    }

    "unmarshal False Delete HttpResponse to delete false boolean" in {
      val result = Unmarshal(falseDeleteHttpResponse).to[Boolean]

      whenReady(result, timeout(10 seconds)) { r =>
        r shouldEqual false
      }
    }

    "unmarshal Get Blob HttpResponse  AmbryBlobResponse" in {
      val result = Unmarshal(getFileBlobHttpResponse).to[AmbryGetBlobResponse]

      whenReady(result, timeout(10 seconds)) { r =>
        r.blobSize shouldEqual getBlobResponse.blobSize
        r.contentType shouldEqual getBlobResponse.contentType
        r.expires shouldEqual getBlobResponse.expires
        //todo test source equality
      }
    }



  }
