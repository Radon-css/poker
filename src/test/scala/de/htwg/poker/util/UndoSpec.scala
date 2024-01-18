package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
/*
import de.htwg.poker.controller.Controller
import de.htwg.poker.model.GameState

class UndoSpec extends AnyWordSpec with Matchers {
  "An UndoManager" when {
    "performing undo and redo steps" should {
      "correctly update the game state" in {
        val controller = mock(classOf[Controller])
        val undoManager = new UndoManager()
        val gameState1 = mock(classOf[GameState])
        val gameState2 = mock(classOf[GameState])
        val gameState3 = mock(classOf[GameState])

        undoManager.doStep(gameState1)
        undoManager.doStep(gameState2)
        undoManager.doStep(gameState3)

        undoManager.undoStep(controller, gameState2)
        verify(controller).gameState_=(gameState2)

        undoManager.redoStep(controller)
        verify(controller).gameState_=(gameState3)
      }
    }
  }
}
 */
