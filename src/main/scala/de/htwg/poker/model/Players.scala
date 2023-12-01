package de.htwg.poker.model

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0
) {
  def balanceToString() = "(" + balance + "$)"
}
