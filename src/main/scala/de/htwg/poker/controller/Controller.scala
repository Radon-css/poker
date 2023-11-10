package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Dealer
import model.GameState
import util.Observable

class Controller(var gameState: GameState) extends Observable {

  def startGame(playerNameList: List[String]) = {
    gameState = Dealer.createGame(playerNameList)
    this.notifyObservers
  }

  def bet(amount: Int): Unit = {
    gameState = gameState.bet(amount)
    this.notifyObservers
  }

  override def toString(): String = gameState.toString()
}
