package de.htwg.poker.gui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.util.UpdateBoard
import de.htwg.poker.model.GameState
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

object guiRoutes extends DefaultJsonProtocol {

  val routes: Route =
    pathPrefix("gui") {
      concat(
        // GUIVIEW routes
        path("render" / Segment) { gameStateJson =>
          get {
            complete {
              val gameState = gameStateJson.parseJson.convertTo[GameState]
              GUIView.render(gameState)
            }
          }
        }
      )
    }
}
