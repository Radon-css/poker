package de.htwg.poker

import controller.Controller
import aview.TUI
import model.GameState
import aview.GUI
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContext.Implicits.global


object Poker extends scalafx.application.JFXApp3 {
  
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
}
