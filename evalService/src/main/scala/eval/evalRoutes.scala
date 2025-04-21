package de.htwg.poker.eval

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.util.UpdateBoard
import de.htwg.poker.model.GameState
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

object evalRoutes extends DefaultJsonProtocol {

  val routes: Route =
    pathPrefix("eval") {
      // handinfo routes
      concat(
        path("evaluate") {
          post {
            entity(as[GameState]) { gameState =>
              val playerCards = gameState.players.get.head.cards
              val boardCards = gameState.board
              val result = HandInfo.getHandInfo(playerCards, boardCards)
              complete(result)
            }
          }
        },
        // Evaluator routes
        path("calcWinner") {
          post {
            entity(as[GameState]) { gameState =>
              val players = gameState.players.get
              val boardCards = gameState.board
              val result = Evaluator.calcWinner(players, boardCards)
              complete(result)
            }
          }
        }
      )
    }
}
