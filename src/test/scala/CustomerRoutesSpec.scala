import actors.{Customer, CustomerContent, CustomerRegisterActor, CustomerID}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import system.CustomerRoutes

import scala.concurrent.duration.DurationInt

class CustomerRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with CustomerRoutes {

  val customerRegisterActor: ActorRef =
    system.actorOf(CustomerRegisterActor.props, "customerRegister")

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(5.seconds)

  lazy val routes = customerRoutes

  "CustomerRoutes" should {
    "return no content if no present (GET /customers)" in {
      val customer = CustomerID("123")
      val customerEntity = Marshal(customer).to[MessageEntity].futureValue
      val request = HttpRequest(uri = "/customers").withEntity(customerEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"contentIDs":[]}""")
      }
    }
  }

  "be able to add customerContentID (POST /customers)" in {
    val customer = CustomerContent("abc", "zRE49")
    val customerEntity = Marshal(customer).to[MessageEntity].futureValue

    val request = Post("/customers").withEntity(customerEntity)
    println(request)

    request ~> routes ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"description":"CustomerContent(abc,zRE49) added."}""")
    }
  }

  "be able to add customerContentIDs (POST /customers)" in {
    val customer = Customer("123", List("zRE49", "wYqiZ", "srT5k", "FBSxr"))
    val customerEntity = Marshal(customer).to[MessageEntity].futureValue

    val request = Post("/customers").withEntity(customerEntity)
    println(request)

    request ~> routes ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"description":"Customer(123,List(zRE49, wYqiZ, srT5k, FBSxr)) added."}""")
    }
  }

  "be able to remove a customrs contentID (DELETE /customers)" in {
    val customer = CustomerContent("123", "srT5k")
    val customerEntity = Marshal(customer).to[MessageEntity].futureValue
    val request = Delete(uri = "/customers").withEntity(customerEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)

      // we expect the response to be json:
      contentType should ===(ContentTypes.`application/json`)

      // and no entries should be in the list:
      entityAs[String] should ===("""{"description":"ContentID: srT5k deleted."}""")
    }
  }

  //need to add test for single customer and content id


}





