package system

import actors.{Customer, CustomerContent, CustomerID}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete

import scala.concurrent.Future
import messages._
import akka.pattern.ask
import akka.util.Timeout


/**
  * CustomerRoutes trait holding all the route definitions
  */

trait CustomerRoutes extends JsonSupport {


  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[CustomerRoutes])


  def customerRegisterActor: ActorRef


  implicit lazy val timeout = Timeout(5.seconds)


  lazy val customerRoutes: Route =
    pathPrefix("customers") {
      concat(
        pathEnd {
          concat(
            get {
              entity(as[CustomerID]) { c =>
                val customer: Future[CustomerWatchList] =
                  (customerRegisterActor ? GetWatchList(c)).mapTo[CustomerWatchList]
                onSuccess(customer) { performed =>
                  complete((StatusCodes.OK, performed))
                }
              }
            },
            post {
              entity(as[Customer]) { customer =>
                val addContentID: Future[ActionPerformed] =
                  (customerRegisterActor ? AddAllContentIDs(customer)).mapTo[ActionPerformed]
                onSuccess(addContentID) { performed =>
                  log.info("Created customer content [{}]: {}", customer, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        pathEnd {
          concat(
            get {
              entity(as[CustomerID]) { c =>
                val customer: Future[CustomerWatchList] =
                  (customerRegisterActor ? GetWatchList(c)).mapTo[CustomerWatchList]
                onSuccess(customer) { performed =>
                  complete((StatusCodes.OK, performed))
                }
              }
            },
            post {
              entity(as[CustomerContent]) { customerContent =>
                val addContentID: Future[ActionPerformed] =
                  (customerRegisterActor ? AddContentID(customerContent)).mapTo[ActionPerformed]
                onSuccess(addContentID) { performed =>
                  log.info("Created customer content [{}]: {}", customerContent, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        pathEnd {
          concat(
            get {
              entity(as[CustomerID]) { c =>
                val maybeUser: Future[Option[CustomerWatchList]] =
                  (customerRegisterActor ? GetCustomer(c)).mapTo[Option[CustomerWatchList]]
                rejectEmptyResponse {
                  complete(maybeUser)
                }
              }
            },
            delete {
              entity(as[CustomerContent]) { cc =>
                val contentDeleted: Future[ActionPerformed] =
                  (customerRegisterActor ? DeleteContentID(cc.customerID, cc.contentID)).mapTo[ActionPerformed]
                onSuccess(contentDeleted) { performed =>
                  log.info("Deleted content [{}]: {}", cc, performed.description)
                  complete((StatusCodes.OK, performed))
                }
              }
            }
          )
        }
      )
    }
}
