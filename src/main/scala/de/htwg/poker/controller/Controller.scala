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

  object UpdateBoard {
    val strategy =
      if (gameState.getBoard.size == 0) addCardsToBoard(3)
      else if (gameState.getBoard.size == 3) addCardsToBoard(1)
      else if (gameState.getBoard.size == 4) addCardsToBoard(1)
      else {
        gameState = gameState.startRound
        Thread.sleep(750)
        notifyObservers
      }
  }

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
    if (shouldSkipPlayer) {
      skipPlayers
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
    if (shouldSkipPlayer) {
      skipPlayers
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
    if (shouldSkipPlayer) {
      skipPlayers
    } else if (handout_required_fold) {
      UpdateBoard.strategy
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
    if (shouldSkipPlayer) {
      skipPlayers
    } else if (handout_required) {
      UpdateBoard.strategy
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
    if (shouldSkipPlayer) {
      skipPlayers
    } else if (handout_required) {
      UpdateBoard.strategy
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
    UpdateBoard.strategy
    notifyObservers
  }

  def skipPlayers = {
    var current = gameState.getPlayerAtTurn

    if (!hasAtLeastTwoWithGreaterThanZeroBalance) {
      // handout remaining cards and start new Round
      if (gameState.getBoard.size == 0) addCardsToBoard(5)
      else if (gameState.getBoard.size == 3) addCardsToBoard(2)
      else if (gameState.getBoard.size == 4) addCardsToBoard(1)
      gameState = gameState.startRound
      notifyObservers
    } else {
      // the next player whose balance is greater than 0 is at turn
      while (gameState.getPlayers(current).balance == 0) {
        current = gameState.getNextPlayer(current)
      }
      gameState = gameState.copy(
        playerAtTurn = current
      )
      notifyObservers
    }
  }

  def addCardsToBoard(amount: Int) = {
    for (i <- 0 until amount) {
      if (i != 0) {
        Thread.sleep(500)
      }
      gameState = gameState.addCardToBoard
      notifyObservers
    }
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
    // jeder checkt durch -> bei SmallBlindSpieler austeilen
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

  }

  def hasAtLeastTwoWithGreaterThanZeroBalance: Boolean = {
    val playersWithGreaterThanZeroBalance =
      gameState.getPlayers.filter(player => player.balance != 0)
    playersWithGreaterThanZeroBalance.length >= 2
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

  def shouldSkipPlayer: Boolean = {
    return gameState.getCurrentPlayer.balance == 0
  }
}
