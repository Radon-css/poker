package de.htwg.poker.tui

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.tui.types.TUIGameState
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._

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
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid String: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }

}
