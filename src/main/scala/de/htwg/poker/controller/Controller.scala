package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player
import model.Dealer
import model.GameState
import util.Observable
import de.htwg.poker.model.boardState

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
    if(boardState.state == "preflop" && gameState.playerAtTurn == 0) {
      gameState = gameState.bet(gameState.getSmallBlind)
    } else if(boardState.state == "preflop" && gameState.playerAtTurn == 1) {
      gameState = gameState.bet(gameState.getBigBlind) }
      else {
        gameState = gameState.bet(amount)
      }
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    (true, "")
  }

  def fold(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    }
    if(boardState.state == "preflop" && gameState.playerAtTurn == 0) {
      gameState = gameState.bet(gameState.getSmallBlind)
    } else if(boardState.state == "preflop" && gameState.playerAtTurn == 1) {
      gameState = gameState.bet(gameState.getBigBlind) 
    } else { gameState = gameState.fold()
      }
    
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
      boardState.continue()
    }
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    (true, "")
  }

  def call(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize == 0) {
      return (false, "invalid call before bet")
    }
     if(boardState.state == "preflop" && gameState.playerAtTurn == 0) {
      gameState = gameState.bet(gameState.getSmallBlind)
    } else if(boardState.state == "preflop" && gameState.playerAtTurn == 1) {
      gameState = gameState.bet(gameState.getBigBlind) 
    } else { gameState = gameState.call()
      }
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
      boardState.continue()
    }
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
    (true, "")
  }

  def check(): (Boolean, String) = {
    if (gameState.getPlayers.isEmpty) {
      return (false, "start a game first")
    } else if (gameState.getHighestBetSize != 0) {
      return (false, "cannot check")
    }
     if(boardState.state == "preflop" && gameState.playerAtTurn == 0) {
      gameState = gameState.bet(gameState.getSmallBlind)
    } else if(boardState.state == "preflop" && gameState.playerAtTurn == 1) {
      gameState = gameState.bet(gameState.getBigBlind) 
    } else { gameState = gameState.check()
      }
    if (handout_required()) {
      gameState = gameState.updateBoard.strategy
      boardState.continue()
    }
    this.notifyObservers
    gameState.getPlayers.foreach(player => println(player.currentAmountBetted))
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
