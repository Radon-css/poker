package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.controller.Controller
import de.htwg.poker.model.GameState

class UndoSpec extends AnyWordSpec with Matchers {
  "An UndoManager" when {
    "performing undo and redo steps" should {
      "update the game state correctly" in {

        val undoManager = new UndoManager()
        val gameState1 = new GameState(
          originalPlayers = List.empty,
          players = None,
          deck = None,
          playerAtTurn = 0,
          currentHighestBetSize = 0,
          board = List.empty,
          pot = 0,
          smallBlind = 0,
          bigBlind = 0,
          smallBlindPointer = 0,
          allInFlag = false
        )
        val gameState2 = new GameState(
          originalPlayers = List.empty,
          players = None,
          deck = None,
          playerAtTurn = 0,
          currentHighestBetSize = 0,
          board = List.empty,
          pot = 0,
          smallBlind = 0,
          bigBlind = 0,
          smallBlindPointer = 0,
          allInFlag = false
        )
        val gameState3 = new GameState(
          originalPlayers = List.empty,
          players = None,
          deck = None,
          playerAtTurn = 0,
          currentHighestBetSize = 0,
          board = List.empty,
          pot = 0,
          smallBlind = 0,
          bigBlind = 0,
          smallBlindPointer = 0,
          allInFlag = false
        )
        val controller = new Controller(gameState1)

        undoManager.doStep(gameState1)
        undoManager.doStep(gameState2)
        undoManager.doStep(gameState3)

        undoManager.undoStep(controller, gameState2)
        assert(controller.gameState == gameState2)

        undoManager.redoStep(controller)
        assert(controller.gameState == gameState3)
      }
    }
  }
}
