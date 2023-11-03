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

case class Dealer() {
  def startgame(numPlayers: Int): List[Player] = {
    // Überprüfe, ob genügend Karten im Deck für die Spieler vorhanden sind
    require(numPlayers * 2 <= shuffledDeck.length, "Nicht genug Karten im Deck!")

    // Erstelle eine Liste von Spielern und teile ihnen Karten zu
    val players = (0 until numPlayers).map { playerIndex =>
      val cardIndex = playerIndex * 2
      val player = Player(s"Player ${playerIndex + 1}")
      player.card1 = shuffledDeck(cardIndex)
      player.card2 = shuffledDeck(cardIndex + 1)
      player
    }.toList

    // Entferne die zugeteilten Karten aus dem Deck
    val remainingDeck = shuffledDeck.drop(numPlayers * 2)

    // Aktualisiere das Deck im Dealer
    shuffledDeck = remainingDeck

    players
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
  Dealer.startgame(1)
  println(
    s.playername + " zieht: " + s.card1.toString + ", " + s.card2.toString
  )
}
