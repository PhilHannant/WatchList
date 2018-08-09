package system

import actors.{Customer, CustomerContent, CustomerID}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import messages.{ActionPerformed, CustomerWatchList}
import spray.json.DefaultJsonProtocol

/**
  * JsonSupport trait holding details of how Spray JSON will convert case classes used.
  */

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._


  implicit val customersJsonFormat = jsonFormat3(Customer)

  implicit val customerIDJsonFormat = jsonFormat1(CustomerID)

  implicit val customerContentJsonFormat = jsonFormat2(CustomerContent)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val customerWatchList = jsonFormat1(CustomerWatchList)
}

