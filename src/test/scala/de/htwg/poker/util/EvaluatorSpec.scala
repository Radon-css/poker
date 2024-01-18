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

      result should be("Flush")
    }

  }
}
