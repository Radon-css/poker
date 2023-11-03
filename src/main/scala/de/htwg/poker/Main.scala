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

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield Card(suit, rank)
}

val shuffledDeck: List[Card] = Random.shuffle(deck)

object Dealer {
  def startgame(numPlayers: Int): Int = {
      for(i <- 0 until numPlayers) {
        val player = new Player(shuffledDeck(i),shuffledDeck(i + 1),"Player" + (i + 1))
          println(
      player.playername + " zieht: " + player.card1.toString + ", " + player.card2.toString
      )
      }
      numPlayers
    }
  def flop(numPlayers: Int): Unit = {
    println("")
    println(shuffledDeck(numPlayers * 2 - 1 ).toString + " " + shuffledDeck(numPlayers * 2).toString + " " + shuffledDeck(numPlayers * 2 + 1).toString)
  }
}

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val coins: Int = 1000
) {
     
}

@main
def start: Unit = {
  val numPlayers = Dealer.startgame(3)
  Dealer.flop(numPlayers)
}
