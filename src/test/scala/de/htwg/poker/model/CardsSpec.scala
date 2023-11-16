package de.htwg.poker.model

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._

class CardsSpec extends AnyWordSpec with Matchers {

  "A Rank" when {
    "converted to a string" should {
      "return the correct string representation" in {
        Rank.Two.toString should be("2")
        Rank.Three.toString should be("3")
        Rank.Four.toString should be("4")
        Rank.Five.toString should be("5")
        Rank.Six.toString should be("6")
        Rank.Seven.toString should be("7")
        Rank.Eight.toString should be("8")
        Rank.Nine.toString should be("9")
        Rank.Ten.toString should be("10")
        Rank.Jack.toString should be("J")
        Rank.Queen.toString should be("Q")
        Rank.King.toString should be("K")
        Rank.Ace.toString should be("A")
      }
    }
  }

  "A Suit" when {
    "converted to a string" should {
      "return the correct string representation" in {
        Suit.Clubs.toString should be("♣")
        Suit.Spades.toString should be("♠")
        Suit.Diamonds.toString should be("♢")
        Suit.Hearts.toString should be("♡")
      }
    }
  }

  "A Card" when {
    val card = Card(Suit.Hearts, Rank.Ace)

    "created" should {
      "have a suit and a rank" in {
        card.suit should be(Suit.Hearts)
        card.rank should be(Rank.Ace)
      }
    }

    "converted to a string" should {
      "return the correct string representation" in {
        card.toString should be("A♡")
      }
    }
  }
}
