package io.pixelart.ambry.client.model.test

import akka.http.scaladsl.model
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model.httpModel._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.language.postfixOps
import MockData._
import akka.http.javadsl.model.headers.ContentLength
import io.pixelart.ambry.client.application.test.AkkaSpec

/**
 * Created by rabzu on 15/12/2016.
 */
class UnmarshallerSpec extends AkkaSpec("unmarshal") with ScalaFutures with StrictLogging {

  import io.pixelart.ambry.client.infrastructure.translator.AmbryResponseUnmarshallers._

  import akka.http.scaladsl.unmarshalling._

  "Custom Header" should {
    val blobsize: Long = 123

    "unapply" in {
      val AmbryBlobSizeHeader(t1) = AmbryBlobSizeHeader(blobsize)
      t1 should ===(blobsize)
    }

    "match" in {
      val AmbryBlobSizeHeader(v3) = RawHeader(AmbryBlobSizeHeader.name, blobsize.toString)
      v3 should ===(blobsize.toString)
    }

    "convert raw header to custom header" in {
      List(RawHeader("x-ambry-blob-size", "123")).collectFirst {
        case AmbryBlobSizeHeader(size) =>
          size should ===("123")
      }
    }

    "convert custom header to raw header" in {
      List(AmbryBlobSizeHeader(blobsize)).collectFirst {
        case HttpHeader("x-ambry-blob-size", size) =>
          size should ===("123")
      }
    }
  }

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

  val getBlobHttpResponseStreamed = HttpResponse(
    status = StatusCodes.OK,
    headers = List(AmbryBlobSizeHeader(foldedBS.size.toString), expiresHeader, `Content-Length`(213)),
    entity = HttpEntity.Chunked.fromData(ContentTypes.`text/xml(UTF-8)`, Source(TestByteStrings))
  )

  logger.info(getBlobInfoHttpResponse.toString())

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
//
//      "7. unmarshal Get Stream Blob HttpResponse as  AmbryBlobResponse" in {
//      val result = Unmarshal(getBlobHttpResponseStreamed).to[AmbryGetBlobResponse]
//      whenReady(result, timeout(10 seconds)) { r =>
//        r. shouldEqual getBlobHttpResponseStreamed.headers.collectFirst{case h: ContentLength => h.length}
//        //todo test source equality
//      }
//    }




  }
}
