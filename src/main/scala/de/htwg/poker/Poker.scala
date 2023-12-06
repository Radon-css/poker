package de.htwg.poker;
import controller.Controller
import aview.TUI
import aview.GUI
import scala.io.StdIn.readLine
import model.GameState

object Poker {
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val tui = new TUI(controller)
  val gui = new GUI(controller)
  @main
  def run: Unit = {
    tui.gameLoop()
  }
}
