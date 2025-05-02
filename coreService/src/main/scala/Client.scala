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
      gameState: GameState
  ): Future[List[Player]] = {

    val jsonString = gameState.asJson.noSpaces

    val entity = HttpEntity(ContentTypes.`application/json`, jsonString)
    val request = HttpRequest(HttpMethods.POST, "http://eval:8083/eval/calcWinner", entity = entity)

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
    val request = HttpRequest(HttpMethods.POST, "http://eval:8083/eval/evalHand", entity = entity)

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
    val request = HttpRequest(HttpMethods.POST, "http://tui:8082/tui/getTUIView", entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Future.failed(new RuntimeException(s"getTUIView Failed with status ${response.status}"))
      }
    }
  }
}
