package io.pixelart.ambry.client.infrastructure.translator

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.unmarshalling._
import io.pixelart.ambry.client.domain.model.AmbryHttpHeaderModel._
import io.pixelart.ambry.client.domain.model.{AmbryId, AmbryBlobInfoResponse, AmbryHealthStatusResponse, AmbryPostFileResponse}

/**
 * Created by rabzu on 11/12/2016.
 */
package object AmbryResponseUnmarshallers {


  object Unmarhsallers {

    implicit final val fromHealthCheckResponse: FromEntityUnmarshaller[AmbryHealthStatusResponse] =
      PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(AmbryHealthStatusResponse(_))

    implicit final val fromUploadResponse: FromResponseUnmarshaller[AmbryPostFileResponse] = {
      //  val h: Class[Location] = headers.Location.getClass[Location]
      def unmarshal(response: HttpResponse) =
        AmbryPostFileResponse(AmbryId(response.getHeader("Location").get.asInstanceOf[Location].uri.toString()))

      Unmarshaller.strict(unmarshal)
    }

    implicit final val fromGetFileResponse: FromResponseUnmarshaller[AmbryBlobInfoResponse] = {
      //  val h: Class[Location] = headers.Location.getClass[Location]
      def unmarshal(response: HttpResponse) = {

        val size = response.getHeader(AmbryBlobSizeHeader.name).get.asInstanceOf[AmbryBlobSizeHeader].size
        val serviceId = response.getHeader(AmbryServiceIdHeader.name).get.asInstanceOf[AmbryServiceIdHeader].value
        val creationTime = response.getHeader(AmbryCreationTimeHeader.name).get.asInstanceOf[AmbryCreationTimeHeader].value
        val privateHeader = response.getHeader(AmbryPrivateHeader.name).get.asInstanceOf[AmbryPrivateHeader].prvt

        val contentTypeHeader = response.getHeader(AmbryContentTypeHeader.name).get.asInstanceOf[AmbryContentTypeHeader].value
        val ownerIdHeader = response.getHeader(AmbryOwnerIdHeader.name).get.asInstanceOf[AmbryContentTypeHeader].value

        AmbryBlobInfoResponse(size, serviceId, creationTime, privateHeader, contentTypeHeader, ownerIdHeader)
      }

      Unmarshaller.strict(unmarshal)
    }

  }


}