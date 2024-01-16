package de.htwg.poker
package controller
import model.Player
import model.GameState
import util.Observable
import util.UndoManager

class Controller(var gameState: GameState) extends Observable {

  private val undoManager = new UndoManager

  /* the following methods are structured in this particular way:
    first, check the action for errors and throw an exception if necessary.
    second, update the gameState.
    third, notify the observers.
    additionally, for some actions like bet, call and fold it first has to be checked wether community cards need to be revealed.
   */

  def createGame(
      playerNameList: List[String],
      smallBlind: String,
      bigBlind: String
  ): Boolean = {
    if (playerNameList.size < 2) {
      throw new Exception("Minimum two players required")
    }

    try {
      val smallBlindInt = smallBlind.toInt
      val bigBlindInt = bigBlind.toInt

      if (smallBlindInt > 100 || bigBlindInt > 200) {
        throw new Exception(
          "Small blind must be smaller than 101 and big blind must be smaller than 201"
        )
      }

      if (bigBlindInt <= smallBlindInt) {
        throw new Exception("Small blind must be smaller than big blind")
      }

      gameState =
        gameState.createGame(playerNameList, smallBlindInt, bigBlindInt)
      notifyObservers
      true
    } catch {
      case _: NumberFormatException =>
        throw new Exception("Last two inputs must be integers")
    }
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
      throw new Exception("Start a game first")
    } else if (
      gameState.getPlayers(gameState.getPlayerAtTurn).balance < amount
    ) {
      throw new Exception("Insufficient balance")
    } else if (
      gameState.getBigBlind >= amount || gameState.getHighestBetSize >= amount
    ) {
      throw new Exception("Bet size is too low")
    }

    undoManager.doStep(gameState)
    gameState = gameState.bet(amount)
    notifyObservers
    true
  }

  def allin: Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("Start a game first")
    }

    undoManager.doStep(gameState)
    gameState = gameState.allIn
    notifyObservers
    true
  }

  def fold: Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("Start a game first")
    }

    undoManager.doStep(gameState)
    gameState = gameState.fold

    // Check if handout is required and if so, call updateBoard to reveal board cards
    if (handout_required_fold) {
      gameState = gameState.UpdateBoard.strategy
    }
    notifyObservers
    true
  }

  def call: Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("Start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      throw new Exception("Invalid call before bet")
    } else if (
      gameState
        .getPlayers(gameState.getPlayerAtTurn)
        .currentAmountBetted == gameState.getHighestBetSize
    ) {
      throw new Exception("Cannot call")
    }
    undoManager.doStep(gameState)
    gameState = gameState.call

    // Check if handout is required and if so, call updateBoard to reveal board cards
    if (handout_required) {
      gameState = gameState.UpdateBoard.strategy
    }
    notifyObservers
    true
  }

  def check: Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("Start a game first")
    } else if (
      gameState
        .getPlayers(gameState.getPlayerAtTurn)
        .currentAmountBetted != gameState.getHighestBetSize
    ) {
      throw new Exception("Cannot check")
    }

    undoManager.doStep(gameState)
    gameState = gameState.check

    // Check if handout is required and if so, call updateBoard to reveal board cards
    if (handout_required) {
      gameState = gameState.UpdateBoard.strategy
    }
    notifyObservers
    true
  }

  // helper methods

  // check if handout is required
  def handout_required: Boolean = {
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size == 0 && player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
        && gameState.getPlayers.head.currentAmountBetted != 0
        && (gameState.getPlayers.size > 2 && gameState.getPlayerAtTurn == 2
          || gameState.getPlayers.size < 3 && gameState.getPlayerAtTurn == 0)
    ) ||
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size != 0 &&
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
    ) && gameState.playerAtTurn == 0
  }

  // check if handout is required after a fold
  def handout_required_fold: Boolean = {
    gameState.getPlayerAtTurn == gameState.getPlayers.size - 1 && handout_required
  }

  override def toString: String = gameState.toString()
}
