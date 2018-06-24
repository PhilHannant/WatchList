package actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import messages._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


case class Customer(customerID: String, contentIDs: List[String])


object CustomerRegisterActor {
  def props: Props = Props[CustomerRegisterActor]
}

class CustomerRegisterActor extends Actor with ActorLogging {



  var customers = new mutable.ListBuffer[Customer]

  def receive: Receive = {
    case GetWatchList(customer) =>
      sender() ! customers.find(c => c.customerID == customer).get.contentIDs
    case AddContentID(customer, contentID) =>
      if(customers.contains(customer)){
        val c = customers.find(c => c.customerID == customer).get
        val update = c.copy(customer, c.contentIDs ::: List(contentID))
        customers -= customers.find(c => c.customerID == customer).get
        customers += update
      } else {
        customers += Customer(customer, List(contentID))
      }
      sender() ! ActionPerformed(s"Customer ${contentID} added.")
    case GetUser(customer) =>
      sender() ! customers.find(_ == customer)
//    case DeleteContentID(customer, contentID) =>
//      customers.put(customer, customers.get(customer).filter(cID => cID != contentID).get)
//      sender() ! ActionPerformed(s"Customer ${contentID} deleted.")
  }


}

