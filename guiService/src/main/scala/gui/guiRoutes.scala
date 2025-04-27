// GuiRoutes.scala
package de.htwg.poker.gui

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
                  (data("gameState").as[GUIGameState], data("handEval").as[String]) match {
                    case (Right(gameState), Right(handEval)) =>
                      val result = GUIView.getView(handEval, gameState)
                      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result))
                    case (Left(gameStateError), _) =>
                      complete(StatusCodes.BadRequest -> s"Invalid gameState: ${gameStateError.getMessage}")
                    case (_, Left(handEvalError)) =>
                      complete(StatusCodes.BadRequest -> s"Invalid handEval: ${handEvalError.getMessage}")
                  }
                case Left(error) =>
                  complete(StatusCodes.BadRequest -> s"Invalid JSON: ${error.getMessage}")
              }
            }
          }
        }
      )
    }
}
