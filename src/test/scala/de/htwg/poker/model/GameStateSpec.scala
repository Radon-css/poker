package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec with Matchers {
  "A GameState" when {
    "created" should {
      "have the correct initial values" in {
        val gameState =
          new GameState(List(), None, None, 0, 0, List(), 0, 10, 20, 0, false)

        gameState.getPlayers should be(List())
        gameState.getDeck should be(List())
        gameState.getPot should be(0)
        gameState.getPlayerAtTurn should be(0)
        gameState.getOriginalPlayers should be(List())
        gameState.getBoard should be(List())
      }
    }
  }
}
