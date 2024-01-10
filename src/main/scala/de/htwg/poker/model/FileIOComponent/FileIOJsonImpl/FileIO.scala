package de.htwg.poker.model.FileIOComponent.FileIOJsonImpl
import de.htwg.poker.model.FileIOComponent.FileIOInterface
import de.htwg.poker.model.GameStateComponent.GameStateInterface
import play.api.libs.json._
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit

class FileIO extends FileIOInterface {

  override def load: GameStateInterface = {
    val source: String =
      scala.io.Source.fromFile("grid.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val originalPlayers =
      (json \ "gameState" \ "originalPlayers").as[List[List[JsValue]]]
    val players = (json \ "gameState" \ "players").as[List[List[JsValue]]]
    val deck = (json \ "gameState" \ "deck").as[List[List[JsValue]]]
    val playerAtTurn = (json \ "gameState" \ "playerAtTurn").as[Int]
    val currentHighestBetSize =
      (json \ "gameState" \ "currentHighestBetSize").as[Int]
    val board = (json \ "gameState" \ "board").as[List[List[JsValue]]]
    val pot = (json \ "gameState" \ "pot").as[Int]
    val smallBlind = (json \ "gameState" \ "smallBlind").as[Int]
    val bigBlind = (json \ "gameState" \ "bigBlind").as[Int]
    val smallBlindPointer = (json \ "gameState" \ "smallBlindPointer").as[Int]

    // Now you can reconstruct the GameState object using the extracted values
    new GameState(
      originalPlayers = reconstructPlayers(originalPlayers),
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

  // Helper method to reconstruct players and deck
  private def reconstructPlayers(
      playersJson: List[List[JsValue]]
  ): List[Player] = {
    playersJson.flatten.map { playerJson =>
      val card1 = reconstructCard(playerJson(0))
      val card2 = reconstructCard(playerJson(1))
      val playerName = (playerJson(2) \ "playername").as[String]
      val balance = (playerJson(3) \ "balance").as[Int]
      val currentAmountBetted =
        (playerJson(4) \ "currentAmountBetted").as[Int]
      Player(card1, card2, playerName, balance, currentAmountBetted)
    }
  }

  private def reconstructDeck(deckJson: List[List[JsValue]]): List[Card] = {
    deckJson.flatten.map { cardJson =>
      reconstructCard(cardJson.head.as[JsValue])
    }
  }

  private def reconstructCard(cardJson: JsValue): Card = {
    val rank = (cardJson \ "rank").as[String].toString match {
      case "2"  => Rank.Two
      case "3"  => Rank.Three
      case "4"  => Rank.Four
      case "5"  => Rank.Five
      case "6"  => Rank.Six
      case "7"  => Rank.Seven
      case "8"  => Rank.Eight
      case "9"  => Rank.Nine
      case "10" => Rank.Ten
      case "J"  => Rank.Jack
      case "Q"  => Rank.Queen
      case "K"  => Rank.King
      case "A"  => Rank.Ace
    }

    val suit = (cardJson \ "suit").as[String] match {
      case "♣" => Suit.Clubs
      case "♠" => Suit.Spades
      case "♢" => Suit.Diamonds
      case "♡" => Suit.Hearts
    }
    Card(suit, rank)
  }

  override def save(gameState: GameStateInterface): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("gameState.json"))
    pw.write(Json.prettyPrint(gameStateToJson(gameState)))
    pw.close
  }

  def gameStateToJson(gameState: GameStateInterface) = {
    Json.obj(
      "gameState" -> Json.obj(
        "originalPlayers" -> Json.arr(
          for {
            player <- gameState.getOriginalPlayers
          } yield {
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
          }
        ),
        "players" -> Json.arr(
          for {
            player <- gameState.getPlayers
          } yield {
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
          }
        ),
        "deck" -> Json.arr(
          for {
            card <- gameState.getDeck
          } yield {
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString1
            )
          }
        ),
        "playerAtTurn" -> gameState.getPlayerAtTurn,
        "currentHighestBetSize" -> gameState.getHighestBetSize,
        "board" -> Json.arr(
          for {
            card <- gameState.getBoard
          } yield {
            "card" -> Json.obj(
              "rank" -> card.rank.toString,
              "suit" -> card.suit.toString1
            )
          }
        ),
        "pot" -> gameState.getPot,
        "smallBlind" -> gameState.getSmallBlind,
        "bigBlind" -> gameState.getBigBlind,
        "smallBlindPointer" -> gameState.getSmallBlindPointer
      )
    )
  }
}
