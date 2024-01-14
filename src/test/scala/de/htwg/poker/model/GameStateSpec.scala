package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec with Matchers {
  "A GameState" when {
    val card1 = Card(Suit.Hearts, Rank.Ace)
    val card2 = Card(Suit.Diamonds, Rank.King)
    val player1 = Player(card1, card2, "John Doe", 1000, 0)
    val player2 = Player(card1, card2, "Jane Smith", 2000, 0)
    val players = List(player1, player2)
    val deck = List(card1, card2)
    val gameState = GameState(players, Some(players), Some(deck))

    "created" should {
      "have the correct players" in {
        gameState.getPlayers should be(players)
      }

      "have the correct deck" in {
        gameState.getDeck should be(deck)
      }

      "have the correct player at turn" in {
        gameState.getPlayerAtTurn should be(0)
      }

      "have the correct highest bet size" in {
        gameState.getHighestBetSize should be(0)
      }

      "have the correct board" in {
        gameState.getBoard should be(Nil)
      }

      "have the correct small blind" in {
        gameState.getSmallBlind should be(10)
      }

      "have the correct big blind" in {
        gameState.getBigBlind should be(20)
      }

      "have the correct pot" in {
        gameState.getPot should be(30)
      }

      "have the correct original players" in {
        gameState.getOriginalPlayers should be(players)
      }

      "have the correct small blind pointer" in {
        gameState.getSmallBlindPointer should be(0)
      }
    }

    "bet" should {
      "update the player's balance and current amount betted" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        val updatedPlayer = updatedGameState.getPlayers(0)
        updatedPlayer.balance should be(player1.balance - amount)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + amount
        )
      }

      "update the pot and highest bet size" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.getPot should be(gameState.getPot + amount)
        updatedGameState.getHighestBetSize should be(amount)
      }

      "update the player at turn" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

    // Add more test cases for other methods in GameState
  }
}
