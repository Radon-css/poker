package de.htwg.poker.model

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val coins: Int = 1000
) {
  override def toString(): String =
    playername + "(" + coins + ") " + card1 + "," + card2 + "    "
}
