package messages

sealed trait Messages

    case class ActionPerformed(description: String)
    case class GetWatchList(customer: String)
    case class AddContentID(customer: String, contentID: String)
    case class GetUser(name: String)
    case class DeleteContentID(customer: String, contentID: String)


