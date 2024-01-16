package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model._

class EvaluatorSpec extends AnyWordSpec with Matchers {

  "An Evaluator" should {
    val evaluator = new Evaluator

    "evaluate the best hand correctly" in {
      val playerCards = List(
        new Card(Suit.Spades, Rank.Jack),
        new Card(Suit.Spades, Rank.Two)
      )
      val boardCards = List(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Four),
        new Card(Suit.Spades, Rank.Five),
        new Card(Suit.Spades, Rank.Ten),
        new Card(Suit.Spades, Rank.Six)
      )
      val result = evaluator.evaluate(playerCards, boardCards)
      result shouldBe "Flush"
    }

    "evaluate the best hand correctly with different cards" in {
      val playerCards = List(
        new Card(Suit.Clubs, Rank.King),
        new Card(Suit.Diamonds, Rank.Queen)
      )
      val boardCards = List(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Four),
        new Card(Suit.Spades, Rank.Five),
        new Card(Suit.Spades, Rank.Ten),
        new Card(Suit.Spades, Rank.Six)
      )
      val result = evaluator.evaluate(playerCards, boardCards)
      result shouldBe "High"
    }
  }
}
