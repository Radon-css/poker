package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.model.GameState
import de.htwg.poker.util.UpdateBoard
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object FileIORoutes extends DefaultJsonProtocol {

  val routes: Route =
    pathPrefix("fileIO") {
      concat(
        // SAVE route
        path("saveState") {
          post {
            entity(as[GameState]) { gameState =>
              complete {
                fileIO.FileIO.save(gameState)
                "Game state saved successfully"
              }
            }
          }
        },
        // LOAD route
        path("loadState") {
          get {
            complete {
              fileIO.FileIO.load
            }
          }
        }
      )
    }
}
