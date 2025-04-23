package de.htwg.poker
package fileIO

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.fileIO.types.GameState
import spray.json.DefaultJsonProtocol._

class FileIORoutes {

  // Define the JSON format for GameState
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat2(GameState) // Adjust the number of fields to match GameState's constructor

  val routes: Route =
    pathPrefix("fileIO") {
      concat(
        // SAVE route
        path("saveState") {
          post {
            entity(as[GameState]) { gameState =>
              complete {
                FileIO.save(gameState)
                "Game state saved successfully"
              }
            }
          }
        },
        // LOAD route
        path("loadState") {
          get {
            complete {
              FileIO.load
            }
          }
        }
      )
    }
}
