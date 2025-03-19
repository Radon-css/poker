package de.htwg.poker
package controller
import model.Player
import model.GameState
import model.UpdateBoard
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
    }
    undoManager.doStep(gameState)

    // distinguish allIn and normal bet
    if (amount == gameState.getPlayers(gameState.getPlayerAtTurn).balance) {
      gameState = gameState.allIn

    } else {
      if (gameState.getPlayers(gameState.getPlayerAtTurn).balance < amount) {
        throw new Exception("Insufficient balance")
      }
      if (
        gameState.getBigBlind >= amount || gameState.getHighestBetSize >= amount
      ) {
        throw new Exception("Bet size is too low")
      }
      gameState = gameState.bet(amount)
    }
    notifyObservers
    true
  }

  def allIn(): Boolean = {
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
    if (handout_required) {
      // set all players checkedThisRound attribute to false
      gameState = gameState.copy(
        players = Some(
          gameState.getPlayers.map(player =>
            player.copy(checkedThisRound = false)
          )
        )
      )
      gameState = UpdateBoard.strategy(gameState)
    }
    if (playerWonBeforeShowdown) {
      gameState = UpdateBoard.startRound(gameState)
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
      // set all players checkedThisRound attribute to false
      // function as parameter
      gameState = gameState.copy(
        players = Some(
          gameState.getPlayers.map(player =>
            player.copy(checkedThisRound = false)
          )
        )
      )
      gameState = UpdateBoard.strategy(gameState)
    }
    if (playerWonBeforeShowdown) {
      gameState = UpdateBoard.startRound(gameState)
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
      // set all players checkedThisRound attribute to false
      gameState = gameState.copy(
        players = Some(
          gameState.getPlayers.map(player =>
            player.copy(checkedThisRound = false)
          )
        )
      )
      gameState = UpdateBoard.strategy(gameState)
    }
    if (playerWonBeforeShowdown) {
      gameState = UpdateBoard.startRound(gameState)
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
    gameState = UpdateBoard.startRound(gameState)
    notifyObservers
  }

  // helper methods

  // check if handout is required
  def handout_required: Boolean = {
    // preflop handout
    // case jeder called big Blind -> bigBlindPlayer hat recht zu erhöhen
    (gameState.getBoard.size == 0 &&
      gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getBigBlind || player.folded
      ))
    && gameState.getPlayerAtTurn == gameState.getNextPlayer(
      gameState.getNextPlayer(gameState.getSmallBlindPointer)
    )
    // case es wird erhöht -> bigBlindPlayer hat kein recht zu erhöhen
    || (gameState.getBoard.size == 0 && gameState.getHighestBetSize > gameState.getBigBlind
      && gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted || player.folded
      ))
    // postflop handout
    // jeder checkt durch
    || (gameState.getBoard.size != 0
      && gameState.getHighestBetSize == 0
      && gameState.getPlayers.forall(player =>
        player.checkedThisRound || player.folded
      ))
    // es wird erhöht -> dann austeilen, wenn jeder gleich viel gebetted hat
    || (gameState.getBoard.size != 0
      && gameState.getHighestBetSize != 0
      && gameState.getPlayers.forall(player =>
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted || player.folded
      ))
  }

  def playerWonBeforeShowdown =
    gameState.getPlayers.filter(player => !player.folded).length == 1

  override def toString: String = gameState.toString()

  def checkDuplicateName(liste: List[String]): Boolean = {
    val gruppiert = liste.groupBy(identity).mapValues(_.size)
    gruppiert.values.exists(_ > 1)
  }
}
