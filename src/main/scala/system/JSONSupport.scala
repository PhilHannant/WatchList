package system

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import messages.ActionPerformed
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val customersJsonFormat = jsonFormat2(Customer)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}

