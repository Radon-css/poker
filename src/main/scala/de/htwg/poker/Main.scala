package de.htwg.poker;
import scala.util.Random

enum Rank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ass

enum Suit:
  case Clubs, Spades, Diamonds, Hearts

case class Card(val suit: Suit, val rank: Rank) {
  override def toString: String = s"${rank.toString} of ${suit.toString}"
}

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield Card(suit, rank)
}

// Unicode-Symbols
/*def assignUnicodeSymbol(element: String): String = {
      element match {
        case "Apfel"   => "\uD83C\uDF4E" // Apfel-Symbol
        case "Banane"  => "\uD83C\uDF4C" // Banane-Symbol
        case "Kirsche" => "\uD83C\uDF52" // Kirsche-Symbol
        case "Dattel"  => "\uD83C\uDF50" // Dattel-Symbol
        case _         => element // Standardwert, falls nicht gefunden
      }
    }

    val mappedList = elements.map(assignUnicodeSymbol)*/

val shuffledDeck: List[Card] = Random.shuffle(deck)

case class Hand() {
  val random = new Random()
  val Card1 = shuffledDeck(0)
  val Card2 = shuffledDeck(2)
}

case class Dealer(val hand: Hand) {
  val Hand = hand
}

case class Player(
    val hand: Hand,
    val playername: String,
    val coins: Int = 1000
) {}

@main
def start: Unit = {
  println("🂡")
  val playerHand = new Hand()
  val dealerHand = new Hand()
  val s = new Player(playerHand, "Julian")
  val d = new Dealer(dealerHand)
  println("Der Dealer zieht: " + d.Hand.Card1.toString)
  println(s.playername + " zieht: " + s.hand.Card2.toString)
}
