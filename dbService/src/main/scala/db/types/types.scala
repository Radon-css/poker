package de.htwg.poker.db.types
import io.circe.{Decoder, Encoder, HCursor, Json}

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
import io.circe.{Decoder, Encoder}

object DbSuit {
  implicit val encodeDbSuit: Encoder[DbSuit] = Encoder.encodeString.contramap[DbSuit] {
    case DbSuit.Clubs    => "Clubs"
    case DbSuit.Spades   => "Spades"
    case DbSuit.Diamonds => "Diamonds"
    case DbSuit.Hearts   => "Hearts"
  }

  implicit val decodeDbSuit: Decoder[DbSuit] = Decoder.decodeString.emap {
    case "Clubs"    => Right(DbSuit.Clubs)
    case "Spades"   => Right(DbSuit.Spades)
    case "Diamonds" => Right(DbSuit.Diamonds)
    case "Hearts"   => Right(DbSuit.Hearts)
    case other      => Left(s"Unknown DbSuit: $other")
  }
}
object DbCard {
  implicit val encoder: Encoder[DbCard] = Encoder.forProduct2("suit", "rank")(c => (c.suit, c.rank))
  implicit val decoder: Decoder[DbCard] = Decoder.forProduct2("suit", "rank")(DbCard.apply)
}
object DbPlayer {
  implicit val encoder: Encoder[DbPlayer] = Encoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(p => (p.card1, p.card2, p.playername, p.balance, p.currentAmountBetted, p.folded, p.checkedThisRound))

  implicit val decoder: Decoder[DbPlayer] = Decoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(DbPlayer.apply)
}

object DbGameState {
  implicit val encoder: Encoder[DbGameState] = Encoder.forProduct11(
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

  implicit val decoder: Decoder[DbGameState] = Decoder.forProduct11(
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
  )(DbGameState.apply)
}

object DbRank {
  implicit val encodeDbRank: Encoder[DbRank] = Encoder.encodeString.contramap[DbRank] {
    case DbRank.Two   => "Two"
    case DbRank.Three => "Three"
    case DbRank.Four  => "Four"
    case DbRank.Five  => "Five"
    case DbRank.Six   => "Six"
    case DbRank.Seven => "Seven"
    case DbRank.Eight => "Eight"
    case DbRank.Nine  => "Nine"
    case DbRank.Ten   => "Ten"
    case DbRank.Jack  => "Jack"
    case DbRank.Queen => "Queen"
    case DbRank.King  => "King"
    case DbRank.Ace   => "Ace"
  }

  implicit val decodeDbRank: Decoder[DbRank] = Decoder.decodeString.emap {
    case "Two"   => Right(DbRank.Two)
    case "Three" => Right(DbRank.Three)
    case "Four"  => Right(DbRank.Four)
    case "Five"  => Right(DbRank.Five)
    case "Six"   => Right(DbRank.Six)
    case "Seven" => Right(DbRank.Seven)
    case "Eight" => Right(DbRank.Eight)
    case "Nine"  => Right(DbRank.Nine)
    case "Ten"   => Right(DbRank.Ten)
    case "Jack"  => Right(DbRank.Jack)
    case "Queen" => Right(DbRank.Queen)
    case "King"  => Right(DbRank.King)
    case "Ace"   => Right(DbRank.Ace)
    case other   => Left(s"Unknown DbRank: $other")
  }
}
