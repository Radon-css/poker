package de.htwg.poker.db

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.htwg.poker.db.dbImpl.InjectDbImpl.given_DAOInterface as daoInterface
import de.htwg.poker.db.dbImpl.slickImpl.SlickDb
import de.htwg.poker.db.types.DbGameState
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._
import scala.util.{Failure, Success}

class DbRoutes {

  import io.circe.{Decoder, Encoder}

  case class PlayerIdRequest(playerID: String)
  object PlayerIdRequest {
    implicit val encoder: Encoder[PlayerIdRequest] = Encoder.forProduct1("playerID")(p => p.playerID)
    implicit val decoder: Decoder[PlayerIdRequest] = Decoder.forProduct1("playerID")(PlayerIdRequest.apply)
  }

  case class BalanceUpdateRequest(playerID: String, balance: Int)
  object BalanceUpdateRequest {
    implicit val encoder: Encoder[BalanceUpdateRequest] = Encoder.forProduct2("playerID", "balance")(b => (b.playerID, b.balance))
    implicit val decoder: Decoder[BalanceUpdateRequest] = Decoder.forProduct2("playerID", "balance")(BalanceUpdateRequest.apply)
  }

  case class PlayerBalance(playerID: String, balance: Int)
  object PlayerBalance {
    implicit val encoder: Encoder[PlayerBalance] = Encoder.forProduct2("playerID", "balance")(p => (p.playerID, p.balance))
    implicit val decoder: Decoder[PlayerBalance] = Decoder.forProduct2("playerID", "balance")(PlayerBalance.apply)
  }

  case class NameUpdateRequest(playerID: String, name: String)
  object NameUpdateRequest {
    implicit val encoder: Encoder[NameUpdateRequest] = Encoder.forProduct2("playerID", "name")(n => (n.playerID, n.name))
    implicit val decoder: Decoder[NameUpdateRequest] = Decoder.forProduct2("playerID", "name")(NameUpdateRequest.apply)
  }

  case class PlayerName(playerID: String, name: String)
  object PlayerName {
    implicit val encoder: Encoder[PlayerName] = Encoder.forProduct2("playerID", "name")(p => (p.playerID, p.name))
    implicit val decoder: Decoder[PlayerName] = Decoder.forProduct2("playerID", "name")(PlayerName.apply)
  }

  case class GameStateRequest(gameId: String, gameState: DbGameState, step: Long)
  object GameStateRequest {
    implicit val encoder: Encoder[GameStateRequest] = Encoder.forProduct3("gameId", "gameState", "step")(g => (g.gameId, g.gameState, g.step))
    implicit val decoder: Decoder[GameStateRequest] = Decoder.forProduct3("gameId", "gameState", "step")(GameStateRequest.apply)
  }

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
        },
        path("insertGameState") {
          post {
            entity(as[String]) { body =>
              println(s"Received insertGameState body: $body") // Debug print
              decode[GameStateRequest](body) match {
                case Right(GameStateRequest(gameId, gameState, step)) =>
                  daoInterface.insertGameState(gameId, gameState, step) match {
                    case Success(inserted) =>
                      complete(HttpEntity(ContentTypes.`application/json`, s"""{"status": "ok", "rows": $inserted}"""))
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
