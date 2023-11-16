package de.htwg.poker.controller

class ControllerSpec {}
package de.htwg.poker.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" when {

    "created" should {

      "have a GameState" in {
        val gameState = new GameState
        val controller = new Controller(gameState)
        controller.gameState should be(gameState)
      }

      "be an Observable" in {
        val gameState = new GameState
        val controller = new Controller(gameState)
        controller shouldBe an[Observable]
      }
    }
  }
}
