package de.htwg.poker.model

enum Rank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ass
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
    case Ass   => "A"
  }
enum Suit:
  case Clubs, Spades, Diamonds, Hearts
  override def toString: String = this match {
    case Clubs    => "♣"
    case Spades   => "♠"
    case Diamonds => "♢"
    case Hearts   => "♡"
  }

case class Card(val suit: Suit, val rank: Rank) {
  override def toString: String = rank.toString + suit.toString
}