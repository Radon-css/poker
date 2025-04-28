/*package de.htwg.poker.gui

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.gui.types.GUIGameState
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.LoggerFactory

class GuiRoutes {
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  val routes: Route =
    pathPrefix("gui") {
      concat(
        path("getGUIView") {
          post {
            entity(as[String]) { jsonString =>
              logger.debug(s"Received GUI request: $jsonString")

              decode[Map[String, Json]](jsonString) match {
                case Right(data) =>
                  (data.get("gameState").flatMap(_.as[GUIGameState].toOption), data.get("handEval").flatMap(_.as[String].toOption)) match {
                    case (Some(gameState), Some(handEval)) =>
                      val result = GUIView.getView(handEval, gameState)
                      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result))
                    case (None, _) =>
                      complete(StatusCodes.BadRequest -> "Invalid or missing 'gameState'")
                    case (_, None) =>
                      complete(StatusCodes.BadRequest -> "Invalid or missing 'handEval'")
                  }
                case Left(error) =>
                  complete(StatusCodes.BadRequest -> s"Invalid JSON: ${error.getMessage}")
              }
            }
          }
        }
      )
    }
} */
