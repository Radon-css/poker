package de.htwg.poker
package controller
import model.Player
import model.GameState
import util.Observable
import util.UndoManager

class Controller(var gameState: GameState) extends Observable {

  private val undoManager = new UndoManager

  /* the following methods are structured in this particular way:
    first, check the user input for unallowed inputs and throw an exception if necessary.
    second, update the gameState.
    third, notify the observers.
    additionally, for some actions like bet, call and fold it first has to be checked wether new community cards need to be revealed.
   */

  def createGame(
      playerNameList: List[String],
      smallBlind: String,
      bigBlind: String
  ): Boolean = {
    if (playerNameList.size < 2) {
      throw new Exception("Minimum two players required")
    }

    if (checkDuplicateName(playerNameList)) {
      throw new Exception("All player names must be unique")
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
        gameState.createGame(playerNameList, smallBlindInt, bigBlindInt, 0)
      notifyObservers
      true
    } catch {
      case _: NumberFormatException =>
        throw new Exception("Last two inputs must be integers")
    }
  }

  def bet(amount: Int): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("Start a game first")
    } else if (
      gameState.getPlayers(gameState.getPlayerAtTurn).balance < amount
    ) {
      throw new Exception("Insufficient balance")
    } else if (
      amount == gameState
        .getPlayers(gameState.getPlayerAtTurn)
        .balance && gameState.getHighestBetSize >= amount
    ) {
      gameState = gameState.allIn
    } else if (
      gameState.getBigBlind >= amount || gameState.getHighestBetSize >= amount
    ) {
      throw new Exception("Bet size is too low")
    }

    undoManager.doStep(gameState)
    if (amount == gameState.getPlayers(gameState.getPlayerAtTurn).balance) {
      gameState = gameState.allIn
    } else {
      gameState = gameState.bet(amount)
    }
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
//test
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

  def undo: Unit = {
    undoManager.undoStep(this, this.gameState)
    notifyObservers
  }

  def redo: Unit = {
    undoManager.redoStep(this)
    notifyObservers
  }

  def restartGame: Unit = {
    gameState = gameState.restartGame
    notifyObservers
  }

  // helper methods

  // check if handout is required
  def handout_required: Boolean = {
    // preflop handout
    // case jeder called big Blind -> bigBlindPlayer hat recht zu erhöhen
    (gameState.getBoard.size == 0 &&
      gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getBigBlind
      ))
    && gameState.getPlayerAtTurn == gameState.getNextPlayer(
      gameState.getNextPlayer(gameState.getSmallBlindPointer)
    )
    // case es wird erhöht -> bigBlindPlayer hat kein recht zu erhöhen
    || (gameState.getBoard.size == 0 && gameState.getHighestBetSize > gameState.getBigBlind
      && gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
      ))
    // postflop handout
    // jeder checkt durch -> bei Spieler 0 austeilen
    || (gameState.getBoard.size != 0
      && gameState.getHighestBetSize == 0
      && gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
      )
      && gameState.playerAtTurn == gameState.getSmallBlindPointer)
    // es wird erhöht -> dann austeilen, wenn jeder gleich viel gebetted hat
    || (gameState.getBoard.size != 0
      && gameState.getHighestBetSize != 0
      && gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
      ))

    /* gameState.getPlayers.forall(player =>
      gameState.getBoard.size == 0 && player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
        && gameState.getPlayers.head.currentAmountBetted != 0
        && (gameState.getPlayers.size > 2 && gameState.getPlayerAtTurn == 2
          || gameState.getPlayers.size < 3 && gameState.getPlayerAtTurn == 0)
    ) ||
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size != 0 &&
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
    ) && gameState.playerAtTurn == 0
  }*/
  }
  // check if handout is required after a fold
  def handout_required_fold: Boolean = {
    gameState.getPlayerAtTurn == gameState.getPlayers.size - 1 && handout_required
  }

  override def toString: String = gameState.toString()

  def checkDuplicateName(liste: List[String]): Boolean = {
    val gruppiert = liste.groupBy(identity).mapValues(_.size)
    gruppiert.values.exists(_ > 1)
  }
}
