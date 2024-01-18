package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.poker.model.{Card, Rank, Suit}

class EvaluatorSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Evaluator" should {
    "evaluate the best hand correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ten),
        Card(Suit.Spades, Rank.Jack),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Ace)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("StraightFlush")
    }
    /*
    "compare hands correctly" in {
      val evaluator = new Evaluator()

      val hand1 = (
        List(
          Card(Suit.Spades, Rank.Ace),
          Card(Suit.Spades, Rank.King),
          Card(Suit.Spades, Rank.Queen),
          Card(Suit.Spades, Rank.Jack),
          Card(Suit.Spades, Rank.Ten)
        ),
        Evaluator.Type.StraightFlush
      )

      val hand2 = (
        List(
          Card(Suit.Hearts, Rank.Ace),
          Card(Suit.Hearts, Rank.King),
          Card(Suit.Hearts, Rank.Queen),
          Card(Suit.Hearts, Rank.Jack),
          Card(Suit.Hearts, Rank.Ten)
        ),
        Evaluator.Type.StraightFlush
      )

      val hands = List(hand1, hand2)

      val result = evaluator.compareHands(hands)

      result should be(hand2._1)
    }
     */
  }
}
