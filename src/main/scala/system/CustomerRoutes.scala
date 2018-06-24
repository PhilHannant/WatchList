package system

import actors.Customer
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
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
      pathEnd {
        concat(
          get {
            val customer: Future[Customer] =
              (customerRegisterActor ? GetWatchList).mapTo[Customer]
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
      }

    )

  }

}
