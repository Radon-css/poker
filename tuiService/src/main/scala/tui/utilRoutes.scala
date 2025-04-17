package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.util.UpdateBoard
import de.htwg.poker.model.GameState
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

trait GameStateJsonSupport extends DefaultJsonProtocol {
  implicit val cardFormat: RootJsonFormat[Card] = jsonFormat2(Card.apply)
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat4(Player.apply)
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat11(
    GameState.apply
  )
}

object PokerRoutes extends GameStateJsonSupport {

  val routes: Route =
    pathPrefix("poker") {
      concat(
        // GUIVIEW routes
        path("render") {
          get {
            complete {
              GUIView.render()
            }
          }
        },
        // TUIVIEW routes
        path("update") {
          get {
            complete {
              TUIView.update()
            }
          }
        }
        //
      )
    }
}
