package de.htwg.poker.gui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.gui.types.GameState

import io.circe._, io.circe.generic.auto._, io.circe.parser._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import akka.http.scaladsl.server.Directives._

class guiRoutes {

  val routes: Route =
    pathPrefix("gui") {
      concat(
        // GUIVIEW routes
        path("getGUIView") {
          parameters("handEval", "gameStateJson") { (handEval, gameStateJson) =>
            get {
              decode[GameState](gameStateJson) match {
                case Right(gameState) =>
                  complete(GUIView.getView(handEval, gameState))
                case Left(error) =>
                  complete((400, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
