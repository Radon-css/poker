package de.htwg.poker;

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PokerSpec extends AnyWordSpec {
  "A Hand" should {
    "have valid cards when created" in {
      val hand = Hand()
      assert(Card.contains(hand.Card1))
      assert(Card.contains(hand.Card2))
    }
  }

  "A Dealer" should {
    "have a hand with valid cards" in {
      val dealer = Dealer()
      assert(Card.contains(dealer.Hand.Card1))
      assert(Card.contains(dealer.Hand.Card2))
    }
  }

  "A Player" should {
    "have a hand with valid cards" in {
      val player = Player("Julian")
      assert(Card.contains(player.Hand.Card1))
      assert(Card.contains(player.Hand.Card2))
    }
    "have the name that was assigned to him" in {
      val playerName = "Julian"
      val player = Player(playerName)
      assert(player.Name == playerName)
    }
  }
  "The main method" should {
    "print out the cards of the dealer" in {
      val dealer = Dealer()
      start should be("Der Dealer zieht: " + dealer.Hand.toString)
    }
    "print out the cards of the player" in {
      val player = Player("Julian")
      start should be("Julian zieht: " + player.Hand.toString)
    }
  }
}
