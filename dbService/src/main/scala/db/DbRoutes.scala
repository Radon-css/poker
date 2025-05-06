package de.htwg.poker.db

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.db.dbImpl.InjectDbImpl.given_DAOInterface as daoInterface
import de.htwg.poker.db.dbImpl.slickImpl.SlickDb
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.util.{Failure, Success}

class DbRoutes {

  case class PlayerIdRequest(playerID: String)
  case class BalanceUpdateRequest(playerID: String, balance: Int)
  case class PlayerBalance(playerID: String, balance: Int)
  case class NameUpdateRequest(playerID: String, name: String)
  case class PlayerName(playerID: String, name: String)

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
                  daoInterface.fetchBalance(playerID) match {
                    case Success(amount) =>
                      val json = PlayerBalance(playerID, amount).asJson.noSpaces
                      complete(HttpEntity(ContentTypes.`application/json`, json))
                    case Failure(e) =>
                      complete(StatusCodes.InternalServerError -> s"""{"error": "${e.getMessage}"}""")
                  }
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        path("updateName") {
          post {
            entity(as[String]) { body =>
              decode[NameUpdateRequest](body) match {
                case Right(NameUpdateRequest(playerID, name)) =>
                  daoInterface.updateName(playerID, name)
                  complete(HttpEntity(ContentTypes.`application/json`, s"""{"status":"Name updated for $playerID"}"""))
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
