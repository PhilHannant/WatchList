package system

import actors.Customer
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import messages._
import akka.pattern.ask
import akka.util.Timeout


trait CustomerRoutes extends JsonSupport {



  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[CustomerRoutes])


  def customerRegisterActor: ActorRef


  implicit lazy val timeout = Timeout(5.seconds)


  lazy val customerRoutes: Route =
    pathPrefix("customers") {
      concat(
        path(Segment) { customerID =>
          concat(
            get {
              val customer: Future[CustomerWatchList] =
                (customerRegisterActor ? GetWatchList(customerID)).mapTo[CustomerWatchList]
              complete(customer)
            },
            post {
              entity(as[Customer]) { customer =>
                val addContentID: Future[ActionPerformed] =
                  (customerRegisterActor ? AddAllContentIDs(customer)).mapTo[ActionPerformed]
                onSuccess(addContentID) { performed =>
                  log.info("Created user [{}]: {}", customer, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { customerID =>
          concat(
            get {
              val maybeUser: Future[Option[CustomerWatchList]] =
                (customerRegisterActor ? GetUser(customerID)).mapTo[Option[CustomerWatchList]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
            },
            delete {
              val userDeleted: Future[ActionPerformed] =
                (customerRegisterActor ? DeleteContentID(customerID, "srT5k")).mapTo[ActionPerformed]
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user [{}]: {}", customerID, performed.description)
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )

    }
}
