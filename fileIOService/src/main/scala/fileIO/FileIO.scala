package de.htwg.poker
package fileIO

import de.htwg.poker.fileIO.types.{Card, GameState, Player, Rank, Suit}
import java.io._
import play.api.libs.json._

object FileIO {

  def load: GameState = {
    val source: String =
      scala.io.Source.fromFile("gameState.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val playersAndBalances = (json \ "gameState" \ "playersAndBalances").as[List[JsValue]].map { js =>
      ((js \ "name").as[String], (js \ "balance").as[Int])
    }
    val players = (json \ "gameState" \ "players").as[List[JsValue]]
    val deck = (json \ "gameState" \ "deck").as[List[JsValue]]
    val playerAtTurn = (json \ "gameState" \ "playerAtTurn").as[Int]
    val currentHighestBetSize = (json \ "gameState" \ "currentHighestBetSize").as[Int]
    val board = (json \ "gameState" \ "board").as[List[JsValue]]
    val pot = (json \ "gameState" \ "pot").as[Int]
    val smallBlind = (json \ "gameState" \ "smallBlind").as[Int]
    val bigBlind = (json \ "gameState" \ "bigBlind").as[Int]
    val smallBlindPointer = (json \ "gameState" \ "smallBlindPointer").as[Int]
    val newRoundStarted = (json \ "gameState" \ "newRoundStarted").as[Boolean]

    GameState(
      playersAndBalances = playersAndBalances,
      players = Some(reconstructPlayers(players)),
      deck = Some(reconstructDeck(deck)),
      playerAtTurn = playerAtTurn,
      currentHighestBetSize = currentHighestBetSize,
      board = reconstructDeck(board),
      pot = pot,
      smallBlind = smallBlind,
      bigBlind = bigBlind,
      smallBlindPointer = smallBlindPointer,
      newRoundStarted = newRoundStarted
    )
  }

  def save(gameState: GameState): Unit = {
    val pw = new PrintWriter(new File("gameState.json"))
    pw.write(Json.prettyPrint(gameStateToJson(gameState)))
    pw.close
  }

  private def gameStateToJson(gameState: GameState) = {
    Json.obj(
      "gameState" -> Json.obj(
        "playersAndBalances" -> Json.arr(gameState.playersAndBalances.map { case (name, balance) =>
          Json.obj(
            "name" -> name,
            "balance" -> balance
          )
        }),
        "players" -> gameState.players.map(_.map { player =>
          Json.obj(
            "Player" -> Json.obj(
              "card1" -> Json.obj(
                "rank" -> player.card1.rank.toString,
                "suit" -> player.card1.suit.toString
              ),
              "card2" -> Json.obj(
                "rank" -> player.card2.rank.toString,
                "suit" -> player.card2.suit.toString
              ),
              "playername" -> player.playername,
              "balance" -> player.balance,
              "currentAmountBetted" -> player.currentAmountBetted
            )
          )
        }),
        "deck" -> gameState.deck.map(_.map { card =>
          Json.obj(
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString
            )
          )
        }),
        "playerAtTurn" -> gameState.playerAtTurn,
        "currentHighestBetSize" -> gameState.currentHighestBetSize,
        "board" -> gameState.board.map { card =>
          Json.obj(
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString
            )
          )
        },
        "pot" -> gameState.pot,
        "smallBlind" -> gameState.smallBlind,
        "bigBlind" -> gameState.bigBlind,
        "smallBlindPointer" -> gameState.smallBlindPointer,
        "newRoundStarted" -> gameState.newRoundStarted
      )
    )
  }

  private def reconstructPlayers(players: List[JsValue]): List[Player] = {
    players.map { player =>
      val card1 = (player \ "Player" \ "card1").as[JsValue]
      val card2 = (player \ "Player" \ "card2").as[JsValue]
      val rank1 = (card1 \ "rank").as[String]
      val suit1 = (card1 \ "suit").as[String]
      val rank2 = (card2 \ "rank").as[String]
      val suit2 = (card2 \ "suit").as[String]
      Player(
        card1 = Card(
          Suit.valueOf(reverseSuit(suit1)),
          Rank.valueOf(reverseRank(rank1))
        ),
        card2 = Card(
          Suit.valueOf(reverseSuit(suit2)),
          Rank.valueOf(reverseRank(rank2))
        ),
        playername = (player \ "Player" \ "playername").as[String],
        balance = (player \ "Player" \ "balance").as[Int],
        currentAmountBetted = (player \ "Player" \ "currentAmountBetted").as[Int]
      )
    }
  }

  private def reverseSuit(suit: String): String = {
    suit match {
      case "♣" => "Clubs"
      case "♠" => "Spades"
      case "♢" => "Diamonds"
      case "♡" => "Hearts"
      case s   => s // handle already converted suits
    }
  }

  private def reverseRank(rank: String): String = {
    rank match {
      case "2"  => "Two"
      case "3"  => "Three"
      case "4"  => "Four"
      case "5"  => "Five"
      case "6"  => "Six"
      case "7"  => "Seven"
      case "8"  => "Eight"
      case "9"  => "Nine"
      case "10" => "Ten"
      case "J"  => "Jack"
      case "Q"  => "Queen"
      case "K"  => "King"
      case "A"  => "Ace"
      case r    => r // handle already converted ranks
    }
  }

  private def reconstructDeck(deck: List[JsValue]): List[Card] = {
    deck.map { card =>
      val rank = (card \ "card" \ "rank").as[String]
      val suit = (card \ "card" \ "suit").as[String]
      Card(Suit.valueOf(reverseSuit(suit)), Rank.valueOf(reverseRank(rank)))
    }
  }
}
