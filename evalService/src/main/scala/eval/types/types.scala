package de.htwg.poker.eval.types
import io.circe._
import io.circe.syntax._
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
  implicit val decodeEvalSuit: Decoder[EvalSuit] = Decoder.instance { cursor =>
    cursor.keys.flatMap(_.headOption) match {
      case Some("Clubs")    => Right(EvalSuit.Clubs)
      case Some("Spades")   => Right(EvalSuit.Spades)
      case Some("Diamonds") => Right(EvalSuit.Diamonds)
      case Some("Hearts")   => Right(EvalSuit.Hearts)
      case Some(other)      => Left(DecodingFailure(s"Unknown EvalSuit: $other", cursor.history))
      case None             => Left(DecodingFailure("Expected EvalSuit key", cursor.history))
    }
  }

  implicit val encodeEvalSuit: Encoder[EvalSuit] = Encoder.instance {
    case EvalSuit.Clubs    => Json.obj("Clubs" -> Json.obj())
    case EvalSuit.Spades   => Json.obj("Spades" -> Json.obj())
    case EvalSuit.Diamonds => Json.obj("Diamonds" -> Json.obj())
    case EvalSuit.Hearts   => Json.obj("Hearts" -> Json.obj())
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
  implicit val decodeEvalRank: Decoder[EvalRank] = Decoder.instance { cursor =>
    cursor.keys.flatMap(_.headOption) match {
      case Some("Two")   => Right(EvalRank.Two)
      case Some("Three") => Right(EvalRank.Three)
      case Some("Four")  => Right(EvalRank.Four)
      case Some("Five")  => Right(EvalRank.Five)
      case Some("Six")   => Right(EvalRank.Six)
      case Some("Seven") => Right(EvalRank.Seven)
      case Some("Eight") => Right(EvalRank.Eight)
      case Some("Nine")  => Right(EvalRank.Nine)
      case Some("Ten")   => Right(EvalRank.Ten)
      case Some("Jack")  => Right(EvalRank.Jack)
      case Some("Queen") => Right(EvalRank.Queen)
      case Some("King")  => Right(EvalRank.King)
      case Some("Ace")   => Right(EvalRank.Ace)
      case Some(other)   => Left(DecodingFailure(s"Unknown EvalRank: $other", cursor.history))
      case None          => Left(DecodingFailure("Expected EvalRank key", cursor.history))
    }
  }

  implicit val encodeEvalRank: Encoder[EvalRank] = Encoder.instance {
    case EvalRank.Two   => Json.obj("Two" -> Json.obj())
    case EvalRank.Three => Json.obj("Three" -> Json.obj())
    case EvalRank.Four  => Json.obj("Four" -> Json.obj())
    case EvalRank.Five  => Json.obj("Five" -> Json.obj())
    case EvalRank.Six   => Json.obj("Six" -> Json.obj())
    case EvalRank.Seven => Json.obj("Seven" -> Json.obj())
    case EvalRank.Eight => Json.obj("Eight" -> Json.obj())
    case EvalRank.Nine  => Json.obj("Nine" -> Json.obj())
    case EvalRank.Ten   => Json.obj("Ten" -> Json.obj())
    case EvalRank.Jack  => Json.obj("Jack" -> Json.obj())
    case EvalRank.Queen => Json.obj("Queen" -> Json.obj())
    case EvalRank.King  => Json.obj("King" -> Json.obj())
    case EvalRank.Ace   => Json.obj("Ace" -> Json.obj())
  }
}
