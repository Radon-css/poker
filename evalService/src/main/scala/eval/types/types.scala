package de.htwg.poker.eval.types
import io.circe.{Decoder, Encoder, HCursor, Json}

enum EvalSuit:
  case Clubs, Spades, Diamonds, Hearts

enum EvalRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace

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

import io.circe.{Decoder, Encoder}

object EvalHandRequest {
  implicit val encoder: Encoder[EvalHandRequest] = Encoder.forProduct2("gameState", "player")(e => (e.gameState, e.player))
  implicit val decoder: Decoder[EvalHandRequest] = Decoder.forProduct2("gameState", "player")(EvalHandRequest.apply)
}

object EvalSuit {
  implicit val encodeEvalSuit: Encoder[EvalSuit] = Encoder.encodeString.contramap[EvalSuit] {
    case EvalSuit.Clubs    => "Clubs"
    case EvalSuit.Spades   => "Spades"
    case EvalSuit.Diamonds => "Diamonds"
    case EvalSuit.Hearts   => "Hearts"
  }

  implicit val decodeEvalSuit: Decoder[EvalSuit] = Decoder.decodeString.emap {
    case "Clubs"    => Right(EvalSuit.Clubs)
    case "Spades"   => Right(EvalSuit.Spades)
    case "Diamonds" => Right(EvalSuit.Diamonds)
    case "Hearts"   => Right(EvalSuit.Hearts)
    case other      => Left(s"Unknown EvalSuit: $other")
  }
}
object EvalCard {
  implicit val encoder: Encoder[EvalCard] = Encoder.forProduct2("suit", "rank")(c => (c.suit, c.rank))
  implicit val decoder: Decoder[EvalCard] = Decoder.forProduct2("suit", "rank")(EvalCard.apply)
}
object EvalPlayer {
  implicit val encoder: Encoder[EvalPlayer] = Encoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(p => (p.card1, p.card2, p.playername, p.balance, p.currentAmountBetted, p.folded, p.checkedThisRound))

  implicit val decoder: Decoder[EvalPlayer] = Decoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(EvalPlayer.apply)
}

object EvalGameState {
  implicit val encoder: Encoder[EvalGameState] = Encoder.forProduct11(
    "playersAndBalances",
    "players",
    "deck",
    "playerAtTurn",
    "currentHighestBetSize",
    "board",
    "pot",
    "smallBlind",
    "bigBlind",
    "smallBlindPointer",
    "newRoundStarted"
  )(gs =>
    (
      gs.playersAndBalances,
      gs.players,
      gs.deck,
      gs.playerAtTurn,
      gs.currentHighestBetSize,
      gs.board,
      gs.pot,
      gs.smallBlind,
      gs.bigBlind,
      gs.smallBlindPointer,
      gs.newRoundStarted
    )
  )

  implicit val decoder: Decoder[EvalGameState] = Decoder.forProduct11(
    "playersAndBalances",
    "players",
    "deck",
    "playerAtTurn",
    "currentHighestBetSize",
    "board",
    "pot",
    "smallBlind",
    "bigBlind",
    "smallBlindPointer",
    "newRoundStarted"
  )(EvalGameState.apply)
}

object EvalRank {
  implicit val encodeEvalRank: Encoder[EvalRank] = Encoder.encodeString.contramap[EvalRank] {
    case EvalRank.Two   => "Two"
    case EvalRank.Three => "Three"
    case EvalRank.Four  => "Four"
    case EvalRank.Five  => "Five"
    case EvalRank.Six   => "Six"
    case EvalRank.Seven => "Seven"
    case EvalRank.Eight => "Eight"
    case EvalRank.Nine  => "Nine"
    case EvalRank.Ten   => "Ten"
    case EvalRank.Jack  => "Jack"
    case EvalRank.Queen => "Queen"
    case EvalRank.King  => "King"
    case EvalRank.Ace   => "Ace"
  }

  implicit val decodeEvalRank: Decoder[EvalRank] = Decoder.decodeString.emap {
    case "Two"   => Right(EvalRank.Two)
    case "Three" => Right(EvalRank.Three)
    case "Four"  => Right(EvalRank.Four)
    case "Five"  => Right(EvalRank.Five)
    case "Six"   => Right(EvalRank.Six)
    case "Seven" => Right(EvalRank.Seven)
    case "Eight" => Right(EvalRank.Eight)
    case "Nine"  => Right(EvalRank.Nine)
    case "Ten"   => Right(EvalRank.Ten)
    case "Jack"  => Right(EvalRank.Jack)
    case "Queen" => Right(EvalRank.Queen)
    case "King"  => Right(EvalRank.King)
    case "Ace"   => Right(EvalRank.Ace)
    case other   => Left(s"Unknown EvalRank: $other")
  }
}
