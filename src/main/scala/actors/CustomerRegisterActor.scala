package actors

import akka.actor.{Actor, ActorLogging, Props}
import messages._

import scala.collection.mutable

/**
  * Customer case class to hold customer ID and list of contentIDs
  * @param customerID
  * @param contentIDs
  */

case class Customer(customerID: String, contentIDs: List[String])

/**
  * CustomerContent case class to hold key vlaue pair between customrID and contentID
  * @param customerID
  * @param contentID
  */
case class CustomerContent(customerID: String, contentID: String)

/**
  * CustomerID case class used to hold just customerID for json queries
  * @param customerID
  */
case class CustomerID(customerID: String)

/**
  * CustomerRegisterActor companion object
  */

object CustomerRegisterActor {
  def props: Props = Props[CustomerRegisterActor]
}

/**
  * CustomerRegisterActor
  */
class CustomerRegisterActor extends Actor with ActorLogging {

  //mutable listBuffer used to as customer list would need to be updated frequently
  var customers = new mutable.ListBuffer[Customer]

  /**
    * Actor Receive function
    * @return
    */
  def receive: Receive = {
    case GetWatchList(customer) =>
      sender() ! CustomerWatchList(getWatcListHandler(customer))
    case AddContentID(customerContent) =>
      addContentIDHandler(customerContent.customerID, customerContent.contentID)
      sender() ! ActionPerformed(s"${customerContent} added.")
    case AddAllContentIDs(customer) =>
      addAllContentIDsHandler(customer)
      sender() ! ActionPerformed(s"${customer} added.")
    case GetCustomer(customer) =>
      if(checkCustomer(customer.customerID)) sender() ! CustomerWatchList(getCustomerContent(customer.customerID))
    case DeleteContentID(customerID, contentID) =>
      sender() ! ActionPerformed(deleteContentHandler(customerID, contentID))
  }

  /**
    * getWatchlist Handler, checks customer exists and returns content if customer is present else empty list
    * @param customer
    * @return List[String]
    */
  def getWatcListHandler(customer: CustomerID) = {
    if(customers.nonEmpty && checkCustomer(customer.customerID)) getCustomerContent(customer.customerID)
    else List.empty
  }

  /**
    * addContentIDHandler checks customer exists and if so creates copy of customer case class and adds new list of
    * contentIDs appending existing contentIds with contentID. Then removes original value from the customers
    * and adds the new version
    * @param customerID
    * @param contentID
    */
  def addContentIDHandler(customerID: String, contentID: String) = {
    if(checkCustomer(customerID)){
      val c = getCustomer(customerID)
      val update = c.copy(customerID, (c.contentIDs ::: List(contentID)).distinct)
      customers -= customers.find(c => c.customerID == customerID).get
      customers += update
    } else {
      customers += Customer(customerID, List(contentID))
    }
  }

  /**
    * addAllContentIDsHandler checks customer exists and if so creates copy of customer case class and adds new list of
    * contentIDs appending existing contentIds with new contentIDs, removing any duplicates. Then removes original
    * value from the customers and adds the new version
    * @param customer
    */
  def addAllContentIDsHandler(customer: Customer) = {
    if(checkCustomer(customer.customerID)){
      val c = getCustomer(customer.customerID)
      val update = c.copy(customer.customerID, (c.contentIDs ::: customer.contentIDs).distinct)
      customers -= customers.find(c => c.customerID == customer.customerID).get
      customers += update
    } else {
      customers += Customer(customer.customerID, customer.contentIDs.distinct)
    }
  }

  /**
    * deleteContentHandler checks a customer exists, if so clones the existing case class in the customers listbuffer
    * filters out the contentID and then creates a new case class for the customer. The old instance is reomved before
    * the new one is added to the customers listBuffer. If a customer has had all of their content removed and empty
    * list will be returned.
    * @param customerID
    * @param contentID
    * @return String
    */
  def deleteContentHandler(customerID: String, contentID: String): String = {
    if(checkCustomer(customerID)) {
      val c = getCustomer(customerID)
      val remove = c.copy(customerID, c.contentIDs.filter(c => c != contentID))
      customers -= customers.find(c => c.customerID == customerID).get
      customers += remove
      s"ContentID: ${contentID} deleted."
    }
    else s"No $customerID content found."
  }

  /**
    * Boolean function to check if customerID is present in the customers list
    * @param customerID
    * @return Boolean
    */
  def checkCustomer(customerID: String) = {
    if(customers.map(_.customerID).contains(customerID)) true
    else false
  }

  /**
    * returns list of customer's ContentIDs
    * @param customerID
    * @return List[String]
    */
  def getCustomerContent(customerID: String) = {
    customers.find(c => c.customerID == customerID).get.contentIDs
  }

  /**
    * returns customerID if present in the customers list
    * @param customerID
    * @return String
    */
  def getCustomer(customerID: String) = {
    customers.find(c => c.customerID == customerID).get
  }

}

