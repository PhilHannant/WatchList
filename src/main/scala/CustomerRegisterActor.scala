import akka.actor.{ Actor, ActorLogging }
import messages._


class CustomerRegisterActor extends Actor with ActorLogging {


  var customers = scala.collection.mutable.Map[String, List[String]]()

  def receive: Receive = {
    case GetWatchList(customer) =>
      sender() ! customers.get(customer)
    case AddContentID(customer, contentID) =>
      customers.put(customer, customers.get(customer).get :+ contentID)
      sender() ! ActionPerformed(s"User \${user.name} created.")
    case GetUser(customer) =>
      sender() ! customers.find(_ == customer)
    case DeleteContentID(customer, contentID) =>
      customers.put(customer, customers.get(customer).filter(cID => cID != contentID).get)
      sender() ! ActionPerformed(s"User \${name} deleted.")
  }



}

