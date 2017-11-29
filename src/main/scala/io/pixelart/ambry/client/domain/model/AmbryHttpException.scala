package io.pixelart.ambry.client.domain.model

sealed trait AmbryHttpException extends Exception

final case class AmbryHttpAuthorisationException(message: String = "error.Authorisation") extends AmbryHttpException

final case class AmbryHttpConnectionException(
  error: String,
  message: String = "error.connection",
  nestedException: Throwable = null
) extends AmbryHttpException

final case class AmbryHttpBadRequestException(message: String = "error.badRequest") extends AmbryHttpException

final case class AmbryHttpFileUploadException(message: String = "error.uploading") extends AmbryHttpException

final case class AmbryHttpFileNotFoundException(message: String = "error.notfound", nestedException: Throwable = null) extends AmbryHttpException
