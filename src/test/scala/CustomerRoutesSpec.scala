import actors.{Customer, CustomerRegisterActor}
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
    "return no users if no present (GET /customers)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/customers")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"contentIDs":[]}""")
      }
    }
  }

  "be able to add users (POST /customers)" in {
    val customer = Customer("123", List("zRE49", "wYqiZ", "srT5k", "FBSxr"))
    val customerEntity = Marshal(customer).to[MessageEntity].futureValue // futureValue is from ScalaFutures

    println(customerEntity)
    // using the RequestBuilding DSL:
    val request = Post("/customers").withEntity(customerEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.Created)


      // we expect the response to be json:
      contentType should ===(ContentTypes.`application/json`)

      // and we know what message we're expecting back:
      entityAs[String] should ===("""{"description":"Customer Customer(123,List(zRE49, wYqiZ, srT5k, FBSxr)) added."}""")
    }

  }


}





