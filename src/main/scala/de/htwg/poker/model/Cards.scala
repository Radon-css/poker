package de.htwg.poker.model
import scala.util.Random

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
    case Ten   => "T"
    case Jack  => "J"
    case Queen => "Q"
    case King  => "K"
    case Ace   => "A"
  }
  def strength: Int = this match {
    case Two   => 1
    case Three => 2
    case Four  => 3
    case Five  => 4
    case Six   => 5
    case Seven => 6
    case Eight => 7
    case Nine  => 8
    case Ten   => 9
    case Jack  => 10
    case Queen => 11
    case King  => 12
    case Ace   => 13
  }
  def prime: Int = this match {
    case Two   => 2
    case Three => 3
    case Four  => 5
    case Five  => 7
    case Six   => 11
    case Seven => 13
    case Eight => 17
    case Nine  => 19
    case Ten   => 23
    case Jack  => 29
    case Queen => 31
    case King  => 37
    case Ace   => 41
  }

enum Suit:
  case Clubs, Spades, Diamonds, Hearts
  override def toString: String = this match {
    case Clubs    => s"♣"
    case Spades   => s"♠"
    case Diamonds => s"♢"
    case Hearts   => s"♡"
  }
  def id: Int = this match {
    case Clubs    => 1
    case Spades   => 2
    case Diamonds => 3
    case Hearts   => 4
  }

  def toHtml: String = this match {
    case Clubs =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-club-fill\" viewBox=\"0 0 16 16\"><path d=\"M11.5 12.5a3.493 3.493 0 0 1-2.684-1.254 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907 3.5 3.5 0 1 1-2.538-5.743 3.5 3.5 0 1 1 6.708 0A3.5 3.5 0 1 1 11.5 12.5\"/></svg>"
    case Spades =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-spade-fill\" viewBox=\"0 0 16 16\"><path d=\"M7.184 11.246A3.5 3.5 0 0 1 1 9c0-1.602 1.14-2.633 2.66-4.008C4.986 3.792 6.602 2.33 8 0c1.398 2.33 3.014 3.792 4.34 4.992C13.86 6.367 15 7.398 15 9a3.5 3.5 0 0 1-6.184 2.246 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907\"/></svg>"
    case Diamonds =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-diamond-fill\" viewBox=\"0 0 16 16\"><path fill-rule=\"evenodd\" d=\"M6.95.435c.58-.58 1.52-.58 2.1 0l6.515 6.516c.58.58.58 1.519 0 2.098L9.05 15.565c-.58.58-1.519.58-2.098 0L.435 9.05a1.482 1.482 0 0 1 0-2.098L6.95.435z\"/></svg>"
    case Hearts =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-suit-heart-fill\" viewBox=\"0 0 16 16\"><path d=\"M4 1c2.21 0 4 1.755 4 3.92C8 2.755 9.79 1 12 1s4 1.755 4 3.92c0 3.263-3.234 4.414-7.608 9.608a.513.513 0 0 1-.784 0C3.234 9.334 0 8.183 0 4.92 0 2.755 1.79 1 4 1\"/></svg>"
  }

class Card(val suit: Suit, val rank: Rank) {
  override def toString: String = "[" + rank.toString + suit.toString + "]"
  def toHtml: String = {
    s"<div class=\"rounded-lg bg-slate-100 w-6 h-9 hover:scale-125 flex flex-col justify-center items-center shadow-xl shadow-black/50\">${suit.toHtml}<h1 class=\"font-bold \">${rank.toString}</h1></div>"
  }
}

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield new Card(suit, rank)
}

def shuffleDeck: List[Card] = {
  val random = new Random
  random.shuffle(deck)
}

def removeCards(deck: List[Card], n: Int): List[Card] = {
  val newCardList = deck.drop(n);
  newCardList
}
