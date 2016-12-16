package io.pixelart.ambry.client.infrastructure.translator

import _root_.temp.AmbryBlobInfoResponse
import _root_.temp.AmbryGetBlobResponse
import _root_.temp.AmbryHealthStatusResponse
import _root_.temp.AmbryHttpHeaderModel.AmbryBlobSizeHeader
import _root_.temp.AmbryHttpHeaderModel.AmbryContentTypeHeader
import _root_.temp.AmbryHttpHeaderModel.AmbryCreationTimeHeader
import _root_.temp.AmbryHttpHeaderModel.AmbryOwnerIdHeader
import _root_.temp.AmbryHttpHeaderModel.AmbryPrivateHeader
import _root_.temp.AmbryHttpHeaderModel.AmbryServiceIdHeader
import _root_.temp.AmbryId
import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.model.headers.{Expires, Location}
import akka.http.scaladsl.unmarshalling._
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model._

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

    implicit final val fromUploadResponse: FromResponseUnmarshaller[AmbryPostFileResponse] = {

    //  val h: Class[Location] = headers.Location.getClass[Location]
    def unmarshal(response: HttpResponse) = {

      val locheader = response
        .headers
        .collect { case l: Location => l }
        .headOption
        .getOrElse(throw new NoSuchElementException("header not found: Location"))

      AmbryPostFileResponse(AmbryId(locheader.uri.toString))
    }
    Unmarshaller.strict(unmarshal)
  }

    implicit final val fromGetBlobResponse: FromResponseUnmarshaller[AmbryGetBlobResponse] = {

    //  val h: Class[Location] = headers.Location.getClass[Location]
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

      AmbryGetBlobResponse(response.entity.dataBytes, sizeHeader.size, response.entity.contentType, expiresHeader.date)
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

      val ownerIdHeader = response
        .headers
        .collect { case h: AmbryOwnerIdHeader => h }
        .headOption
        .getOrElse(throw new NoSuchElementException(s"header not found: ${AmbryOwnerIdHeader.name}"))

      AmbryBlobInfoResponse(sizeHeader.size, serviceIdHeader.id, creationTimeHeader.time, privateHeader.prvt, contentTypeHeader.contentType, ownerIdHeader.ownerId)
    }

    Unmarshaller.strict(unmarshal)
  }

  }


}