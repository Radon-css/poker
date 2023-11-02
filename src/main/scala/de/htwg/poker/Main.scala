package de.htwg.poker;
import scala.util.Random

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
    case Jack  => "Jack"
    case Queen => "Queen"
    case King  => "King"
    case Ass   => "Ace"
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

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield Card(suit, rank)
}

val shuffledDeck: List[Card] = Random.shuffle(deck)

case class Hand() {
  /*  def draw(): (Card,Card) = {

    (shuffledDeck(0),shuffledDeck(1))
} */
  val Card1 = shuffledDeck(0)
  val Card2 = shuffledDeck(1)

}

case class Dealer(val hand: Hand) {}

case class Player(
    val hand: Hand,
    val playername: String,
    val coins: Int = 1000
) {}

@main
def start: Unit = {
  val playerHand = new Hand()
  val dealerHand = new Hand()
  val s = new Player(playerHand, "Julian")
  val d = new Dealer(dealerHand)
  println(
    "Der Dealer zieht: " + d.hand.Card1.toString + ", " + d.hand.Card2.toString
  )
  println(
    s.playername + " zieht: " + s.hand.Card1.toString + ", " + s.hand.Card2.toString
  )
}
