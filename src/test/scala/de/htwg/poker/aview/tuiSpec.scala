package de.htwg.poker.aview
import de.htwg.poker.controller.Controller

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._
import de.htwg.poker.model.GameState
import de.htwg.poker.model.shuffledDeck
import de.htwg.poker.model.Player

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" when {
    "processInput" should {
      "return true for valid start command" in {
        val controller = new Controller(new GameState(None, None, 0))
        val tui = new TUI(controller)
        tui.processInput("start 2") should be(true)
      }
      "return false for invalid command" in {
        val controller = new Controller(new GameState(None, None, 0))
        val tui = new TUI(controller)
        tui.processInput("invalid") should be(false)
      }
    }
  }
}
