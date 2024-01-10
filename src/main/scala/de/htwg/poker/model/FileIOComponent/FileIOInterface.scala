package de.htwg.poker.model.FileIOComponent

import de.htwg.poker.model.GameStateComponent.GameStateInterface

trait FileIOInterface {
  def load: GameStateInterface
  def save(gameState: GameStateInterface): Unit
}
