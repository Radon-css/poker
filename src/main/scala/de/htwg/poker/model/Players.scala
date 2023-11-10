package de.htwg.poker.model

case class Player(
    val card1: Option[Card],
    val card2: Option[Card],
    val playername: String,
    val coins: Int = 1000
) {
    override def toString(): String = playername + " "
}
