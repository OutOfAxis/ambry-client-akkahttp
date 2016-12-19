package io.pixelart.ambry.client.domain.model

import java.util.NoSuchElementException
import akka.http.scaladsl.model.{ Uri, ContentType }
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.github.nscala_time.time.Imports.DateTime
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
/**
 * Created by rabzu on 14/12/2016.
 */

sealed trait AmbryHttpRequestModel

sealed trait AmbryHttpResponseModel

case class AmbryId(value: String)

case class AmbryOwnerId(value: String)

case class AmbryServiceId(value: String)

//todo: replace it with akka Uri model
case class AmbryUri(uri: String)

object AmbryHealthStatusResponse {
  def apply(status: String): AmbryHealthStatusResponse = status match {
    case "GOOD" => AmbryHealthStatusResponse(Good)
    case "BAD"  => AmbryHealthStatusResponse(Bad)
    case _      => throw new NoSuchElementException
  }
}

sealed trait HealthStatus

object Good extends HealthStatus

object Bad extends HealthStatus

/**
 * Ambry Front end server response
 *
 * @param status
 */
case class AmbryHealthStatusResponse(status: HealthStatus) extends AmbryHttpResponseModel

//todo: 1. add user metadata support
//todo: 2. make non required header fields Optional
final case class UploadBlobRequestData(
  blobSource: Source[ByteString, Any],
  size: Long,
  serviceId: AmbryServiceId,
  contentType: ContentType,
  ttl: Long = -1,
  prvt: Boolean = false,
  ownerId: AmbryOwnerId
) extends AmbryHttpRequestModel

//todo: Add User Metadata fields
case class AmbryBlobInfoResponse(
  blobSize: Long,
  serviceId: AmbryServiceId,
  creationTime: String,
  isPrivate: Boolean,
  ambryContentType: String,
  ttl: Option[Long] = Some(-1),
  ownerId: Option[AmbryOwnerId]
) extends AmbryHttpResponseModel

case class AmbryGetBlobResponse(
  blob: Source[ByteString, Any],
  blobSize: Long,
  contentType: ContentType,
  expires: DateTime
) extends AmbryHttpResponseModel

/**
 * The resource id returned after save on Ambry.
 */
case class AmbryBlobUploadResponse(ambryId: AmbryId) extends AmbryHttpResponseModel

//todo
//case class AmbryUMResponse(umDesc: String)

