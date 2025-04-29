package de.htwg.poker

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import de.htwg.poker.controllers.Receiver
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods._

class CoreRoutes {
  implicit val system: ActorSystem = ActorSystem("PokerSystem")
  implicit val mat: Materializer = Materializer(system)
  implicit val ec: ExecutionContext = system.dispatcher

  val receiver = new Receiver()(system, mat)

  def corsHandler(route: Route): Route = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(HttpOrigin("http://localhost:5173")),
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Content-Type", "Authorization", "X-Requested-With", "playerid"),
      `Access-Control-Allow-Methods`(GET, POST, OPTIONS)
    ) {
      options {
        complete("")
      } ~ route
    }
  }

  val routes: Route = corsHandler { 
    pathPrefix("core") {
      concat(
        path("get") {
          get {
            receiver.getJson // ruft deine Funktion auf
          }
        },
        path("bet" / IntNumber) { x =>
          post {
            receiver.bet(x)
          }
        },
        path("allIn") {
          post {
            receiver.allIn()
          }
        },
        path("fold") {
          post {
            receiver.fold()
          }
        },
        path("call") {
          post {
            receiver.call()
          }
        },
        path("check") {
          post {
            receiver.check()
          }
        },
        path("restartGame") {
          post {
            receiver.restartGame()
          }
        },
        path("leave") {
          post {
            receiver.leave()
          }
        },
        path("join") {
          get {
            receiver.join()
          }
        },
        path("newGame") {
          post {
            receiver.newGame()
          }
        },
        path("websocket") {
        get {
          receiver.socket()
        }
      }
      )
    }
  }
}
