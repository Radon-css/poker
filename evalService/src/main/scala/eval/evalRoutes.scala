package de.htwg.poker.eval

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.eval.types.GameState
import io.circe.generic.auto._
import io.circe.syntax._
import de.htwg.poker.eval.types.Player
import de.htwg.poker.eval.types.Card

class evalRoutes {

  val routes: Route =
    pathPrefix("eval") {
      // handinfo routes
      concat(
        path("evaluate") {
          post {
            entity(as[GameState]) { gameState =>
              val playerCards = gameState.players.get.head.cards
              val boardCards = gameState.board
              val result = HandInfo.evalHand(playerCards, boardCards)
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
