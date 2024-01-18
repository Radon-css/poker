package de.htwg.poker.model

import de._
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
  "Update Board" when {
    /*
    "called" should {
      "update the board" in {
        val gameState =
          new GameState(List(), None, None, 0, 0, List(), 0, 10, 20, 0, false)
        val board = List(Card(Suit.Spades, Rank.Ace))
        val newGameState = gameState.UpdateBoard(board)
        newGameState.getBoard should be(board)
      }
    }
     */
  }
  "getNextPlayer" should {
    "return the next player index" in {
      val gameState =
        new GameState(List(), None, None, 0, 0, List(), 0, 10, 20, 0, false)
      gameState.getNextPlayer should be(1)
    }
    "return 1 if the current player is the last player" in {
      val gameState =
        new GameState(List(), None, None, 0, 1, List(), 0, 10, 20, 0, false)
      gameState.getNextPlayer should be(1)
    }
  }
  "getNextPlayerWhenFold" should {
    "return the next player index" in {
      val gameState =
        new GameState(List(), None, None, 0, 0, List(), 0, 10, 20, 0, false)
      gameState.getNextPlayerWhenFold should be(0)
    }
    "return the current player index if the current player is the last player" in {
      val gameState =
        new GameState(List(), None, None, 0, 1, List(), 0, 10, 20, 0, false)
      gameState.getNextPlayerWhenFold should be(0)
    }
  }
  "getPreviousPlayer" should {
    "return the previous player index" in {
      val gameState =
        new GameState(List(), None, None, 1, 1, List(), 0, 10, 20, 0, false)
      gameState.getPreviousPlayer should be(0)
    }
  }
  "getCurrentHand" should {
    /*
    "return the evaluated hand" in {
      val gameState =
        new GameState(List(), None, None, 0, 0, List(), 0, 10, 20, 0, false)
      val evaluator = new Evaluator()
      val expectedHand = evaluator.evaluate(
        List(
          gameState.getPlayers(gameState.getPlayerAtTurn).card1,
          gameState.getPlayers(gameState.getPlayerAtTurn).card2
        ),
        gameState.getBoard
      )
      gameState.getCurrentHand should be(expectedHand)
    }
  }
     */
  }
}
