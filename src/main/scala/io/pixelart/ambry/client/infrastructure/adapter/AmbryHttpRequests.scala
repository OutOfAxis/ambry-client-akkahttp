package io.pixelart.ambry.client.infrastructure.adapter

import com.softwaremill.tagging.@@
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.config.{Endpoint, ActorImplicits}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.pixelart.ambry.client.domain.model.AmbryId

/**
  * Created by rabzu on 11/12/2016.
  */
trait AmbryHttpRequests  extends StrictLogging with ActorImplicits {

/**
  * GET /healthCheck
  *
  * Can be used to check the status of the frontend.
  * Status here refers to the frontend's ability to answer requests.
  *
  * A successful response will be returned with a status code of 200 OK
  * and the body of the response will contain the status of frontend - GOOD/BAD.
  *
  * */
def healthCheck(url: String @@ Endpoint)

  /**
    * POST /
    *
    * This API uploads a blob to Ambry
    *
    * The API returns a resource ID that can be used to access the blob.
    *
    * API:
    *
    * */
  def postBlob(url: String @@ Endpoint, data: Source[ByteString, Any])

  /**
    *GET /<ambry-id>
    *
    * gets the content of the blob represented by the blob ID
    *
    * */
  def getBlob(url: String @@ Endpoint, data: Source[ByteString, Any])

  /**
    * GET /<ambry-id>/BlobInfo
    *
    * */
  def getBlobInfo(url: String @@ Endpoint, data: Source[ByteString, Any])



  /**
    * GET /<ambry-id>/
    *
    *  Supports the "If-Modified-Since" header and
    *  returns 304 Not_Modified if the blob has not been modified since the time specified
    *
    *
    *
    * */
  def getUserMetadata(url: String @@ Endpoint, data: Source[ByteString, Any])

  /**
    * GET /<ambry-id>
    *
    * If the blob has not been modified since the time specified
    * */
  def modifiedSince(url: String @@ Endpoint, data: Source[ByteString, Any])

  /**
    * HEAD /<ambry-id>
    * Gets the blob properties of the blob represented by the supplied blob ID.
    *
    * */
def getBlobProperties(url: String @@ Endpoint, blobId: AmbryId)

  /**
    * DELETE /<ambry-id>
    *
    * Deletes the blob represented by the supplied blob ID.
    *
    * */
def deleteBlob(url: String @@ Endpoint, blobId: AmbryId)

}
