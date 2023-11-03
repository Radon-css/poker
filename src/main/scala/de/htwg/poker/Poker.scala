package de.htwg.poker;
import controller.Dealer
import aview.TUI

@main
def run: Unit = {
  val dealer = new Dealer
  val tui = TUI(dealer)
  tui.run
  val playerList = dealer.handout(playerNames)
  dealer.flop(playerList.length)
}
