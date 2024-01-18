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
  "startRound" should {
    "initialize a new round with correct values" in {
      val gameState = new GameState(
        List(
          Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.King),
            "Player1",
            100
          ),
          Player(
            new Card(Suit.Spades, Rank.Ace),
            new Card(Suit.Spades, Rank.King),
            "Player2",
            100
          ),
          Player(
            new Card(Suit.Diamonds, Rank.Ace),
            new Card(Suit.Diamonds, Rank.King),
            "Player3",
            100
          )
        ),
        None,
        None,
        0,
        0,
        List(),
        0,
        10,
        20,
        0,
        false
      )

      val newGameState = gameState.UpdateBoard.startRound

      newGameState.getPlayers.size should be(3)
      newGameState.getDeck.size should be(46)
      newGameState.getPlayerAtTurn should be(2)
      newGameState.getHighestBetSize should be(20)
      newGameState.getBoard should be(Nil)
      newGameState.getPot should be(30)
      newGameState.getSmallBlind should be(10)
      newGameState.getBigBlind should be(20)
      newGameState.getSmallBlindPointer should be(0)

      val player1 = newGameState.getPlayers.head
      player1.card1 should not be null
      player1.card2 should not be null
      player1.playername should be("Player1")
      player1.balance should be(90)
      player1.currentAmountBetted should be(10)

      val player2 = newGameState.getPlayers(1)
      player2.card1 should not be null
      player2.card2 should not be null
      player2.playername should be("Player2")
      player2.balance should be(80)
      player2.currentAmountBetted should be(20)

      val player3 = newGameState.getPlayers(2)
      player3.card1 should not be null
      player3.card2 should not be null
      player3.playername should be("Player3")
      player3.balance should be(100)
      player3.currentAmountBetted should be(0)
    }
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
