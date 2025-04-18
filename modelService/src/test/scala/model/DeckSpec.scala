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
        val shuffledDeck = shuffleDeck
        shuffledDeck.length should be(52)
      }

      "have different order than the original deck" in {
        val shuffledDeck = shuffleDeck
        shuffledDeck should not be deck
      }
    }
  }
}
