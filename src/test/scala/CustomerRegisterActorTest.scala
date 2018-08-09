
import actors.{Customer, CustomerContent, CustomerID, CustomerRegisterActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import messages.{AddContentID, _}
import akka.util.Timeout
import akka.pattern.ask
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await}

/**
  * CustomerRegisterActorTest used to check functionality of customerRegisterTest
  */

class CustomerRegisterActorTest extends FlatSpec with Matchers {

  val system = ActorSystem("bookingSystem")
  val customerRegisterActor = system.actorOf(Props[CustomerRegisterActor], "customerRegsiterActor")
  implicit val timeout = Timeout(5 seconds)


  val crActorRef: ActorRef = customerRegisterActor

  "addContent" should "add content for a customer and return an ActionPerformed" in {
    val addContent = crActorRef ? AddContentID(CustomerContent("123", "zRE49"))
    val result = Await.result(addContent, timeout.duration)
    println(result)
    result should be (ActionPerformed("CustomerContent(123,zRE49) added."))
  }

  "addContentID" should "not add duplicates" in {
    crActorRef ? AddContentID(CustomerContent("123", "zRE49"))
    crActorRef ? AddContentID(CustomerContent("123", "zRE49"))
    crActorRef ? AddContentID(CustomerContent("123", "wYqiZ"))
    crActorRef ? AddContentID(CustomerContent("123", "wYqiZ"))
    val watchList = crActorRef ? GetWatchList(CustomerID("123"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ")))
  }

  "getWatchList" should "return a list of watchItems" in {
    crActorRef ? AddContentID(CustomerContent("123", "zRE49"))
    crActorRef ? AddContentID(CustomerContent("123", "wYqiZ"))
    crActorRef ? AddContentID(CustomerContent("123", "15nW5"))
    crActorRef ? AddContentID(CustomerContent("123", "srT5k"))
    crActorRef ? AddContentID(CustomerContent("123", "FBSxr"))
    val watchList = crActorRef ? GetWatchList(CustomerID("123"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr")))
  }

  "deleteContentID" should "remove a contentId from a customer's watchlist" in {
    crActorRef ? AddContentID(CustomerContent("123", "zRE49"))
    crActorRef ? AddContentID(CustomerContent("123", "wYqiZ"))
    crActorRef ? AddContentID(CustomerContent("123", "15nW5"))
    crActorRef ? AddContentID(CustomerContent("123", "srT5k"))
    crActorRef ? AddContentID(CustomerContent("123", "FBSxr"))
    crActorRef ? DeleteContentID("123", "15nW5")
    val watchList = crActorRef ? GetWatchList(CustomerID("123"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ", "srT5k", "FBSxr")))
  }

  "addAllContentIDs" should "not add duplicate contentIDs" in {
    val customer = Customer("abc", List("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr", "zRE49", "wYqiZ", "15nW5"))
    crActorRef ? AddAllContentIDs(customer)
    val watchList = crActorRef ? GetWatchList(CustomerID("abc"))
    val result = Await.result(watchList, timeout.duration)
    println(result)
    result should be (CustomerWatchList(List("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr")))
  }

  "getHouseholdLists" should "return a list of contentId lists" in {
    val customer1 = Customer("xyz", List("zRE49", "wYqiZ", "15nW5", "srT5k"), "house1")
    val customer2 = Customer("ef1", List("FBSxr", "zRE49", "wYqiZ", "15nW5"), "house1")
    val customer3 = Customer("wej", List("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr", "zRE49", "wYqiZ", "15nW5"), "house2")
    crActorRef ? AddAllContentIDs(customer1)
    crActorRef ? AddAllContentIDs(customer2)
    crActorRef ? AddAllContentIDs(customer3)
    val householdList = crActorRef ? GetHouseHoldLists(customer2)
    val result = Await.result(householdList, timeout.duration)
    println(result)
    result should be (HouseholdWatchLists(List(List("zRE49", "wYqiZ", "15nW5", "srT5k"),
      List("FBSxr", "zRE49", "wYqiZ", "15nW5"))))
  }

}
