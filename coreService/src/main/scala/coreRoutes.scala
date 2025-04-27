package de.htwg.poker

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.controllers.Receiver

object CoreRoutes {

  val routes: Route = pathPrefix("core") {
    concat(
      path("get") {
        get {
          complete(Receiver.getJson()) // ruft deine Funktion auf
        }
      },
      path("bet" / IntNumber) { x =>
        post {
          complete(Receiver.bet(x))
        }
      },
      path("allIn") {
        post {
          complete(Receiver.allIn())
        }
      },
      path("fold") {
        post {
          complete(Receiver.fold())
        }
      },
      path("call") {
        post {
          complete(Receiver.call())
        }
      },
      path("check") {
        post {
          complete(Receiver.check())
        }
      },
      path("restartGame") {
        post {
          complete(Receiver.restartGame())
        }
      },
      path("leave") {
        post {
          complete(Receiver.leave())
        }
      },
      path("join") {
        get {
          complete(Receiver.join())
        }
      },
      path("newGame") {
        post {
          complete(Receiver.newGame())
        }
      }
      // WebSocket können wir auch bauen, wenn du willst
    )
  }
}
