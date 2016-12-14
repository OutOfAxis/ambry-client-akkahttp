package io.pixelart.ambry.client.infrastructure.service

import com.softwaremill.tagging.@@
import com.typesafe.scalalogging.StrictLogging
import io.pixelart.ambry.client.application.config.{AmbryPort, AmbryHostname}

/**
  * Created by rabzu on 11/12/2016.
  */


class AmbryClient(hostname: String @@ AmbryHostname, port: String @@ AmbryPort = 1174) extends StrictLogging {






}
