package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Round

class Dealer {

  def game(playerNames: String): Unit = {
    val round = new Round
    val playerNameList = round.createPlayerNameList(playerNames)
    val playerList = round.createPlayerList(playerNameList)
    val x = round.handout(playerNameList)
  }
}
