import java.text.SimpleDateFormat

import actors.CustomerRegisterActor
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import messages._
import akka.util.Timeout
import akka.pattern.ask
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, Future, duration}

class CustomerRegisterActorTest extends FlatSpec with Matchers {

  val system = ActorSystem("bookingSystem")
  val customerRegisterActor = system.actorOf(Props[CustomerRegisterActor], "customerRegsiterActor")
  implicit val timeout = Timeout(5 seconds)


  val bmActorRef: ActorRef = customerRegisterActor

  "addConetent" should "add content for a customer and return an ActionPerformed" in {
    val addContent = bmActorRef ? AddContentID("123", "zRE49")
    val result = Await.result(addContent, timeout.duration)
    println(result)
    result should be (ActionPerformed(s"Customer zRE49 added."))
  }

}