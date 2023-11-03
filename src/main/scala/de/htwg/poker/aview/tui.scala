package de.htwg.poker
package aview
import controller.Dealer
import util.Observer
import scala.io.StdIn.readLine

class TUI(dealer: Dealer) extends Observer {
  def readInput: String = {
    readLine("Spielernamen eingeben: ")
  }
  def run = {
    val playerNames = readInput
    val playerList = dealer.game(playerNames)
  }
}
