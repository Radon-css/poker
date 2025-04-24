package de.htwg.poker
package fileIO.types

enum FileIOSuit:
  case Clubs, Spades, Diamonds, Hearts

enum FileIORank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace

class FileIOCard(val suit: FileIOSuit, val rank: FileIORank) {}

case class FileIOPlayer(
    val card1: FileIOCard,
    val card2: FileIOCard,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}

case class FileIOGameState(
    playersAndBalances: List[(String, Int)],
    players: Option[List[FileIOPlayer]],
    deck: Option[List[FileIOCard]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[FileIOCard] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0,
    newRoundStarted: Boolean = true
) {}
