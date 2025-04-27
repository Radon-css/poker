package de.htwg.poker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import aview.GUI
import aview.TUI
import de.htwg.poker.controllers.Controller
import model.GameState
import scala.concurrent.ExecutionContext
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object CoreServer extends scalafx.application.JFXApp3 {
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
    // Provide the implicit ExecutionContext
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    // Start GUI
    gui.start()

    // Start TUI in background
    Future {
      tui.gameLoop()
    }

    // Start HTTP server
    startHttpServer()
  }

  private def startHttpServer(): Unit = {
    implicit val system: ActorSystem = ActorSystem("CoreServer")
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
