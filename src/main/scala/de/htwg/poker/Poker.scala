package de.htwg.poker;
import controller.Controller
import aview.TUI
import scala.io.StdIn.readLine

@main
def run: Unit = {
  val controller = new Controller
  val tui = new TUI(controller)
  val input = readLine();
  tui.processInput(input)
}
