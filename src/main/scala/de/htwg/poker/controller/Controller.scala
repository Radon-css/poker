package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Dealer
import model.GameState
import util.Observable
import util.UndoManager

class Controller(var gameState: GameState) extends Observable {

  private val undoManager = new UndoManager

  def startGame(playerNameList: List[String]) = {
    gameState = Dealer.createGame(playerNameList)
    this.notifyObservers
  }

  def undo: Unit = {
    undoManager.undoStep(this, this.gameState)
    notifyObservers
  }

  def redo: Unit = {
    undoManager.redoStep(this)
    notifyObservers
  }

  def bet(amount: Int): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getPlayers(gameState.getPlayerAtTurn).coins < amount) {
      return (false, "insufficient balance")
    } else if (gameState.getHighestBetSize >= amount) {
      return (false, "bet Size is too low")
    }
    undoManager.doStep(gameState)
    gameState = gameState.bet(amount)
    this.notifyObservers
    (true, "")
  }

  def fold(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    }
    undoManager.doStep(gameState)
    gameState = gameState.fold()

    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
    (true, "")
  }

  def call(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      return (false, "invalid call before bet")
    }
    undoManager.doStep(gameState)
    gameState = gameState.call()
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
    (true, "")
  }

  def check(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize != 0) {
      return (false, "cannot check")
    }
    undoManager.doStep(gameState)
    gameState = gameState.check()
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
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
