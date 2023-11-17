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

  def bet(amount: Int): (Boolean, String) = {
    if(gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getPlayers(gameState.getPlayerAtTurn).coins < amount){
      return (false, "insufficient balance")
    }
    gameState = gameState.bet(amount)
    this.notifyObservers
    (true,"")
  }

  def fold(): (Boolean, String) = {
    if(gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    }
    gameState = gameState.fold()
    this.notifyObservers
    (true,"")
  }

  def call(): (Boolean, String) = {
    if(gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getBetSize == 0) {
      return (false, "invalid call before bet")
    }
    gameState = gameState.call()
    this.notifyObservers
    (true,"")
  }

  override def toString(): String = gameState.toString()
}
