package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardsSpec extends AnyWordSpec with Matchers {

  val ANSI_BLACK = "\u001b[30m"
  val ANSI_RED = "\u001b[31m"
  val ANSI_RESET = "\u001b[0m"

  "A Card" when {
    "created" should {
      "have a suit and rank" in {
        val card = new Card(Suit.Clubs, Rank.Ace)
        card.suit should be(Suit.Clubs)
        card.rank should be(Rank.Ace)
      }
    }

    "converted to string" should {
      "return a string representation of the card" in {
        val card = new Card(Suit.Spades, Rank.King)
        card.toString should be(s"[K$ANSI_BLACK♠$ANSI_RESET]")
      }
    }

    "converted to HTML" should {
      "return an HTML representation of the card" in {
        val card = new Card(Suit.Diamonds, Rank.Two)
        card.toHtml should be(
          "<div class=\"rounded-lg bg-slate-100 w-6 h-9 hover:scale-125 flex flex-col justify-center items-center shadow-xl shadow-black/50\"><svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-diamond-fill\" viewBox=\"0 0 16 16\"><path fill-rule=\"evenodd\" d=\"M6.95.435c.58-.58 1.52-.58 2.1 0l6.515 6.516c.58.58.58 1.519 0 2.098L9.05 15.565c-.58.58-1.519.58-2.098 0L.435 9.05a1.482 1.482 0 0 1 0-2.098L6.95.435z\"/></svg><h1 class=\"font-bold \">2</h1></div>"
        )
      }
    }
  }
  "A Rank" when {
    "converted to string" should {
      "return a string representation of the rank" in {
        Rank.Ace.toString should be("A")
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
      }
    }

    "converted to strength" should {
      "return a strength representation of the rank" in {
        Rank.Ace.strength should be(13)
        Rank.Two.strength should be(1)
        Rank.Three.strength should be(2)
        Rank.Four.strength should be(3)
        Rank.Five.strength should be(4)
        Rank.Six.strength should be(5)
        Rank.Seven.strength should be(6)
        Rank.Eight.strength should be(7)
        Rank.Nine.strength should be(8)
        Rank.Ten.strength should be(9)
        Rank.Jack.strength should be(10)
        Rank.Queen.strength should be(11)
        Rank.King.strength should be(12)
      }
    }
  }
  "A Suit" when {
    "converted to string" should {
      "return a string representation of the suit" in {
        Suit.Clubs.toString should be(s"$ANSI_BLACK♣$ANSI_RESET")
        Suit.Spades.toString should be(s"$ANSI_BLACK♠$ANSI_RESET")
        Suit.Diamonds.toString should be(s"$ANSI_RED♢$ANSI_RESET")
        Suit.Hearts.toString should be(s"$ANSI_RED♡$ANSI_RESET")
      }
    }

    "converted to id" should {
      "return an id representation of the suit" in {
        Suit.Clubs.id should be(1)
        Suit.Spades.id should be(2)
        Suit.Diamonds.id should be(3)
        Suit.Hearts.id should be(4)
      }
    }
  }

  "A Deck" when {
    "shuffled" should {
      "return a shuffled list of cards" in {
        val shuffledDeck = shuffleDeck
        shuffledDeck should not be deck
        shuffledDeck should contain theSameElementsAs deck
      }
    }

    "cards are removed" should {
      "return a new deck with the specified number of cards removed" in {
        val n = 5
        val newDeck = removeCards(deck, n)
        newDeck.length should be(deck.length - n)
        newDeck should contain theSameElementsAs deck.drop(n)
      }
    }
  }
}
