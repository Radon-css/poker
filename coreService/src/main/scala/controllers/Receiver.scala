package de.htwg.poker.controllers

import akka.actor.ActorRefFactory
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated} // Note: Using untyped ActorRef
import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketUpgradeResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.{Done, NotUsed}
import concurrent.duration.DurationInt
import de.htwg.poker.Client
import de.htwg.poker.controllers.Controller
import de.htwg.poker.model.Card
import de.htwg.poker.model.GameState
import de.htwg.poker.model.Player
import de.htwg.poker.util.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import play.api.libs.json.{Format, JsValue, Json}
import scala.collection.immutable.ListMap
import scala.collection.immutable.VectorMap
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.swing.Reactor
import scala.swing.event.Event

/** This controller creates an Action to handle HTTP requests to the application's home page.
  */
class Receiver()(implicit
    val system: ActorSystem, // This is already here
    val mat: Materializer
) {

  private val connectionManager = system.actorOf(Props[ConnectionManager](), "connectionManager")

  private def broadcastUpdate(): Unit = {
    println("Broadcasting update to all connections")
    connectionManager ! ConnectionManager.Broadcast(pokerToJson().toString())
  }

  val gameController = new Controller(
    new GameState(Nil, None, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )

  val stream = new Stream(gameController)(using system)
  // lobby

  // maps names to (cookieID, authID)
  var players: ListMap[String, (String, String)] = ListMap()

  // list of cookieIDs of players that are currently offline
  var offlinePlayers: List[String] = List()

  var smallBlind: Int = 10
  var bigBlind: Int = 20

  var isLobby = false
  var newRoundStarted = true

  def gameState = gameController.gameState

  def newGame() = Route {
    isLobby = false

    gameController.createGame(
      players.keys.toList,
      Some(ListMap(players.map { case (name, (_, authId)) => name -> authId }.toSeq*)),
      smallBlind.toString,
      bigBlind.toString
    )
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def bet(amount: Int) = Route {
    println("PokerController.bet() function called")
    gameController.bet(amount)
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def allIn() = Route {
    println("PokerController.allIn() function called")
    gameController.allIn()
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def fold() = Route {
    println("PokerController.fold() function called")
    gameController.fold
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def call() = Route {
    println("PokerController.call() function called")
    gameController.call

    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def check() = Route {
    println("PokerController.check() function called")
    gameController.check
    val updatedPokerJson = pokerToJson()

    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def restartGame() = Route {
    gameController.restartGame
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  // lobby functions
  def join(): Route = {
    println("Joining lobby")
    isLobby = true

    optionalHeaderValueByName("playerID") { playerIdOpt =>
      optionalHeaderValueByName("authID") { authIdOpt =>
        val cookieID = playerIdOpt.getOrElse("")
        val authID = authIdOpt.getOrElse("")
        val playersLength = players.size

        println(s"players: $players")
        println(s"cookieID: $cookieID")
        println(s"authID: $authID")

        if (cookieID.isEmpty) {
          broadcastUpdate()
          complete(StatusCodes.BadRequest -> "Error: playerID is missing")
        } else if (players.values.toList.contains(cookieID)) {
          println("Player already in lobby")
          val updatedPokerJson = pokerToJson()
          broadcastUpdate()
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
        } else if (playersLength >= 6) {
          broadcastUpdate()
          complete(StatusCodes.BadRequest -> "Error: Player limit reached")
        } else {
          onComplete(Client.fetchName(authID)) {
            case scala.util.Success(playerNameObj) =>
              val playerName = playerNameObj.name
              if (authID.nonEmpty) {
                players = players + (playerName -> (cookieID, authID))
              }

              println(s"New Player: $cookieID $playerName")
              val updatedPokerJson = pokerToJson()
              broadcastUpdate()
              complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))

            case scala.util.Failure(e) =>
              println(s"Failed to fetch name for $authID: ${e.getMessage}")
              complete(StatusCodes.InternalServerError -> "Failed to fetch player name")
          }
        }
      }
    }
  }

  def leave() = Route {
    isLobby = true;
    val updatedPokerJson = pokerToJson()
    broadcastUpdate()
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, updatedPokerJson.toString)))
  }

  def disconnected(cookieID: String) = {
    broadcastUpdate()
  }

  def reconnected(cookieID: String) = {
    broadcastUpdate()
  }

  def fetchBalance(authID: String): Route = {
    onComplete(Client.fetchBalance(authID)) {
      case scala.util.Success(balance) =>
        val jsonString = balance.asJson.noSpaces
        complete(HttpEntity(ContentTypes.`application/json`, jsonString))
      case scala.util.Failure(_) =>
        complete(StatusCodes.InternalServerError -> "Failed to fetch balance")
    }
  }

  def updateName(authID: String, name: String): Route = {
    println(s"Updating name for authID: $authID to $name")
    onComplete(Client.updateName(authID, name)) {
      case scala.util.Success(json) =>
        renamePlayerByAuthId(name, authID)
        broadcastUpdate()
        complete(HttpEntity(ContentTypes.`application/json`, json))
      case scala.util.Failure(_) =>
        complete(StatusCodes.InternalServerError -> "Failed to update name")
    }
  }

  def insertPlayer(authID: String): Route = {
    onComplete(Client.insertPlayer(authID)) {
      case scala.util.Success(json) =>
        complete(HttpEntity(ContentTypes.`application/json`, json))
      case scala.util.Failure(_) =>
        complete(StatusCodes.InternalServerError -> "Failed to insert player")
    }
  }

  def renamePlayerByAuthId(newName: String, authId: String): Unit = {
    players.find { case (_, (_, aId)) => aId == authId } match {
      case Some((oldName, (cookieId, _))) =>
        players -= oldName
        players += (newName -> (cookieId, authId))
      case None =>
        println(s"No player found with authId $authId")
    }
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
    broadcastUpdate()
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
            "id" -> players.get(player.playername).map(_._1).getOrElse(""),
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
              players.get(player.playername).map(_._1).getOrElse("")
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
      val cookieID = playerIdOpt.getOrElse("")

      val handler = Sink.foreach[Message] { case _ =>
      }

      val source = Source
        .actorRef[Message](
          completionMatcher = { case Done => CompletionStrategy.immediately },
          failureMatcher = PartialFunction.empty,
          bufferSize = 100,
          overflowStrategy = OverflowStrategy.dropHead
        )
        .mapMaterializedValue { actorRef =>
          connectionManager ! ConnectionManager.Register(actorRef)
          actorRef ! TextMessage(pokerToJson().toString())

          // Verwenden Sie das system-Member statt classicSystem
          system.actorOf(Props(new Actor {
            context.watch(actorRef)
            def receive: Receive = { case Terminated(_) =>
              connectionManager ! ConnectionManager.Unregister(actorRef)
              disconnected(cookieID)
              context.stop(self)
            }
          }))

          NotUsed
        }

      handleWebSocketMessages(Flow.fromSinkAndSource(handler, source))
    }
  }

  // Aktualisierte ConnectionManager-Definition
}

class ConnectionManager extends Actor {
  import ConnectionManager._

  var connections: Set[ActorRef] = Set.empty // klassischer ActorRef

  def receive: Receive = {
    case Register(connection) =>
      connections += connection
      context.watch(connection)

    case Unregister(connection) =>
      connections -= connection
      context.unwatch(connection)

    case Broadcast(message) =>
      connections.foreach(_ ! TextMessage(message))

    case GetConnections =>
      sender() ! connections

    case Terminated(connection) =>
      connections -= connection
  }
}

object ConnectionManager {
  sealed trait Command
  case class Register(connection: ActorRef) extends Command // klassischer ActorRef
  case class Unregister(connection: ActorRef) extends Command
  case class Broadcast(message: String) extends Command
  case object GetConnections extends Command
  case object SendPings extends Command
}
