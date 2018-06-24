package system

import actors.{Customer, CustomerContent}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import messages.{ActionPerformed, CustomerWatchList}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val customersJsonFormat = jsonFormat2(Customer)

  implicit val customerContentJsonFormat = jsonFormat2(CustomerContent)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val customerWatchList = jsonFormat1(CustomerWatchList)
}

