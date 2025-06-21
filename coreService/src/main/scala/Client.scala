package de.htwg.poker

import akka.Done
import akka.actor.ActorSystem
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import de.htwg.poker.model._
import de.htwg.poker.model.{Card, GameState, Player}
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Client {

  implicit val playerEncoder: Encoder[Player] = deriveEncoder
  implicit val playerDecoder: Decoder[Player] = deriveDecoder

  implicit val gameStateEncoder: Encoder[GameState] = deriveEncoder
  implicit val gameStateDecoder: Decoder[GameState] = deriveDecoder

  implicit val playerBalanceEncoder: Encoder[PlayerBalance] = deriveEncoder
  implicit val playerBalanceDecoder: Decoder[PlayerBalance] = deriveDecoder

  implicit val playerNameEncoder: Encoder[PlayerName] = deriveEncoder
  implicit val playerNameDecoder: Decoder[PlayerName] = deriveDecoder

  implicit val insertGameStateResponseEncoder: Encoder[InsertGameStateResponse] = deriveEncoder
  implicit val insertGameStateResponseDecoder: Decoder[InsertGameStateResponse] = deriveDecoder

  import AppConfig.{materializer, system}

  def calcWinner(
      gameState: GameState
  ): Future[List[Player]] = {

    val jsonString = gameState.asJson.noSpaces

    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8083/eval/calcWinner", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].flatMap { json =>
            decode[List[Player]](json) match {
              case Right(players) => Future.successful(players)
              case Left(err)      => Future.failed(new RuntimeException(s"JSON decoding error: $err"))
            }
          }
        case _ =>
          Future.failed(new RuntimeException(s"calcWinner Failed with status ${response.status}"))
      }
    }
  }

  def evalHand(
      gameState: GameState,
      player: Int
  ): Future[String] = {

    val jsonString = Map(
      "gameState" -> gameState.asJson,
      "player" -> player.asJson
    ).asJson.noSpaces

    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8083/eval/evalHand", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"evalHand Failed with status ${response.status}"))
      }
    }
  }

  def getGUIView(
      gameState: GameState,
      handEval: String
  ): Future[String] = {

    val jsonString = Map(
      "gameState" -> gameState.asJson,
      "handEval" -> handEval.asJson
    ).asJson.noSpaces

    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8081/gui/getGUIView", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"getGUIView Failed with status ${response.status}"))
      }
    }
  }

  def getTUIView(
      gameState: GameState
  ): Future[String] = {

    val jsonString = gameState.asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8082/tui/getTUIView", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"getTUIView Failed with status ${response.status}"))
      }
    }
  }

  case class PlayerBalance(playerID: String, balance: Int)
  def fetchBalance(playerID: String)(implicit system: ActorSystem, mat: Materializer): Future[PlayerBalance] = {
    val jsonString = Map("playerID" -> playerID.asJson).asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/fetchBalance", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].flatMap { responseBody =>
            decode[PlayerBalance](responseBody) match {
              case Right(playerBalance) => Future.successful(playerBalance)
              case Left(error)          => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
            }
          }
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { errorBody =>
            Future.failed(new RuntimeException(s"fetchBalance failed with status ${response.status}: $errorBody"))
          }
      }
    }
  }

  def updateBalance(
      playerID: String,
      balance: Int
  ): Future[String] = {

    val jsonString = Map(
      "playerID" -> playerID.asJson,
      "balance" -> balance.asJson
    ).asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/updateBalance", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"updateBalance Failed with status ${response.status}"))
      }
    }
  }

  def insertPlayer(
      playerID: String
  ): Future[String] = {

    val jsonString = Map("playerID" -> playerID.asJson).asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/insertPlayer", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"insertPlayer Failed with status ${response.status}"))
      }
    }
  }

  case class PlayerName(playerID: String, name: String)

  def updateName(playerID: String, name: String): Future[String] = {
    val jsonString = Map(
      "playerID" -> playerID.asJson,
      "name" -> name.asJson
    ).asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/updateName", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"updateName Failed with status ${response.status}"))
      }
    }
  }

  def fetchName(playerID: String)(implicit system: ActorSystem, mat: Materializer): Future[PlayerName] = {
    val jsonString = Map("playerID" -> playerID.asJson).asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/fetchName", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].flatMap { responseBody =>
            decode[PlayerName](responseBody) match {
              case Right(playerName) => Future.successful(playerName)
              case Left(error)       => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
            }
          }
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { errorBody =>
            Future.failed(new RuntimeException(s"fetchName failed with status ${response.status}: $errorBody"))
          }
      }
    }
  }

  case class InsertGameStateResponse(status: String, message: String)

  def insertGameState(jsonString: String)(implicit system: ActorSystem, mat: Materializer): Future[InsertGameStateResponse] = {
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8084/db/insertGameState", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].flatMap { responseBody =>
            decode[InsertGameStateResponse](responseBody) match {
              case Right(result) => Future.successful(result)
              case Left(error)   => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
            }
          }
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { errorBody =>
            Future.failed(new RuntimeException(s"insertGameState failed with status ${response.status}: $errorBody"))
          }
      }
    }
  }
}
