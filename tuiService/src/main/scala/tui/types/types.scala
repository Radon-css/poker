package de.htwg.poker.tui.types

enum TUISuit:
  case Clubs, Spades, Diamonds, Hearts

enum TUIRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace

case class TUICard(val suit: TUISuit, val rank: TUIRank) {}

case class TUIPlayer(
    val card1: TUICard,
    val card2: TUICard,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}

case class TUIGameState(
    playersAndBalances: List[(String, Int)],
    players: Option[List[TUIPlayer]],
    deck: Option[List[TUICard]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[TUICard] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0,
    newRoundStarted: Boolean = true
) {}
