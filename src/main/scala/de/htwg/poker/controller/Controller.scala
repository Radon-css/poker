package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Dealer
import model.GameState
import util.Observable

class Controller extends Observable{

  def startGame(playerNameList: List[String]) = {
    val gameState = Dealer.createGame(playerNameList)
    this.notifyObservers
  }
  override def toString(): String = GameState.toString()
}
