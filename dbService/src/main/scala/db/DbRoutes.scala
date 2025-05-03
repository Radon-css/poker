package de.htwg.poker.db

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.db.dbImpl.InjectDbImpl.given_DAOInterface as daoInterface
import de.htwg.poker.db.dbImpl.slickImpl.SlickDb
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class DbRoutes {

  case class PlayerIdRequest(playerID: String)
  case class BalanceUpdateRequest(playerID: String, balance: Int)

  val routes: Route =
    pathPrefix("db") {
      concat(
        path("insertPlayer") {
          post {
            entity(as[String]) { body =>
              decode[PlayerIdRequest](body) match {
                case Right(PlayerIdRequest(playerID)) =>
                  daoInterface.insertPlayer(playerID)
                  complete(HttpEntity(ContentTypes.`application/json`, s"""{"status":"Player $playerID inserted"}"""))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        path("updateBalance") {
          post {
            entity(as[String]) { body =>
              decode[BalanceUpdateRequest](body) match {
                case Right(BalanceUpdateRequest(playerID, balance)) =>
                  daoInterface.updateBalance(playerID, balance)
                  complete(HttpEntity(ContentTypes.`application/json`, s"""{"status":"Balance updated for $playerID"}"""))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        path("fetchBalance") {
          post {
            entity(as[String]) { body =>
              decode[PlayerIdRequest](body) match {
                case Right(PlayerIdRequest(playerID)) =>
                  val balance = daoInterface.fetchBalance(playerID)
                  complete(HttpEntity(ContentTypes.`application/json`, s"""{"playerID":"$playerID", "balance":$balance}"""))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
