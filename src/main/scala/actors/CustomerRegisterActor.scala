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
      sender() ! CustomerWatchList(getWatcListHandler(customer))
    case AddContentID(customerID, contentID) =>
      if(checkCustomer(customerID)){
        val c = customers.find(c => c.customerID == customerID).get
        val update = c.copy(customerID, c.contentIDs ::: List(contentID))
        customers -= customers.find(c => c.customerID == customerID).get
        customers += update
      } else {
        customers += Customer(customerID, List(contentID))
      }
      sender() ! ActionPerformed(s"Customer ${contentID} added.")
    case AddAllContentIDs(customer) =>
      customers += customer
      sender() ! ActionPerformed(s"Customer ${customer} added.")
    case GetCustomer(customer) =>
      println("GetCustomer")
      if(checkCustomer(customer.customerID)) sender() ! CustomerWatchList(getCustomerContent(customer.customerID))
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

  def getWatcListHandler(customer: CustomerID) = {
    if(customers.nonEmpty && checkCustomer(customer.customerID)) getCustomerContent(customer.customerID)
    else List.empty
  }

  def checkCustomer(customerID: String) = {
    if(customers.map(_.customerID).contains(customerID)) true
    else false
  }

  def getCustomerContent(customerID: String) = {
    customers.find(c => c.customerID == customerID).get.contentIDs
  }
}

