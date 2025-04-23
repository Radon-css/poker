package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.tui.types.GameState
import io.circe._, io.circe.generic.auto._, io.circe.parser._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

class tuiRoutes {

  val routes: Route =
    pathPrefix("tui") {
      concat(
        // TUIVIEW routes
        path("getTUIView") {
          parameters("gameStateJson") { gameStateJson =>
            get {
              decode[GameState](gameStateJson) match {
                case Right(gameState) =>
                  complete(TUIView.getView(gameState))
                case Left(error) =>
                  complete((400, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
