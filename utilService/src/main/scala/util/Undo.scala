package de.htwg.poker.util
import de.htwg.poker.controller.Controller
import de.htwg.poker.model.GameState

class UndoManager {
  private var undoStack: List[GameState] = Nil
  private var redoStack: List[GameState] = Nil
  def doStep(gameState: GameState) = {
    undoStack = gameState :: undoStack
  }
  def undoStep(controller: Controller, previousGameState: GameState) = {
    undoStack match {
      case Nil =>
      case head :: stack => {
        controller.gameState = undoStack.head
        undoStack = stack
        redoStack = previousGameState :: redoStack
      }
    }
  }
  def redoStep(controller: Controller) = {
    redoStack match {
      case Nil =>
      case head :: stack => {
        controller.gameState = redoStack.head
        redoStack = stack
        undoStack = head :: undoStack
      }
    }
  }
}
