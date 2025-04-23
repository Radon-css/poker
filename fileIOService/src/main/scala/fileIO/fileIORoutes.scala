package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.fileIO.types.GameState
import de.htwg.poker.util.UpdateBoard

class FileIORoutes {

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
