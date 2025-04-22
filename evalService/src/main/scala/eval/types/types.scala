package de.htwg.poker.tui.types

enum Suit:
  case Clubs, Spades, Diamonds, Hearts

enum Rank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace

class Card(val suit: Suit, val rank: Rank) {}

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}
