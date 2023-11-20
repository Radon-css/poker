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
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getPlayers(gameState.getPlayerAtTurn).coins < amount) {
      return (false, "insufficient balance")
    } else if (gameState.getHighestBetSize >= amount) {
      return (false, "bet Size is too low")
    }
    gameState = gameState.bet(amount)
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    (true, "")
  }

  def fold(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    }
    gameState = gameState.fold()
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    if (handout_required()) {
      println("austeilen") // austeilen
    }
    (true, "")
  }

  def call(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      return (false, "invalid call before bet")
    }
    gameState = gameState.call()
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    if (handout_required()) {
      println("austeilen") // austeilen
    }
    (true, "")
  }

  def check(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize != 0) {
      return (false, "cannot check")
    }
    gameState = gameState.check()
    this.notifyObservers
    if (handout_required()) {
      println("austeilen") // austeilen
    }
    (true, "")
  }

  override def toString(): String = gameState.toString()

  // Hilfsfunktion
  def handout_required(): Boolean = {
    gameState.getPlayers.forall(player =>
      player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
    ) && gameState.getPlayers.head.currentAmountBetted != 0 || gameState.getPlayers
      .forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
      ) && gameState.playerAtTurn == 0
  }
}
