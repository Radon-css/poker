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
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Client {

  import AppConfig.{materializer, system}

  def calcWinner(
      players: List[Player],
      boardCards: List[Card]
  ): Future[List[Player]] = {

    val jsonString = Map(
      "players" -> Some(players).asJson,
      "boardCards" -> boardCards.asJson
    ).asJson.noSpaces

    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8080/eval/calcWinner", entity = entity)

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
      playerCards: List[Card],
      boardCards: List[Card]
  ): Future[String] = {

    val jsonString = Map(
      "playerCards" -> playerCards.asJson,
      "boardCards" -> boardCards.asJson
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

  def saveState(
      gameState: GameState
  ): Future[String] = {

    val jsonString = gameState.asJson.noSpaces
    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://127.0.0.1:8080/fileIO/saveState", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"saveState Failed with status ${response.status}"))
      }
    }
  }

  def loadState()(implicit system: ActorSystem, mat: Materializer): Future[GameState] = {

    val request = HttpRequest(HttpMethods.GET, "http://127.0.0.1:8080/fileIO/loadState")

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].flatMap { json =>
            decode[GameState](json) match {
              case Right(state) => Future.successful(state)
              case Left(err)    => Future.failed(new RuntimeException(s"JSON decoding error: $err"))
            }
          }
        case _ =>
          Future.failed(new RuntimeException(s"loadState Failed with status ${response.status}"))
      }
    }
  }
}
