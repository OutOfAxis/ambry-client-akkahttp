package io.pixelart.ambry.client.infrastructure.translator

import akka.http.javadsl.model.headers.{ ContentLength, LastModified }
import akka.http.scaladsl.model.{ HttpHeader, HttpResponse, StatusCodes }
import akka.http.scaladsl.model.headers.{ Expires, Location, `Content-Length`, `Content-Range`, `Content-Type` }
import akka.http.scaladsl.unmarshalling._
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._
import com.github.nscala_time.time.Imports.DateTime
import io.pixelart.ambry.client.domain.model.httpModel._
import org.joda.time.format.DateTimeFormat

/**
 * Created by rabzu on 11/12/2016.
 */

//fixme: for some reason akka-http does not recognise AmbryHeaders as Custom headers. its wiered because Spec shows equivalece of the two
package object AmbryResponseUnmarshallers extends StrictLogging {

  implicit final val fromHealthCheckResponse: FromEntityUnmarshaller[AmbryHealthStatusResponse] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(AmbryHealthStatusResponse(_))

  implicit final val fromDeleteResponse: FromResponseUnmarshaller[Boolean] = {
    def unmarshal: PartialFunction[HttpResponse, Boolean] = {
      case HttpResponse(StatusCodes.Accepted, _, _, _) => true
      case _                                           => false
    }

    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromUploadResponse: FromResponseUnmarshaller[AmbryBlobUploadResponse] = {

    def unmarshal(response: HttpResponse) = {

      val locheader = response
        .headers
        .collectFirst { case l: Location => l }
        .getOrElse(throw new NoSuchElementException("header not found: Location"))

      AmbryBlobUploadResponse(AmbryId.fromAmbry(locheader.uri.toString))
    }
    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromGetBlobResponse: FromResponseUnmarshaller[AmbryGetBlobResponse] = {

    def unmarshal(response: HttpResponse) = {

      val sizeHeader = response
        .headers
        .collectFirst {
          case HttpHeader("x-ambry-blob-size", value) ⇒ value
        }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryBlobSizeHeader.name}"))

//      val expiresHeader = response
//        .headers
//        .collectFirst { case h: Expires => h }
//        .getOrElse(throw new NoSuchElementException("header not found: Expires"))

//      val e = new DateTime(expiresHeader.date.clicks)

      //      val contentLengthOption = response
      //        .headers
      //        .collectFirst {
      //          case h: ContentLength => h.length
      //        }

      val contentRangeOption = response
        .headers
        .collectFirst {
          case h: `Content-Range` => h.contentRange
        }

      AmbryGetBlobResponse(response.entity.dataBytes, sizeHeader.toLong, response.entity.contentType, contentRangeOption)
    }
    Unmarshaller.strict(unmarshal)
  }

  implicit final val fromGetBlobInfoResponse: FromResponseUnmarshaller[AmbryBlobInfoResponse] = {
    def unmarshal(response: HttpResponse) = {
      val sizeHeader = response
        .headers
        .collectFirst {
          case HttpHeader("x-ambry-blob-size", value) ⇒ value
        }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryBlobSizeHeader.name}"))

      val serviceIdHeader = response
        .headers
        .collectFirst { case HttpHeader(AmbryServiceIdHeader.name, value) => value }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryServiceIdHeader.name}"))

      val creationTimeHeader = response
        .headers
        .collectFirst {
          case HttpHeader(AmbryCreationTimeHeader.name, value) =>
            value
        }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryCreationTimeHeader.name}"))

      val privateHeader = response
        .headers
        .collectFirst { case HttpHeader(AmbryPrivateHeader.name, value) => value }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryPrivateHeader.name}"))

      val contentTypeHeader = response
        .headers
        .collectFirst { case HttpHeader(AmbryContentTypeHeader.name, value) =>  `Content-Type`.parseFromValueString(value).right.get.contentType }
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryContentTypeHeader.name}"))

      val ttlHeader = response
        .headers
        .collectFirst { case HttpHeader(AmbryTtlHeader.name, value) => value }

      val ownerIdHeader = response
        .headers
        .collectFirst { case HttpHeader(AmbryOwnerIdHeader.name, value) => AmbryOwnerId(value) }

      AmbryBlobInfoResponse(
        sizeHeader.toLong,
        AmbryServiceId(serviceIdHeader),
        creationTimeHeader,
        privateHeader.toBoolean,
        contentTypeHeader,
        ttlHeader.map(_.toLong),
        ownerIdHeader
      )
    }
    Unmarshaller.strict(unmarshal)
  }
}