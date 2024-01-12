package de.htwg.poker
package controller.ControllerComponent.ControllerBaseImpl
import model.PlayersComponent.PlayerInterface as Player
import model.GameStateComponent.GameStateInterface as GameState
import util.Observable
import util.UndoManager
import controller.ControllerComponent.ControllerInterface
import model.FileIOComponent.FileIOInterface
import model.FileIOComponent.FileIOXmlImpl.FileIO
import com.google.inject.{Guice, Inject}
import net.codingwell.scalaguice.InjectorExtensions._
import com.google.inject.name.Names

class Controller @Inject() (var gameState: GameState)
    extends Observable
    with ControllerInterface {

  val injector = Guice.createInjector(new PokerModule)

  val fileIOInterface = injector.getInstance(classOf[FileIOInterface])

  private val undoManager = new UndoManager

  def createNewGameState(): Unit = {
    gameState = injector.instance[GameState]
  }

  def getGameState(): GameState = gameState
  def createGame(
      playerNameList: List[String],
      smallBlind: String,
      bigBlind: String
  ): Boolean = {
    if (playerNameList.size < 1) {
      throw new Exception("minimum two players")
    }
    smallBlind.toInt
    bigBlind.toInt
    val smallBlindInt = smallBlind.toInt
    val bigBlindInt = bigBlind.toInt

    if (smallBlindInt > 100 || bigBlindInt > 200) {
      throw new Exception(
        "small blind must be smaller than 101 and big blind must be smaller than 201"
      )
    }

    if (bigBlindInt <= smallBlindInt) {
      throw new Exception(
        "small blind must be smaller than big blind"
      )
    }

    gameState = gameState.createGame(playerNameList, smallBlindInt, bigBlindInt)
    this.notifyObservers
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

  def save: Unit = {
    fileIOInterface.save(gameState)
  }

  def load: Unit = {
    gameState = fileIOInterface.load
    notifyObservers
  }

  def bet(amount: Int): Boolean = {
    if (gameState.getPlayers.isEmpty) {
      throw new Exception("start a game first")
    } else if (
      gameState.getPlayers(gameState.getPlayerAtTurn).balance < amount
    ) {
      throw new Exception("insufficient balance")
    } else if (gameState.getBigBlind >= amount) {
      throw new Exception("bet Size is too low")
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
      gameState = gameState.updateBoard()
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
      gameState = gameState.updateBoard()
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
      gameState = gameState.updateBoard()
    }
    this.notifyObservers
    true
  }

  override def toString(): String = gameState.toString()

  // Hilfsfunktion
  def handout_required(): Boolean = {
    // handout_preflop
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size == 0 && player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted && gameState.getPlayers.head.currentAmountBetted != 0 && (gameState.getPlayers.size > 2 && gameState.getPlayerAtTurn == 2 || gameState.getPlayers.size < 3 && gameState.getPlayerAtTurn == 0)
    ) ||
    // handout_not_preflop
    gameState.getPlayers.forall(player =>
      gameState.getBoard.size != 0 &&
        player.currentAmountBetted == gameState.getPlayers.head.currentAmountBetted
    ) && gameState.getPlayerAtTurn == 0
  }

  def handout_required_fold(): Boolean = {
    gameState.getPlayerAtTurn == gameState.getPlayers.size - 1 && handout_required()
  }

}
