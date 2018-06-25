import java.text.SimpleDateFormat

import actors.{CustomerID, CustomerRegisterActor}
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import messages._
import akka.util.Timeout
import akka.pattern.ask
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, Future, duration}

class CustomerRegisterActorTest extends FlatSpec with Matchers {

  val system = ActorSystem("bookingSystem")
  val customerRegisterActor = system.actorOf(Props[CustomerRegisterActor], "customerRegsiterActor")
  implicit val timeout = Timeout(5 seconds)


  val crActorRef: ActorRef = customerRegisterActor

  "addConetent" should "add content for a customer and return an ActionPerformed" in {
    val addContent = crActorRef ? AddContentID("123", "zRE49")
    val result = Await.result(addContent, timeout.duration)
    println(result)
    result should be (ActionPerformed(s"Customer zRE49 added."))
  }

  "getWatchList" should "return a list of watchItems" in {
    crActorRef ? AddContentID("123", "zRE49")
    crActorRef ? AddContentID("123", "wYqiZ")
    crActorRef ? AddContentID("123", "15nW5")
    crActorRef ? AddContentID("123", "srT5k")
    crActorRef ? AddContentID("123", "FBSxr")
    val watchList = crActorRef ? GetWatchList(CustomerID("123"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr")))
  }

  "deleteContentID" should "remove a contentId from a customer's watchlist" in {
    crActorRef ? AddContentID("123", "zRE49")
    crActorRef ? AddContentID("123", "wYqiZ")
    crActorRef ? AddContentID("123", "15nW5")
    crActorRef ? AddContentID("123", "srT5k")
    crActorRef ? AddContentID("123", "FBSxr")
    crActorRef ? DeleteContentID("123", "15nW5")
    val watchList = crActorRef ? GetWatchList(CustomerID("123"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ", "srT5k", "FBSxr")))

  }

}
