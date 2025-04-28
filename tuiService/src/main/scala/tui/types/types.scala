package de.htwg.poker.tui.types

enum TUISuit:
  case Clubs, Spades, Diamonds, Hearts
  override def toString: String = this match {
    case Clubs    => s"♣"
    case Spades   => s"♠"
    case Diamonds => s"♢"
    case Hearts   => s"♡"
  }

enum TUIRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace
  override def toString: String = this match {
    case Two   => "2"
    case Three => "3"
    case Four  => "4"
    case Five  => "5"
    case Six   => "6"
    case Seven => "7"
    case Eight => "8"
    case Nine  => "9"
    case Ten   => "T"
    case Jack  => "J"
    case Queen => "Q"
    case King  => "K"
    case Ace   => "A"
  }

case class TUICard(val suit: TUISuit, val rank: TUIRank) {
  override def toString: String = "[" + rank.toString + suit.toString + "]"
}

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
