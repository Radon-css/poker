package de.htwg.poker.controller.ControllerComponent

import de.htwg.poker.model.GameStateComponent.GameStateInterface as GameState
import de.htwg.poker.util.Observer
import de.htwg.poker.util.Observable

trait ControllerInterface extends Observable {

  def getGameState(): GameState

  def createGame(
      playerNameList: List[String],
      smallBlind: String,
      bigBlind: String
  ): Boolean

  def undo: Unit

  def redo: Unit

  def bet(amount: Int): Boolean

  def allin(): Boolean

  def fold(): Boolean

  def call(): Boolean

  def check(): Boolean

  def handout_required(): Boolean

  def handout_required_fold(): Boolean
}
