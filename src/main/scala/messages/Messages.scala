package messages

/**
  * Sealed trait containing all the possible messages that can be sent to and by the actor system
  */

import actors.{Customer, CustomerContent, CustomerID}

sealed trait Messages
case class ActionPerformed(description: String) extends Messages
case class GetWatchList(customer: CustomerID) extends Messages
case class GetHouseHoldLists(customer: Customer) extends Messages
case class AddContentID(customerContent: CustomerContent) extends Messages
case class DeleteContentID(customer: String, contentID: String) extends Messages
case class CustomerWatchList(contentIDs: List[String]) extends Messages
case class HouseholdWatchLists(contentIDs: List[List[String]]) extends Messages
case class AddAllContentIDs(customer: Customer) extends Messages



