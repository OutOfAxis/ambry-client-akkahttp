package io.pixelart.ambry.client.infrastructure.adapter

import akka.http.scaladsl.model.HttpRequest
import com.softwaremill.tagging.@@
import com.typesafe.scalalogging.StrictLogging
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.pixelart.ambry.client.application.config.ActorImplicits
import io.pixelart.ambry.client.domain.model.{ UploadBlobRequestData, AmbryUri, Blob, AmbryId }

/**
 * Created by rabzu on 11/12/2016.
 */
trait AmbryHttpRequests extends StrictLogging with ActorImplicits {

  /**
   * GET /healthCheck
   *
   * Can be used to check the status of the frontend.
   * Status here refers to the frontend's ability to answer requests.
   *
   * A successful response will be returned with a status code of 200 OK
   * and the body of the response will contain the status of frontend - GOOD/BAD.
   *
   */
  def healthStatusHttpRequest(url: AmbryUri): HttpRequest

  /**
   * POST /
   *
   * This API uploads a blob to Ambry
   *
   * The API returns a resource ID that can be used to access the blob.
   *
   * API:
   *
   */
  def uploadBlobHttpRequest(ambryUri: AmbryUri, uploadBlobData: UploadBlobRequestData): HttpRequest

  /**
   * GET /<ambry-id>
   *
   * gets the content of the blob represented by the blob ID
   *
   */
  def getBlobHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest

  /**
   * GET /<ambry-id>/BlobInfo
   *
   */
  def getBlobInfoHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest

  /**
   * GET /<ambry-id>/UserMetadata
   *
   *  HEADER Zero or more headers with this prefix that represent user metadata
   *
   *  x-ambry-um-
   *
   *  The user metadata as response headers.
   *
   *  The response headers will contain the user metadata that was uploaded (if any)
   *
   */
  //  def getUserMetadata(url: AmbryUri, headerSuffix: String*)
  /**
   * GET /<ambry-id>
   *
   * Supports the "If-Modified-Since" header and
   *
   * returns 304 Not_Modified if the blob has not been modified since the time specified
   *
   * If the blob has not been modified since the time specified
   *
   * todo: what date format
   *
   */
  //  def modifiedSince(url: AmbryUri)
  /**
   * HEAD /<ambry-id>
   * Gets the blob properties of the blob represented by the supplied blob ID.
   *
   */
  def getBlobPropertiesHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest

  /**
   * DELETE /<ambry-id>
   *
   * Deletes the blob represented by the supplied blob ID.
   *
   */
  def deleteBlobHttpRequest(ambryUri: AmbryUri, ambryId: AmbryId): HttpRequest

}
