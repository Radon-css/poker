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

  def bet(amount: Int): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    } else if (
      gameState.getPlayers(gameState.getPlayerAtTurn).balance < amount
    ) {
      throw new Exception("insufficient balance")
    } else if (gameState.getHighestBetSize >= amount) {
      throw new Exception("bet Size is too low")
    }
    undoManager.doStep(gameState)
    gameState = gameState.bet(amount)
    this.notifyObservers
    true
  }

  def allin(): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    }
    undoManager.doStep(gameState)
    gameState = gameState.allIn()
    this.notifyObservers
    true
  }

  def fold(): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    }
    undoManager.doStep(gameState)
    gameState = gameState.fold()

    if (handout_required_fold()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
    true
  }

  def call(): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      throw new Exception("invalid call before bet")
    } else if (
      gameState
        .getPlayers(gameState.getPlayerAtTurn)
        .currentAmountBetted == gameState.getHighestBetSize
    ) {
      throw new Exception("cannot call")
    }
    undoManager.doStep(gameState)
    gameState = gameState.call()
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
    true
  }

  def check(): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    } else if (
      gameState
        .getPlayers(gameState.getPlayerAtTurn)
        .currentAmountBetted != gameState.getHighestBetSize
    ) {
      throw new Exception("cannot check")
    }
    undoManager.doStep(gameState)
    gameState = gameState.check()
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
    }
    this.notifyObservers
    true
  }

  override def toString(): String = gameState.toString()

  // Hilfsfunktion
  def handout_required(): Boolean = {
    // handout_preflop
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size == 0 && player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted && gameState.getPlayers.head.currentAmountBetted != 0 && gameState.getPlayerAtTurn == 2
    ) ||
    // handout_not_preflop
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size != 0 &&
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
    ) && gameState.playerAtTurn == 0
  }

  def handout_required_fold(): Boolean = {
    gameState.getPlayerAtTurn == gameState.getPlayers.size - 1 && handout_required()
  }
}
