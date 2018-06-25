package messages

import actors.{Customer, CustomerID}

sealed trait Messages
case class ActionPerformed(description: String) extends Messages
case class GetWatchList(customer: CustomerID) extends Messages
case class AddContentID(customer: String, contentID: String) extends Messages
case class GetUser(name: String) extends Messages
case class DeleteContentID(customer: String, contentID: String) extends Messages
case class CustomerWatchList(contentIDs: List[String]) extends Messages
case class AddAllContentIDs(customer: Customer) extends Messages



