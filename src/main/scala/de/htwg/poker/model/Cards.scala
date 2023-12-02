package de.htwg.poker.model

val ANSI_BLACK = "\u001b[30m"
val ANSI_RED = "\u001b[31m"
val ANSI_RESET = "\u001b[0m"

enum Rank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace
  override def toString: String = this match {
    case Two   => "2"
    case Three => "3"
    case Four  => "4"
    case Five  => "5"
    case Six   => "6"
    case Seven => "7"
    case Eight => "8"
    case Nine  => "9"
    case Ten   => "10"
    case Jack  => "J"
    case Queen => "Q"
    case King  => "K"
    case Ace   => "A"
  }

enum Suit:
  case Clubs, Spades, Diamonds, Hearts
  override def toString: String = this match {
    case Clubs    => s"$ANSI_BLACK♣$ANSI_RESET"
    case Spades   => s"$ANSI_BLACK♠$ANSI_RESET"
    case Diamonds => s"$ANSI_RED♢$ANSI_RESET"
    case Hearts   => s"$ANSI_RED♡$ANSI_RESET"
  }

class Card private (val suit: Suit, val rank: Rank) {
  override def toString: String = "[" + rank.toString + suit.toString + "]"
}

object Card {
  class CardBuilder {
    private var suit: Suit = _
    private var rank: Rank = _

    def setSuit(suit: Suit): CardBuilder = {
      this.suit = suit
      this
    }

    def setRank(rank: Rank): CardBuilder = {
      this.rank = rank
      this
    }

    def build(): Card = {
      new Card(suit, rank)
    }
  }

  def create(): CardBuilder = {
    new CardBuilder
  }
}
