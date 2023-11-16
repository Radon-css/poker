package de.htwg.poker.model

package de.htwg.poker.model

package de.htwg.poker.model

package de.htwg.poker.model

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" when {
    val card1 = Card(Suit.Hearts, Rank.Ass)
    val card2 = Card(Suit.Clubs, Rank.King)
    val player = Player(card1, card2, "Alice", 1000)

    "created" should {
      "have a name" in {
        player.playername should be("Alice")
      }
      "have two cards" in {
        player.card1 should be(card1)
        player.card2 should be(card2)
      }
      "have a number of coins" in {
        player.coins should be(1000)
      }
    }

    "converted to a string" should {
      "include the player's name, number of coins, and cards" in {
        player.toString should be("Alice(1000) AH,CK    ")
      }
    }
  }
}
