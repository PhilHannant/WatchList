package system


import scala.concurrent.Await
import scala.concurrent.duration.Duration

import actors.CustomerRegisterActor

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

/**
  * HTTP Akka Server based on the Akka http quick start server
  */
object Server extends App with CustomerRoutes {


  implicit val system: ActorSystem = ActorSystem("akkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val customerRegisterActor: ActorRef = system.actorOf(CustomerRegisterActor.props, "customerRegisterActor")

  lazy val routes: Route = customerRoutes

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)

}

