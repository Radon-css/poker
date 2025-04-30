package de.htwg.poker.eval.types

enum EvalSuit:
  case Clubs, Spades, Diamonds, Hearts

enum EvalRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen,
    King, Ace

case class EvalCard(val suit: EvalSuit, val rank: EvalRank) {}

case class EvalPlayer(
    val card1: EvalCard,
    val card2: EvalCard,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {}

case class EvalGameState(
                      playersAndBalances: List[(String, Int)],
                      players: Option[List[EvalPlayer]],
                      deck: Option[List[EvalCard]],
                      playerAtTurn: Int = 0,
                      currentHighestBetSize: Int = 0,
                      board: List[EvalCard] = Nil,
                      pot: Int = 30,
                      smallBlind: Int = 10,
                      bigBlind: Int = 20,
                      smallBlindPointer: Int = 0,
                      newRoundStarted: Boolean = true
                    ) {}

case class EvalHandRequest(gameState: EvalGameState, player: Int)
