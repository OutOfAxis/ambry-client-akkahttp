package io.pixelart.ambry.client.infrastructure.translator

import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.model.headers.{Expires, Location}
import akka.http.scaladsl.unmarshalling._
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._
import com.github.nscala_time.time.Imports.DateTime


/**
 * Created by rabzu on 11/12/2016.
 */
package object AmbryResponseUnmarshallers {

  implicit final val fromHealthCheckResponse: FromEntityUnmarshaller[AmbryHealthStatusResponse] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(AmbryHealthStatusResponse(_))

  implicit final val fromDeleteResponse: FromResponseUnmarshaller[Boolean] = {
    def unmarshal: PartialFunction[HttpResponse, Boolean] = {
      case HttpResponse(StatusCodes.Accepted, _, _, _) => true
      case _ => false
    }

    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromUploadResponse: FromResponseUnmarshaller[AmbryBlobUploadResponse] = {

    def unmarshal(response: HttpResponse) = {

      val locheader = response
        .headers
        .collect { case l: Location => l }
        .headOption
        .getOrElse(throw new NoSuchElementException("header not found: Location"))

      AmbryBlobUploadResponse(AmbryId(locheader.uri.toString))
    }
    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromGetBlobResponse: FromResponseUnmarshaller[AmbryGetBlobResponse] = {

    def unmarshal(response: HttpResponse) = {

      val sizeHeader = response
        .headers
        .collect { case h: AmbryBlobSizeHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryBlobSizeHeader.name}"))

      val expiresHeader = response
        .headers
        .collect { case h: Expires => h }
        .headOption
        .getOrElse(throw new NoSuchElementException("header not found: Expires"))

      val e = new DateTime(expiresHeader.date.clicks)
      AmbryGetBlobResponse(response.entity.dataBytes, sizeHeader.size, response.entity.contentType, e)
    }
    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromGetBlobInfoResponse: FromResponseUnmarshaller[AmbryBlobInfoResponse] = {
    def unmarshal(response: HttpResponse) = {

      val sizeHeader = response
        .headers
        .collect { case h: AmbryBlobSizeHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryBlobSizeHeader.name}"))

      val serviceIdHeader = response
        .headers
        .collect { case h: AmbryServiceIdHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryServiceIdHeader.name}"))

      val creationTimeHeader = response
        .headers
        .collect { case h: AmbryCreationTimeHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryCreationTimeHeader.name}"))

      val privateHeader = response
        .headers
        .collect { case h: AmbryPrivateHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryPrivateHeader.name}"))

      val contentTypeHeader = response
        .headers
        .collect { case h: AmbryContentTypeHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryContentTypeHeader.name}"))

      val ttlHeader = response
        .headers
        .collect { case h: AmbryTtlHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryTtlHeader.name}"))


      val ownerIdHeader = response
        .headers
        .collect { case h: AmbryOwnerIdHeader => h }
        .headOption

      AmbryBlobInfoResponse(
        sizeHeader.size,
        serviceIdHeader.id,
        creationTimeHeader.date,
        privateHeader.prvt,
        contentTypeHeader.contentType,
        ttlHeader.ttl,
        ownerIdHeader.map(_.ownerId)
      )
    }
    Unmarshaller.strict(unmarshal)
  }
}