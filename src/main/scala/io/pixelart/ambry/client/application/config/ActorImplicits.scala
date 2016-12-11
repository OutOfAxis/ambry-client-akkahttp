package io.pixelart.ambry.client.application.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait ActorImplicits {
  implicit def actorSystem: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def executionContext: ExecutionContext
}
