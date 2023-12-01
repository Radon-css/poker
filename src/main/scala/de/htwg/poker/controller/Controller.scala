package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Dealer
import model.GameState
import util.Observable
import de.htwg.poker.model.boardState

class Controller(var gameState: GameState) extends Observable {
  val controller = this

  def startGame(playerNameList: List[String]) = {
    gameState = Dealer.createGame(playerNameList)
    this.notifyObservers
  }

  abstract class Move(){

    def execute(): (Boolean, String) = {
      val (isValid, errorMessage) = doChecks()
      if(!isValid) {
        return (false, errorMessage)
      }
      if(checkBlind()) {
        return (true, "")
      }

      updateGameState()
      gameState.getPlayers.foreach(player => println(player.currentAmountBetted))

      if (handout_required()) {
      gameState = gameState.updateBoard.strategy
      boardState.continue()
    }
      controller.notifyObservers
      (true,"")
    }

    def checkBlind(): Boolean = {
      if(boardState.state == "preflop" && gameState.playerAtTurn == 0) {
        gameState = gameState.bet(gameState.getSmallBlind)
        return true
      } 
      else if(boardState.state == "preflop" && gameState.playerAtTurn == 1) {
        gameState = gameState.bet(gameState.getBigBlind)
        return true
      }
      false
    }

    def doChecks(): (Boolean, String)

    def updateGameState(): Unit
  }

  class bet(amount: Int) extends Move{
    override def doChecks() = {
      if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getPlayers(gameState.getPlayerAtTurn).coins < amount) {
      return (false, "insufficient balance")
    } else if (gameState.getHighestBetSize >= amount) {
      return (false, "bet Size is too low")
    }
      (true,"")
    }

    override def updateGameState() = {
      gameState = gameState.bet(amount)
    }
  }

  class fold() extends Move{
    override def doChecks() = {
      if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    }
      (true,"")
    }

    override def updateGameState() = {
      gameState = gameState.fold()
    }
  }

  class call() extends Move{
    override def doChecks() = {
      if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      return (false, "invalid call before bet")
    }
      (true,"")
    }

    override def updateGameState() = {
      gameState = gameState.call()
    }
  }

  class check() extends Move{
    override def doChecks() = {
      if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize != 0) {
      return (false, "cannot check")
    }
      (true,"")
    }

    override def updateGameState() = {
      gameState = gameState.check()
    }
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
