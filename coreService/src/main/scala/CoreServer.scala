package de.htwg.poker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import aview.TUI
import de.htwg.poker.controllers.Controller
import model.GameState
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object CoreServer {
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val tui = new TUI(controller)

  // <<< Hier global!
  val system: ActorSystem = ActorSystem("CoreServer")

  def main(args: Array[String]): Unit = {
    start()
  }

  def start(): Unit = {
    // Provide the implicit ExecutionContext
    implicit val ec: ExecutionContext = system.dispatcher

    // Start TUI in eigenem Thread
    new Thread(() => tui.gameLoop()).start()

    // Start HTTP server
    startHttpServer()

    // <<< Jetzt funktioniert es, weil system sichtbar ist
    Await.result(system.whenTerminated, Duration.Inf)
  }

  private def startHttpServer(): Unit = {
    implicit val system: ActorSystem = CoreServer.system // <- Hier wichtig!!
    implicit val executionContext: ExecutionContext = system.dispatcher
    implicit val materializer: Materializer = Materializer(system)

    val routes = new CoreRoutes().routes

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    bindingFuture.onComplete {
      case Success(binding) =>
        println(s"Server running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(exception) =>
        println(s"Server failed to start: ${exception.getMessage}")
        system.terminate()
    }
}

}
