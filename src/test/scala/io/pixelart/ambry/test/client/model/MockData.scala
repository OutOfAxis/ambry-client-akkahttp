package io.pixelart.ambry.test.client.model

import java.nio.file.{ Files, Paths }

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ MediaTypes, ContentType, ContentTypes }
import akka.stream.scaladsl.{ FileIO, Source }
import akka.util.ByteString
import com.github.nscala_time.time.Imports._
import com.typesafe.config.ConfigFactory
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._
import scala.collection.mutable.ListBuffer

/**
 * Created by rabzu on 18/12/2016.
 */
object MockData {

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
    creationTimeHeader.date.toString,
    privateHeader.prvt,
    contentTypeHeader.contentType,
    Some((nowMillis.getMillis * 0.001).toLong),
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

  val serviceId = AmbryServiceId("ServiceId")
  val ownerId = AmbryOwnerId("OwnerId")

  val testFileLocalPath = Paths.get(getClass.getResource("/media/test_image.jpg").getPath)
  val testFileSize = Files.size(testFileLocalPath)
  val source = FileIO.fromPath(testFileLocalPath)

  val uploadData = UploadBlobRequestData(source, testFileSize, serviceId, MediaTypes.`image/jpeg`, ownerId = ownerId)

  /**RawHeaders*/

  val headersList = scala.collection.immutable.Seq(
    RawHeader(blobSizeHeader.name, blobSizeHeader.size.toString),
    RawHeader(serviceIdHeader.name(), serviceIdHeader.id.value),
    RawHeader(contentTypeHeader.name(), contentTypeHeader.value),
    RawHeader(ttlHeader.name(), ttlHeader.value),
    RawHeader(privateHeader.name(), privateHeader.value),
    RawHeader(ownerIdHeader.name(), ownerIdHeader.value)
  )

}
