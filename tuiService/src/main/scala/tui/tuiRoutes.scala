package de.htwg.poker.tui

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.util.UpdateBoard
import de.htwg.poker.model.GameState
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

object utilRoutes extends DefaultJsonProtocol {

  val routes: Route =
    pathPrefix("tui") {
      concat(
        // TUIVIEW routes
        path("getTUIView") {
          get {
            complete {
              TUIView.getView()
            }
          }
        }
        //
      )
    }
}
