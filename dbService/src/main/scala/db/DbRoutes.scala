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
                  onComplete(daoInterface.fetchBalance(playerID)) {
                    case scala.util.Success(amount) =>
                      val json = PlayerBalance(playerID, amount).asJson.noSpaces
                      complete(HttpEntity(ContentTypes.`application/json`, json))
                    case scala.util.Failure(e) =>
                      complete(StatusCodes.InternalServerError -> s"""{"error": "${e.getMessage}"}""")
                  }
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        path("updateName") {
          println("updateName route called")
          post {
            entity(as[String]) { body =>
              println(s"Received body: $body")
              decode[NameUpdateRequest](body) match {
                case Right(NameUpdateRequest(playerID, name)) =>
                  onComplete(daoInterface.updateName(playerID, name)) {
                    case scala.util.Success(_) =>
                      complete(HttpEntity(ContentTypes.`application/json`, s"""{"status":"Name updated for $playerID"}"""))
                    case scala.util.Failure(ex) =>
                      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Failed to update name: ${ex.getMessage}"))
                  }
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        },
        path("fetchName") {
          post {
            entity(as[String]) { body =>
              decode[PlayerIdRequest](body) match {
                case Right(PlayerIdRequest(playerID)) =>
                  daoInterface.fetchName(playerID) match {
                    case Success(name) =>
                      val json = PlayerName(playerID, name).asJson.noSpaces
                      complete(HttpEntity(ContentTypes.`application/json`, json))
                    case Failure(e) =>
                      complete(StatusCodes.InternalServerError -> s"""{"error": "${e.getMessage}"}""")
                  }
                case Left(error) =>
                  complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Invalid JSON: ${error.getMessage}"))
              }
            }
          }
        }
      )
    }
}
