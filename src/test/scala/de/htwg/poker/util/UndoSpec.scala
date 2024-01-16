package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.controller.Controller
import de.htwg.poker.model.{GameState, Card, Suit, Rank, Player}

class UndoManagerSpec extends AnyWordSpec with Matchers {
  "An UndoManager" when {
    val card1 = Card(Suit.Hearts, Rank.Ace)
    val card2 = Card(Suit.Diamonds, Rank.King)
    val playername = "John Doe"
    val balance = 1000
    val currentAmountBetted = 0
    val player = Player(card1, card2, playername, balance, currentAmountBetted)
    val gameState =
      GameState(List(player), Some(List(player)), Some(List(card1, card2)))
    val controller = new Controller(gameState)
    val undoManager = new UndoManager()

    "doStep" should {
      "add the current game state to the undo stack" in {
        undoManager.doStep(gameState)
        undoManager.undoStack.head should be(gameState)
      }
    }

    "undoStep" should {
      "restore the previous game state and move it to the redo stack" in {
        val previousGameState = GameState(List.empty, None, None)
        undoManager.doStep(gameState)
        undoManager.undoStep(controller, previousGameState)
        controller.gameState should be(previousGameState)
        undoManager.redoStack.head should be(gameState)
      }

      "not do anything if the undo stack is empty" in {
        val previousGameState = GameState(List.empty, None, None)
        undoManager.undoStep(controller, previousGameState)
        controller.gameState should be(gameState)
        undoManager.redoStack should be(Nil)
      }
    }

    "redoStep" should {
      "restore the next game state from the redo stack and move it to the undo stack" in {
        val nextGameState = GameState(List.empty, None, None)
        undoManager.redoStack = nextGameState :: undoManager.redoStack
        undoManager.redoStep(controller)
        controller.gameState should be(nextGameState)
        undoManager.undoStack.head should be(gameState)
      }

      "not do anything if the redo stack is empty" in {
        undoManager.redoStep(controller)
        controller.gameState should be(gameState)
        undoManager.undoStack.head should be(gameState)
      }
    }
  }
}
