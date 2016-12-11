package io.pixelart.ambry.client.infrastructure.adapter.model

/**
  * Created by rabzu on 11/12/2016.
  */
object AmbryHttpModel {

  sealed trait AmbryHttpRequestModel

  sealed trait AmbryHttpResponseModel


  sealed trait HealthStatus
  object Good extends HealthStatus
  object Bad extends HealthStatus


  final case class HealthCheck(status: HealthStatus)

}
