package de.htwg.poker;
import scala.util.Random

val Card = Vector(
  "Two",
  "Three",
  "Four",
  "Five",
  "Six",
  "Seven",
  "Eight",
  "Nine",
  "Ten",
  "Jack",
  "Queen",
  "King",
  "Ass"
)

case class Hand() {
  val random = new Random()
  val Card1 = Card(random.nextInt(Card.length))
  val Card2 = Card(random.nextInt(Card.length))
  override def toString(): String = {
    Card1 + ", " + Card2
  }

}

case class Dealer(val hand: Hand) {
  val Hand = hand
}

case class Player(val hand: Hand, val playername: String) {
  val Hand = hand
  val Name = playername
}

@main
def start: String = {
  val playerHand = new Hand()
  val dealerHand = new Hand()
  val s = new Player(playerHand, "Julian")
  val d = new Dealer(dealerHand)
  println("Der Dealer zieht: " + d.Hand.toString)
  println(s.Name + " zieht: " + s.Hand.toString)
  "Der Dealer zieht: " + d.Hand.toString + s.Name + " zieht: " + s.Hand.toString
}
