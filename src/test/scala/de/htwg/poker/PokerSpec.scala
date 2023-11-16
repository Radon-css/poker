package de.htwg.poker;

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PokerSpec extends AnyWordSpec {
  val playerHand = Hand()
  val dealerHand = Hand()
  val dealer = Dealer(dealerHand)
  val playerName = "Julian"
  val player = Player(playerHand, playerName)

  "A Hand" should {
    "have valid cards when created" in {
      assert(Card.contains(playerHand.Card1))
      assert(Card.contains(playerHand.Card2))

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
}
