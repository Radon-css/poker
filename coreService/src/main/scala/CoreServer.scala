package de.htwg.poker

import de.htwg.poker.controllers.Controller
import aview.TUI
import model.GameState
import aview.GUI
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.http.scaladsl.server.Route
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}


object CoreServer extends scalafx.application.JFXApp3 {
  
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
    // Starte die GUI
    gui.start()

    // Und parallel dazu TUI im Hintergrund
    Future {
      tui.gameLoop()
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("CoreServer")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: Materializer = Materializer(system)

    // Deine Route zusammenbauen (z.B. EvalRoutes + GuiRoutes + TuiRoutes zusammensetzen)
    val routes: Route = 
      new EvalRoutes().routes ~ 
      new GuiRoutes().routes ~ 
      new TuiRoutes().routes

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    bindingFuture.onComplete {
      case Success(binding) =>
        println(s"Server läuft unter http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(exception) =>
        println(s"Server konnte nicht gestartet werden: ${exception.getMessage}")
        system.terminate()
    }
  }
}
