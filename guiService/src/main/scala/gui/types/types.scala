package de.htwg.poker.gui.types

enum GUISuit:
  case Clubs, Spades, Diamonds, Hearts

enum GUIRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace

case class GUICard(val suit: GUISuit, val rank: GUIRank) {}

case class GUIPlayer(
    val card1: GUICard,
    val card2: GUICard,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}

case class GUIGameState(
    playersAndBalances: List[(String, Int)],
    players: Option[List[GUIPlayer]],
    deck: Option[List[GUICard]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[GUICard] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0,
    newRoundStarted: Boolean = true
) {}
