package io.pixelart.ambry.client.application.config

import com.softwaremill.tagging._
import com.typesafe.config.{ ConfigFactory, Config }

/**
 * Created by rabzu on 11/12/2016.
 */
trait ConfigModule {

  lazy val config: Config @@ GlobalConfig = {
    ConfigFactory.load().taggedWith[GlobalConfig]
  }

}
