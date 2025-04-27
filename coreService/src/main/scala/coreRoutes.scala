package de.htwg.poker

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import de.htwg.poker.controllers.Receiver
import scala.concurrent.ExecutionContext

class CoreRoutes {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "PokerSystem")
  implicit val mat: Materializer = Materializer(system)
  implicit val ec: ExecutionContext = system.executionContext

  val receiver = new Receiver()(system, mat)

  val routes: Route = pathPrefix("core") {
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
      }
      // WebSocket können wir auch bauen, wenn du willst
    )
  }
}
