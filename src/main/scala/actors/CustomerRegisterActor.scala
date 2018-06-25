package actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import messages._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


case class Customer(customerID: String, contentIDs: List[String])
case class CustomerContent(customerID: String, contentID: String)
case class CustomerID(customerID: String)



object CustomerRegisterActor {
  def props: Props = Props[CustomerRegisterActor]
}

class CustomerRegisterActor extends Actor with ActorLogging {



  var customers = new mutable.ListBuffer[Customer]

  def receive: Receive = {
    case GetWatchList(customer) =>
      println(customer.customerID)
      if(customers.isEmpty){ println("empty"); sender() ! CustomerWatchList(List.empty)}
      else sender() ! CustomerWatchList(customers.find(c => c.customerID == customer.customerID).get.contentIDs)
    case AddContentID(customer, contentID) =>
      if(customers.map(_.customerID).contains(customer)){
        val c = customers.find(c => c.customerID == customer).get
        val update = c.copy(customer, c.contentIDs ::: List(contentID))
        customers -= customers.find(c => c.customerID == customer).get
        customers += update
      } else {
        customers += Customer(customer, List(contentID))
      }
      sender() ! ActionPerformed(s"Customer ${contentID} added.")
    case AddAllContentIDs(customer) =>
      customers += customer
      println(customers.toList)
      sender() ! ActionPerformed(s"Customer ${customer} added.")
    case GetUser(customer) =>
      sender() ! customers.find(_ == customer)
    case DeleteContentID(customer, contentID) =>
      if(customers.map(_.customerID).contains(customer)) {
        val c = customers.find(c => c.customerID == customer).get
        val remove = c.copy(customer, c.contentIDs.filter(c => c != contentID))
        customers -= customers.find(c => c.customerID == customer).get
        customers += remove
        println(customers.toList)
        sender() ! ActionPerformed(s"ContentID: ${contentID} deleted.")
      }
      else sender() ! ActionPerformed(s"No $customer content found.")
  }

  def checkCustomer(customerID: String) = ???

}

