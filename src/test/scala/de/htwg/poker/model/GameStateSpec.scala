package de.htwg.poker.model

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._

class GameStateSpec extends AnyWordSpec with Matchers {

  "A GameState" when {
    "created" should {
      "have no players and no deck" in {
        val gameState = GameState(None, None)
        gameState.getPlayers should be(List.empty[Player])
        gameState.getDeck should be(List.empty[Card])
      }
    }

    "betting" should {
      "update the player's coins and move to the next player" in {
        val player1 = Player(
          Card(Suit.Spades, Rank.Ace),
          Card(Suit.Hearts, Rank.King),
          "Alice",
          100
        )
        val player2 = Player(
          Card(Suit.Diamonds, Rank.Queen),
          Card(Suit.Clubs, Rank.Jack),
          "Bob",
          100
        )
        val gameState =
          GameState(Some(List(player1, player2)), Some(shuffledDeck))
        val updatedGameState = gameState.bet(10)
        updatedGameState.getPlayers(0).coins should be(90)
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }
  }
}
