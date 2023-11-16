package de.htwg.poker.controller

import de.htwg.poker.model.GameState
import de.htwg.poker.util.Observable

import org.scalatest._
import org.scalatest.matchers.should._
import org.scalatest.wordspec.AnyWordSpec

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" when {

    "created" should {

      "have a GameState" in {
        val gameState = new GameState(None, None, 0)
        val controller = new Controller(gameState)
        controller.gameState should be(gameState)
      }

      "be an Observable" in {
        val gameState = new GameState(None, None, 0)
        val controller = new Controller(gameState)
        controller shouldBe an[Observable]
      }
    }
  }
}
