package de.htwg.poker.gui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import de.htwg.poker.gui.types.GUIGameState
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class guiRoutes {

  val routes: Route =
    pathPrefix("gui") {
      concat(
        // GUIVIEW routes
        path("getGUIView") {
          parameters("handEval", "gameStateJson") { (handEval, gameStateJson) =>
            get {
              decode[GUIGameState](gameStateJson) match {
                case Right(gameState) =>
                  val result = GUIView.getView(handEval, gameState)
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
