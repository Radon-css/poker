package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.tui.types.GameState

object utilRoutes extends DefaultJsonProtocol {

  val routes: Route =
    pathPrefix("tui") {
      concat(
        // TUIVIEW routes
        path("getTUIView") { (gameStateJson) =>
          get {
            complete {
              val gameState = gameStateJson.parseJson.convertTo[GameState]
              TUIView.getView(gameState)
            }
          }
        }
        //
      )
    }
}
