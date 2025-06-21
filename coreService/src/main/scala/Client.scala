package de.htwg.poker

import akka.Done
import akka.actor.ActorSystem
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import model._
import model.{Card, GameState, Player}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import kafka.CoreKafkaClient

object Client {

  import AppConfig.{materializer, system}

  val kafkaClient = new CoreKafkaClient()(system, materializer)

  def calcWinner(
      gameState: GameState
  ): Future[List[Player]] = {
    val payload = gameState.asJson.noSpaces
      kafkaClient.sendAndAwait("calcWinner", payload, "core-eval-requests").flatMap { json =>
        decode[List[Player]](json) match {
          case Right(players) => Future.successful(players)
          case Left(err)      => Future.failed(new RuntimeException(err))
        }
      }
  }

  def evalHand(gameState: GameState, player: Int): Future[String] = {
    val jsonString = Map(
      "gameState" -> gameState.asJson,
      "player" -> player.asJson
    ).asJson.noSpaces

    kafkaClient.sendAndAwait("evalHand", jsonString, "core-eval-requests")
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

    kafkaClient.sendAndAwait("fetchBalance", jsonString, "core-db-requests").flatMap { json =>
      decode[PlayerBalance](json) match {
        case Right(result) => Future.successful(result)
        case Left(error)   => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
      }
    }
  }

  def updateBalance(playerID: String, balance: Int): Future[String] = {
    val jsonString = Map(
      "playerID" -> playerID.asJson,
      "balance" -> balance.asJson
    ).asJson.noSpaces

    kafkaClient.sendAndAwait("updateBalance", jsonString, "core-db-requests")
  }

  def insertPlayer(playerID: String): Future[String] = {
  val jsonString = Map("playerID" -> playerID.asJson).asJson.noSpaces

  kafkaClient.sendAndAwait("insertPlayer", jsonString, "core-db-requests")
}

  case class PlayerName(playerID: String, name: String)

  def updateName(playerID: String, name: String): Future[String] = {
    val jsonString = Map(
      "playerID" -> playerID.asJson,
      "name" -> name.asJson
    ).asJson.noSpaces

    kafkaClient.sendAndAwait("updateName", jsonString, "core-db-requests")
  }

  def fetchName(playerID: String)(implicit system: ActorSystem, mat: Materializer): Future[PlayerName] = {
    val jsonString = Map("playerID" -> playerID.asJson).asJson.noSpaces

    kafkaClient.sendAndAwait("fetchName", jsonString, "core-db-requests").flatMap { json =>
      decode[PlayerName](json) match {
        case Right(result) => Future.successful(result)
        case Left(error)   => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
      }
    }
  }

  case class InsertGameStateResponse(status: String, message: String)

  def insertGameState(jsonString: String)(implicit system: ActorSystem, mat: Materializer): Future[InsertGameStateResponse] = {
    kafkaClient.sendAndAwait("insertGameState", jsonString, "core-db-requests").flatMap { json =>
      decode[InsertGameStateResponse](json) match {
        case Right(result) => Future.successful(result)
        case Left(error)   => Future.failed(new RuntimeException(s"Invalid JSON response: ${error.getMessage}"))
      }
    }
  }
}
