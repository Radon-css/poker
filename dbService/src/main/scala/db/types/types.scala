package de.htwg.poker.db.types

enum DbSuit:
  case Clubs, Spades, Diamonds, Hearts

enum DbRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace

case class DbCard(val suit: DbSuit, val rank: DbRank) {}

case class DbPlayer(
    val card1: DbCard,
    val card2: DbCard,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}

case class DbGameState(
    playersAndBalances: List[(String, Int)],
    players: Option[List[DbPlayer]],
    deck: Option[List[DbCard]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[DbCard] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0,
    newRoundStarted: Boolean = true
) {}
