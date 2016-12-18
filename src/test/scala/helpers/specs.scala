package helpers

import java.util.concurrent.TimeUnit
import akka.actor.{ ActorIdentity, ActorRef, ActorSystem, Identify }
import akka.http.scaladsl.testkit.{ RouteTest, ScalatestRouteTest, TestFrameworkInterface }
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.testkit.{ ImplicitSender, TestKit }
import io.pixelart.ambry.client.application.ActorImplicits
import org.scalatest._
import org.scalatest.exceptions.TestFailedException
import org.scalatest.prop.{ Checkers, GeneratorDrivenPropertyChecks, PropertyChecks }
import org.scalatest.concurrent.{ IntegrationPatience, ScalaFutures }
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{ DurationInt, FiniteDuration }
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

//import org.scalamock.annotation.mockWithCompanion
//import org.scalamock.annotation.mockObject

trait BaseSpec
  extends WordSpecLike with MustMatchers with OptionValues with BeforeAndAfter with OneInstancePerTest
  with ScalaFutures with IntegrationPatience with PropertyChecks
  with Inside with Inspectors

//    with ProxyMockFactory
//    with GeneratedMockFactory

abstract class AkkaSpec(name: String) extends TestKit(ActorSystem(s"$name-spec")) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {
  //  self: BaseSpec =>

  implicit lazy val dispatcher = system.dispatcher

  implicit lazy val mat = ActorMaterializer()(system)

  trait ActorImplicitsTest extends ActorImplicits {
    override implicit val actorSystem: ActorSystem = system

    override implicit val executionContext: ExecutionContext = dispatcher

    override implicit val materializer: ActorMaterializer = mat
  }
  //  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withFuzzing(true))

  override protected def afterAll(): Unit =
    shutdown(system)

  def expectActor(path: String, max: FiniteDuration = 5 seconds): ActorRef =
    within(max) {
      var actor = null: ActorRef
      awaitAssert {
        system.actorSelection(path) ! Identify(path)
        expectMsgPF(250 milliseconds) { case ActorIdentity(`path`, Some(a)) => actor = a }
      }
      actor
    }
}

