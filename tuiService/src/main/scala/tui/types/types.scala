package de.htwg.poker.tui.types
import io.circe.{Decoder, Encoder, HCursor, Json}

enum TUISuit:
  case Clubs, Spades, Diamonds, Hearts
  override def toString: String = this match {
    case Clubs    => s"♣"
    case Spades   => s"♠"
    case Diamonds => s"♢"
    case Hearts   => s"♡"
  }

enum TUIRank:
  case Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace
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
import io.circe.{Decoder, Encoder}

object TUISuit {
  implicit val encodeTUISuit: Encoder[TUISuit] = Encoder.encodeString.contramap[TUISuit] {
    case TUISuit.Clubs    => "Clubs"
    case TUISuit.Spades   => "Spades"
    case TUISuit.Diamonds => "Diamonds"
    case TUISuit.Hearts   => "Hearts"
  }

  implicit val decodeTUISuit: Decoder[TUISuit] = Decoder.decodeString.emap {
    case "Clubs"    => Right(TUISuit.Clubs)
    case "Spades"   => Right(TUISuit.Spades)
    case "Diamonds" => Right(TUISuit.Diamonds)
    case "Hearts"   => Right(TUISuit.Hearts)
    case other      => Left(s"Unknown TUISuit: $other")
  }
}
object TUICard {
  implicit val encoder: Encoder[TUICard] = Encoder.forProduct2("suit", "rank")(c => (c.suit, c.rank))
  implicit val decoder: Decoder[TUICard] = Decoder.forProduct2("suit", "rank")(TUICard.apply)
}
object TUIPlayer {
  implicit val encoder: Encoder[TUIPlayer] = Encoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(p => (p.card1, p.card2, p.playername, p.balance, p.currentAmountBetted, p.folded, p.checkedThisRound))

  implicit val decoder: Decoder[TUIPlayer] = Decoder.forProduct7(
    "card1",
    "card2",
    "playername",
    "balance",
    "currentAmountBetted",
    "folded",
    "checkedThisRound"
  )(TUIPlayer.apply)
}

object TUIGameState {
  implicit val encoder: Encoder[TUIGameState] = Encoder.forProduct11(
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

  implicit val decoder: Decoder[TUIGameState] = Decoder.forProduct11(
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
  )(TUIGameState.apply)
}

object TUIRank {
  implicit val encodeTUIRank: Encoder[TUIRank] = Encoder.encodeString.contramap[TUIRank] {
    case TUIRank.Two   => "Two"
    case TUIRank.Three => "Three"
    case TUIRank.Four  => "Four"
    case TUIRank.Five  => "Five"
    case TUIRank.Six   => "Six"
    case TUIRank.Seven => "Seven"
    case TUIRank.Eight => "Eight"
    case TUIRank.Nine  => "Nine"
    case TUIRank.Ten   => "Ten"
    case TUIRank.Jack  => "Jack"
    case TUIRank.Queen => "Queen"
    case TUIRank.King  => "King"
    case TUIRank.Ace   => "Ace"
  }

  implicit val decodeTUIRank: Decoder[TUIRank] = Decoder.decodeString.emap {
    case "Two"   => Right(TUIRank.Two)
    case "Three" => Right(TUIRank.Three)
    case "Four"  => Right(TUIRank.Four)
    case "Five"  => Right(TUIRank.Five)
    case "Six"   => Right(TUIRank.Six)
    case "Seven" => Right(TUIRank.Seven)
    case "Eight" => Right(TUIRank.Eight)
    case "Nine"  => Right(TUIRank.Nine)
    case "Ten"   => Right(TUIRank.Ten)
    case "Jack"  => Right(TUIRank.Jack)
    case "Queen" => Right(TUIRank.Queen)
    case "King"  => Right(TUIRank.King)
    case "Ace"   => Right(TUIRank.Ace)
    case other   => Left(s"Unknown TUIRank: $other")
  }
}
