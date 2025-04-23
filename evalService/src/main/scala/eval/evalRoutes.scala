package de.htwg.poker.eval

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.eval.types.{GameState, Player, Card}
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

class evalRoutes {

  val routes: Route =
    pathPrefix("eval") {
      concat(
        // Handinfo route
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
        // Evaluator route
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
