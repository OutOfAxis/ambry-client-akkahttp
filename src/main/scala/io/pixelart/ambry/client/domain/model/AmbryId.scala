package io.pixelart.ambry.client.domain.model

import akka.stream.scaladsl.Source
import akka.util.ByteString

/**
  * Created by rabzu on 11/12/2016.
  */
case class AmbryId(value: String)

case class Blob(data:Source[ByteString, Any])
