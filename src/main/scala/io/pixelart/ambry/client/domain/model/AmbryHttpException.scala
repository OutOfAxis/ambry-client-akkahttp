package io.pixelart.ambry.client.domain.model


sealed trait AmbryHttpException extends Exception

final case class AmbryHttpResponseException(
  error: String
) extends AmbryHttpException

final case class AmbryHttpAuthorisationException(
  error: String,
  message: String = "error.parsing"
//  nestedException: Throwable = null
) extends AmbryHttpException

final case class AmbryHttpConnectionException(
  error: String,
  message: String = "error.connection",
  nestedException: Throwable = null
) extends AmbryHttpException

final case class AmbryHttpBadRequestException(
  error: String,
  message: Option[String] = Some("error.badRequest")
//  nestedException: Throwable = null
) extends AmbryHttpException

final case class AmbryHttpFileUploadException(
  systemId: String,
  message: String = "error.uploading",
  nestedException: Throwable = null
) extends AmbryHttpException
