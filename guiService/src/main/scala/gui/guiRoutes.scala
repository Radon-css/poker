package de.htwg.poker.gui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.gui.types.GameState

class guiRoutes {

  val routes: Route =
    pathPrefix("gui") {
      concat(
        // GUIVIEW routes
        path("getGUIView") { (handEval, gameStateJson) =>
          get {
            complete {
              val gameState = gameStateJson.parseJson.convertTo[GameState]
              GUIView.getView(handEval, gameState)
            }
          }
        }
      )
    }
}
