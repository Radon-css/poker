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
    "eval high card correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Diamonds, Rank.Ten)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("High")
    }
    "eval pair correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Two),
        Card(Suit.Clubs, Rank.Three)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("Pair")
    }
    "eval two pair correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Ace),
        Card(Suit.Clubs, Rank.Three)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("TwoPair")
    }
    "eval three of a kind correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.King),
        Card(Suit.Clubs, Rank.Three)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("Triples")
    }
    "eval straight correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Clubs, Rank.Ace),
        Card(Suit.Clubs, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ten),
        Card(Suit.Spades, Rank.Jack),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Nine)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("Straight")
    }
    "eval flush correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ten),
        Card(Suit.Spades, Rank.Jack),
        Card(Suit.Spades, Rank.Two),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Spades, Rank.Five)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("Flush")
    }
    "eval full house correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Ace)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("FullHouse")
    }
    "eval four of a kind correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Diamonds, Rank.Ace)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("Quads")
    }
    "eval straight flush correctly" in {
      val evaluator = new Evaluator()

      val playerCards = List(
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.King)
      )
      val boardCards = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Spades, Rank.Jack),
        Card(Suit.Spades, Rank.Ten)
      )

      val result = evaluator.evaluate(playerCards, boardCards)

      result should be("StraightFlush")
    }
  }
}
