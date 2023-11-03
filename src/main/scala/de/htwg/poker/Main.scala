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

  def handout(playerNames: String): List[Player] = {
    val playerNamesList = playerNames.split(" ")
    val players = playerNamesList.foldLeft(List.empty[Player]) {
      (list, playerName) =>
        val deckIndex = playerNamesList.indexOf(playerName) * 2
        val player = new Player(
          shuffledDeck(deckIndex),
          shuffledDeck(deckIndex + 1),
          playerName
        )
        print(
          playerName + " " + player.card1.toString + " " + player.card2.toString + "     "
        )
        list :+ player

    }
    players
  }
  def flop(numPlayers: Int): Unit = {
    println("")
    println("")
    println(
      shuffledDeck(numPlayers * 2 - 1).toString + " " + shuffledDeck(
        numPlayers * 2
      ).toString + " " + shuffledDeck(numPlayers * 2 + 1).toString
    )
  }
}

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val coins: Int = 1000
) {}

@main
def start: Unit = {
  val playerNames = scala.io.StdIn.readLine("Spielernamen eingeben: ")
  val playerList = Dealer.handout(playerNames)
  Dealer.flop(playerList.length)
}
