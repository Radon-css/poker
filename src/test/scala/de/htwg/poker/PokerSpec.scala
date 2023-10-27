package de.htwg.poker;

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PokerSpec extends AnyWordSpec {
  val hand = Hand()
  val dealer = Dealer()
  val playerName = "Julian"
  val player = Player(playerName)

  "A Hand" should {
    "have valid cards when created" in {
      assert(Card.contains(hand.Card1))
      assert(Card.contains(hand.Card2))
    }
  }

  "A Dealer" should {
    "have a hand with valid cards" in {
      assert(Card.contains(dealer.Hand.Card1))
      assert(Card.contains(dealer.Hand.Card2))
    }
  }

  "A Player" should {
    "have a hand with valid cards" in {
      assert(Card.contains(player.Hand.Card1))
      assert(Card.contains(player.Hand.Card2))
    }
    "have the name that was assigned to him" in {
      assert(player.Name == playerName)
    }
  }
  "The main method" should {
    "print out the cards of the dealer and the player" in {
      start should be(
        "Der Dealer zieht: " + dealer.Hand.toString + player.Name + " zieht: " + player.Hand.toString
      )
    }
  }
}
