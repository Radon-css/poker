package de.htwg.poker;
import controller.Dealer
import aview.TUI

@main
def run: Unit = {
  val dealer = new Dealer
  val tui = TUI(dealer)
  val x = tui.run
}
