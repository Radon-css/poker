package de.htwg.poker.controllers

import akka.actor.ActorRefFactory
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.{Actor, Props}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketUpgradeResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.{Flow, Sink, Source}
import concurrent.duration.DurationInt
import de.htwg.poker.controllers.Controller
import de.htwg.poker.controllers.PokerControllerPublisher
import de.htwg.poker.model.Card
import de.htwg.poker.model.GameState
import de.htwg.poker.model.Player
import javax.inject.Singleton
import play.api.libs.json.{Format, JsValue, Json}
import scala.collection.immutable.ListMap
import scala.collection.immutable.VectorMap
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.swing.Reactor
import scala.swing.event.Event

/** This controller creates an Action to handle HTTP requests to the application's home page.
  */
class Receiver()(implicit
    val system: ActorSystem[Nothing], // This is already here
    val mat: Materializer
) {

  implicit val classicSystem: akka.actor.ActorSystem = system.classicSystem

  val gameController = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )

  val pokerControllerPublisher = new PokerControllerPublisher(gameController)

  // lobby

  // maps names to playerIDs
  var players: ListMap[String, String] = ListMap()

  // list of playerIDs of players that are currently offline
  var offlinePlayers: List[String] = List()

  var smallBlind: Int = 10
  var bigBlind: Int = 20

  var isLobby = false
  var newRoundStarted = true

  def pokerAsText = pokerControllerPublisher.toString()
  def gameState = pokerControllerPublisher.gameState

  def newGame() = Route {
    isLobby = false

    pokerControllerPublisher.createGame(
      players.keys.toList,
      smallBlind.toString,
      bigBlind.toString
    )
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def bet(amount: Int) = Route {
    println("PokerController.bet() function called")
    pokerControllerPublisher.bet(amount)
    while (offlinePlayerIsAtTurn) {
      Thread.sleep(1000)
      pokerControllerPublisher.fold()
    }
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def allIn() = Route {
    println("PokerController.allIn() function called")
    pokerControllerPublisher.allIn()
    while (offlinePlayerIsAtTurn) {
      Thread.sleep(1000)
      pokerControllerPublisher.fold()
    }
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def fold() = Route {
    println("PokerController.fold() function called")
    pokerControllerPublisher.fold()
    while (offlinePlayerIsAtTurn) {
      Thread.sleep(1000)
      pokerControllerPublisher.fold()
    }
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def call() = Route {
    println("PokerController.call() function called")
    pokerControllerPublisher.call()

    while (offlinePlayerIsAtTurn) {
      Thread.sleep(1000)
      pokerControllerPublisher.fold()
    }
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))

  }

  def check() = Route {
    println("PokerController.check() function called")
    pokerControllerPublisher.check()
    val updatedPokerJson = pokerToJson()

    while (offlinePlayerIsAtTurn) {
      Thread.sleep(1000)
      pokerControllerPublisher.fold()
    }
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def restartGame() = Route {
    pokerControllerPublisher.restartGame()
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  // lobby functions
  def join(): Route = {
    println("Joining lobby")
    isLobby = true

    optionalHeaderValueByName("playerID") { playerIdOpt =>
      val playerID = playerIdOpt.getOrElse("")
      val playersLength = players.toList.length

      println(s"players: $players")
      println(s"playerID: $playerID")

      if (playerID.isEmpty) {
        complete(StatusCodes.BadRequest -> "Error: playerID is missing")
      } else if (players.values.toList.contains(playerID)) {
        println("Player already in lobby")
        val updatedPokerJson = pokerToJson()
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
      } else if (playersLength >= 6) {
        complete(StatusCodes.BadRequest -> "Error: Player limit reached")
      } else {
        val newPlayerName = "Player" + (playersLength + 1)
        players = players + (newPlayerName -> playerID)
        pokerControllerPublisher.lobby()

        println(s"New Player: $playerID $newPlayerName")
        val updatedPokerJson = pokerToJson()
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
      }
    }
  }

  def leave() = Route {
    isLobby = true;
    pokerControllerPublisher.leave()
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def disconnected(playerID: String) = {
    offlinePlayers = playerID :: offlinePlayers
    pokerControllerPublisher.connectionEvent()
  }

  def reconnected(playerID: String) = {
    offlinePlayers = offlinePlayers.filter(_ != playerID)
    pokerControllerPublisher.connectionEvent()
  }

  def offlinePlayerIsAtTurn = {
    val playerAtTurn = gameState.players.getOrElse(gameState.playerAtTurn, "")
    println("OFFLINEPLAYERISATTURN: Player at turn: " + playerAtTurn)
    val playerID = players.getOrElse(gameState.getCurrentPlayer.playername, "")
    println("OFFLINEPLAYERISATTURN: Player ID: " + playerID)
    println("OFFLINEPLAYERISATTURN:" + offlinePlayers.contains(playerID))
    offlinePlayers.contains(playerID)
  }

  case class GameConfig(
      players: List[String],
      smallBlind: String,
      bigBlind: String
  )
  object GameConfig {
    import play.api.libs.json._
    implicit val gameConfigFormat: Format[GameConfig] = Json.format[GameConfig]
  }

  def getJson = Route {
    val updatedPokerJson = pokerToJson()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))

  }

  def pokerToJson() = {
    Json.obj(
      "isLobby" -> isLobby,
      "newRoundStarted" -> gameState.newRoundStarted,
      "lobbyPlayers" -> players,
      "smallBlind" -> smallBlind,
      "bigBlind" -> bigBlind,
      "players" -> gameState.players.getOrElse(List.empty[Player]).zipWithIndex.map { case (player, index) =>
        Json.obj(
          "player" -> Json.obj(
            "id" -> players.getOrElse(player.playername, ""),
            "card1rank" -> player.card1.rank.toString,
            "card1suit" -> player.card1.suit.id,
            "card2rank" -> player.card2.rank.toString,
            "card2suit" -> player.card2.suit.id,
            "playername" -> player.playername,
            "balance" -> player.balance,
            "currentAmountBetted" -> player.currentAmountBetted,
            "folded" -> player.folded,
            "handEval" -> Json.toJson(Await.result(gameState.getHandEval(index), 1.seconds)),
            "offline" -> offlinePlayers.contains(
              players.getOrElse(player.playername, "")
            )
          )
        )
      },
      "playerAtTurn" -> gameState.playerAtTurn,
      "highestBetSize" -> gameState.currentHighestBetSize,
      "board" -> gameState.board.map { card =>
        Json.obj(
          "card" -> Json.obj(
            "rank" -> card.rank.toString,
            "suit" -> card.suit.id
          )
        )
      },
      "pot" -> gameState.pot
    )
  }

  def socket(): Route = {
    parameter("playerID".?) { playerIdOpt =>
      val playerID = playerIdOpt.getOrElse("")
      val flow = Flow.fromSinkAndSource(
        Sink.foreach(println), // Handle incoming messages
        Source.maybe // Send outgoing messages
      )
      handleWebSocketMessages(flow)
    }
  }

  object PokerWebSocketActorFactory {
    def create(out: akka.actor.ActorRef, playerID: String) =
      Props(new PokerWebSocketActor(out, playerID))
  }

  class PokerWebSocketActor(out: akka.actor.ActorRef, id: String) extends Actor with Reactor {
    import context.dispatcher

    val playerID = id

    listenTo(pokerControllerPublisher)

    // Scheduler für Pings
    val pingScheduler = context.system.scheduler.scheduleAtFixedRate(
      initialDelay = 5.seconds,
      interval = 5.seconds,
      receiver = self,
      message = "sendPing"
    )

    // Zeitstempel der letzten Pong-Antwort
    var lastPongReceived: Long = System.currentTimeMillis()

    def receive: Receive = {

      case "sendPing" =>
        out ! "ping" // Sende Ping an den Client
        // Prüfe, ob der Client innerhalb des Zeitlimits geantwortet hat
        if (System.currentTimeMillis() - lastPongReceived > 10000) { // Timeout: 5 Sekunden
          println(s"No pong received from $playerID, closing connection")
          if (!offlinePlayers.contains(playerID)) {
            disconnected(playerID);
            if (offlinePlayerIsAtTurn) {
              println("triggering fold because player offline")
              pokerControllerPublisher.fold()
            }
          }
        }

      case "pong" =>
        println(s"Pong received from $playerID")
        lastPongReceived = System.currentTimeMillis() // Zeitstempel aktualisieren
        if (offlinePlayers.contains(playerID)) {
          reconnected(playerID);
        }

      case msg: String =>
        out ! pokerToJson().toString()
    }

    reactions += { case _ =>
      sendJsonToClient()
    }

    def sendJsonToClient() = {
      out ! pokerToJson().toString()
    }

    override def postStop(): Unit = {
      println(s"Player $playerID disconnected.")
    }
  }
}
