package de.htwg.poker.controller.ControllerComponent.ControllerMockImpl

import de.htwg.poker.controller.ControllerComponent.ControllerInterface
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState

trait ControllerMockInterface extends ControllerInterface {

  def getGameState(): GameState =
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)

  def mockCreateGame(
      playerNameList: List[String],
      smallBlind: String,
      bigBlind: String
  ): Boolean

  def mockUndo(): Unit

  def mockRedo(): Unit

  def mockBet(amount: Int): Boolean

  def mockAllIn(): Boolean

  def mockFold(): Boolean

  def mockCall(): Boolean

  def mockCheck(): Boolean

  def mockHandoutRequired(): Boolean

  def mockHandoutRequiredFold(): Boolean
}
