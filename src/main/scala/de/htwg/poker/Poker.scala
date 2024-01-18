package de.htwg.poker;
import controller.Controller
import aview.TUI
import scala.io.StdIn.readLine
import model.GameState

import scala.concurrent.Await
import scala.concurrent.Future

object Poker {
  // here we need to pass in a stub of a GameState in order to initially create our controller
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0, false)
  )
  val tui = new TUI(controller)

  @main
  def run: Unit = {
    // the strange looking future and await stuff is needed so we can run our GUI and TUI concurrently
    tui.gameLoop()
  }
}
