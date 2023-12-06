package de.htwg.poker;
import controller.Controller
import aview.TUI
import scala.io.StdIn.readLine
import model.GameState
import aview.ScalaFXHelloWorld

import scala.concurrent.Await
import scala.concurrent.Future

object Poker {
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val tui = new TUI(controller)
  val gui = new ScalaFXHelloWorld(controller)

  @main
  def run: Unit = {
    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.gameLoop()
    Await.ready(f, scala.concurrent.duration.Duration.Inf)
  }
}
