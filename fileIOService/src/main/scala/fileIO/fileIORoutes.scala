package de.htwg.poker
package fileIO

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import de.htwg.poker.fileIO.types.FileIOGameState
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class FileIORoutes {

  val routes: Route =
    pathPrefix("fileIO") {
      concat(
        // SAVE route
        path("saveState") {
          post {
            entity(as[String]) { body =>
              decode[FileIOGameState](body) match {
                case Right(gameState) =>
                  FileIO.save(gameState)
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Game state saved successfully"))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        // LOAD route
        path("loadState") {
          get {
            val gameState = FileIO.load // ⬅️ Stelle sicher, dass das ein GameState ist
            complete(HttpEntity(ContentTypes.`application/json`, gameState.asJson.noSpaces))
          }
        }
      )
    }
}
