package de.htwg.poker.model.FileIOComponent.FileIOJsonImpl
import de.htwg.poker.model.FileIOComponent.FileIOInterface
import de.htwg.poker.model.GameStateComponent.GameStateInterface
import play.api.libs.json._
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit
import de.htwg.poker.model.CardsComponent.CardInterface
import com.google.inject.{Guice, Inject}

class FileIO @Inject() extends FileIOInterface {

  override def load: GameStateInterface = {
    val source: String =
      scala.io.Source.fromFile("gameState.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val originalPlayers =
      (json \ "gameState" \ "originalPlayers").as[List[JsValue]]
    val players = (json \ "gameState" \ "players").as[List[JsValue]]
    val deck = (json \ "gameState" \ "deck").as[List[JsValue]]
    val playerAtTurn = (json \ "gameState" \ "playerAtTurn").as[Int]
    val currentHighestBetSize =
      (json \ "gameState" \ "currentHighestBetSize").as[Int]
    val board = (json \ "gameState" \ "board").as[List[JsValue]]
    val pot = (json \ "gameState" \ "pot").as[Int]
    val smallBlind = (json \ "gameState" \ "smallBlind").as[Int]
    val bigBlind = (json \ "gameState" \ "bigBlind").as[Int]
    val smallBlindPointer = (json \ "gameState" \ "smallBlindPointer").as[Int]

    // Now you can reconstruct the GameState object using the extracted values
    new GameState(
      originalPlayers = Nil, // reconstructPlayers(originalPlayers),
      players = Some(reconstructPlayers(players)),
      deck = Some(reconstructDeck(deck)),
      playerAtTurn = playerAtTurn,
      currentHighestBetSize = currentHighestBetSize,
      board = reconstructDeck(board),
      pot = pot,
      smallBlind = smallBlind,
      bigBlind = bigBlind,
      smallBlindPointer = smallBlindPointer
    )
  }
  override def save(gameState: GameStateInterface): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("gameState.json"))
    pw.write(Json.prettyPrint(GameStateToJson(gameState)))
    pw.close
  }

  def GameStateToJson(gameState: GameStateInterface) = {
    Json.obj(
      "gameState" -> Json.obj(
        "originalPlayers" -> Json.arr(gameState.getOriginalPlayers.map {
          player =>
            Json.obj(
              "Players" -> Json.obj(
                "card1" -> Json.obj(
                  "rank" -> player.card1.rank.toString,
                  "suit" -> player.card1.suit.toString1
                ),
                "card2" -> Json.obj(
                  "rank" -> player.card2.rank.toString,
                  "suit" -> player.card2.suit.toString1
                ),
                "playername" -> player.playername,
                "balance" -> player.balance,
                "currentAmountBetted" -> player.currentAmountBetted
              )
            )
        }),
        "players" -> gameState.getPlayers.map { player =>
          Json.obj(
            "Player" -> Json.obj(
              "card1" -> Json.obj(
                "rank" -> player.card1.rank.toString,
                "suit" -> player.card1.suit.toString1
              ),
              "card2" -> Json.obj(
                "rank" -> player.card2.rank.toString,
                "suit" -> player.card2.suit.toString1
              ),
              "playername" -> player.playername,
              "balance" -> player.balance,
              "currentAmountBetted" -> player.currentAmountBetted
            )
          )
        },
        "deck" -> gameState.getDeck.map { card =>
          Json.obj(
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString1
            )
          )
        },
        "playerAtTurn" -> gameState.getPlayerAtTurn,
        "currentHighestBetSize" -> gameState.getHighestBetSize,
        "board" -> gameState.getBoard.map { card =>
          Json.obj(
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString1
            )
          )
        },
        "pot" -> gameState.getPot,
        "smallBlind" -> gameState.getSmallBlind,
        "bigBlind" -> gameState.getBigBlind,
        "smallBlindPointer" -> gameState.getSmallBlindPointer
      )
    )
  }

  def reconstructPlayers(players: List[JsValue]): List[Player] = {
    players.map { player =>
      val card1 = (player \ "Player" \ "card1").as[JsValue]
      val card2 = (player \ "Player" \ "card2").as[JsValue]
      val rank1 = (card1 \ "rank").as[String]
      val suit1 = (card1 \ "suit").as[String]
      val rank2 = (card2 \ "rank").as[String]
      val suit2 = (card2 \ "suit").as[String]
      new Player(
        card1 = new Card(
          Suit.valueOf(reverseSuit(suit1)),
          Rank.valueOf(reverseRank(rank1))
        ),
        card2 = new Card(
          Suit.valueOf(reverseSuit(suit2)),
          Rank.valueOf(reverseRank(rank2))
        ),
        playername = (player \ "Player" \ "playername").as[String],
        balance = (player \ "Player" \ "balance").as[Int],
        currentAmountBetted =
          (player \ "Player" \ "currentAmountBetted").as[Int]
      )
    }
  }

  def reverseSuit(suit: String): String = {
    suit match {
      case "♣" => "Clubs"
      case "♠" => "Spades"
      case "♢" => "Diamonds"
      case "♡" => "Hearts"
    }
  }

  def reverseRank(rank: String): String = {
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
    }
  }

  def reconstructDeck(deck: List[JsValue]): List[Card] = {
    deck.map { card =>
      val rank = (card \ "card" \ "rank").as[String]
      val suit = (card \ "card" \ "suit").as[String]
      new Card(Suit.valueOf(reverseSuit(suit)), Rank.valueOf(reverseRank(rank)))
    }
  }
}
