package de.htwg.poker.eval

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import de.htwg.poker.eval.types.{EvalGameState, EvalPlayer, EvalCard, EvalHandRequest}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class evalRoutes {

  val routes: Route =
    pathPrefix("eval") {
      concat(
        // Handinfo route
        path("evalHand") {
          post {
            entity(as[String]) { body =>
              decode[EvalHandRequest](body) match {
                case Right(EvalHandRequest(gameState, player)) =>
                  val playerCards: List[EvalCard] = List(gameState.players.get(player).card1, gameState.players.get(player).card2)
                  val boardCards: List[EvalCard] = gameState.board
                  val result = HandInfo.evalHand(playerCards, boardCards)
                  complete(HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        // Evaluator route
        path("calcWinner") {
          post {
            entity(as[String]) { body =>
              decode[EvalGameState](body) match {
                case Right(gameState) =>
                  val players = gameState.players.get
                  val boardCards = gameState.board
                  val result = Evaluator.calcWinner(players, boardCards)
                  complete(HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
