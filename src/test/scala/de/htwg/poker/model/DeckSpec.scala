package de.htwg.poker.model

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._

class DeckSpec extends AnyWordSpec with Matchers {

  "A Deck" when {
    "created" should {
      "have 52 cards" in {
        deck.length should be(52)
      }
    }

    "shuffled" should {
      "have the same number of cards" in {
        shuffledDeck.length should be(52)
      }

      "have different order than the original deck" in {
        shuffledDeck should not be deck
      }
    }

    "removeCards" should {
      "remove n cards from the deck" in {
        val newDeck = removeCards(deck, 5)
        newDeck.length should be(47)
        newDeck shouldBe deck.drop(5)
      }
    }
  }
}
