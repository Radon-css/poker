package de.htwg.poker;
import controller.Controller
import aview.TUI
import scala.io.StdIn.readLine
import model.GameState
import model.shuffledDeck

@main
def run: Unit = {
  val controller = new Controller(new GameState(None, None, 0, 0))
  val tui = new TUI(controller)
  tui.gameLoop()
}
