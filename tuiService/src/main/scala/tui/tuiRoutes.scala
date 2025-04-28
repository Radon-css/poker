package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import de.htwg.poker.tui.types.TUIGameState
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class tuiRoutes {

  val routes: Route =
  pathPrefix("tui") {
    concat(
      // TUIVIEW routes
      path("getTUIView") {
        post {
          entity(as[String]) { gameStateJson =>
            decode[TUIGameState](gameStateJson) match {
              case Right(gameState) =>
                val result = TUIView.getView(gameState)
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
