package de.htwg.poker.kafka

// DbKafkaWorker.scala
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import de.htwg.poker.kafka.KafkaMessage
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import de.htwg.poker.db.dbImpl.InjectDbImpl.given_DAOInterface as daoInterface
import de.htwg.poker.db.types.DbGameState


// Datenmodelle
case class PlayerIdRequest(playerID: String)
case class BalanceUpdateRequest(playerID: String, balance: Int)
case class NameUpdateRequest(playerID: String, name: String)
case class GameStateRequest(gameId: String, gameState: DbGameState, step: Long)

class DbKafkaWorker(
)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  val bootstrapServers = "localhost:9092"
  val requestTopic = "core-db-requests"

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServers)
    .withGroupId("db-worker-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)

  def run(): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(requestTopic))
      .mapAsync(1) { msg =>
        decode[KafkaMessage](msg.value()) match {
          case Right(req) => handleRequest(req)
          case Left(err) =>
            system.log.error(s"Failed to parse KafkaMessage: ${err.getMessage}")
            Future.unit
        }
      }
      .runWith(Sink.ignore)
  }

  def handleRequest(req: KafkaMessage): Future[Unit] = {
    println(s"Received request: ${req.action} with payload: ${req.payload}")
    req.action match {
        case "insertPlayer" =>
        decode[PlayerIdRequest](req.payload) match {
            case Right(PlayerIdRequest(playerID)) =>
            Future.fromTry(daoInterface.insertPlayer(playerID))
                .flatMap(response => sendResponse(req.id, response.asJson.noSpaces, req.replyTo))
                .recoverWith(handleError(req))
            case Left(err) =>
            Future.failed(new Exception(s"Invalid payload for insertPlayer: $err"))
        }

        case "updateBalance" =>
        decode[BalanceUpdateRequest](req.payload) match {
            case Right(BalanceUpdateRequest(playerID, balance)) =>
            Future.fromTry(daoInterface.updateBalance(playerID, balance))
                .flatMap(response => sendResponse(req.id, response.asJson.noSpaces, req.replyTo))
                .recoverWith(handleError(req))
            case Left(err) =>
            Future.failed(new Exception(s"Invalid payload for updateBalance: $err"))
        }

        case "updateName" =>
        decode[NameUpdateRequest](req.payload) match {
            case Right(NameUpdateRequest(playerID, name)) =>
            daoInterface.updateName(playerID, name)
                .flatMap(response => sendResponse(req.id, response.asJson.noSpaces, req.replyTo))
                .recoverWith(handleError(req))
            case Left(err) =>
            Future.failed(new Exception(s"Invalid payload for updateName: $err"))
        }

        case "fetchName" =>
        decode[PlayerIdRequest](req.payload) match {
            case Right(PlayerIdRequest(playerID)) =>
            Future.fromTry(daoInterface.fetchName(playerID))
                .flatMap(response => sendResponse(req.id, response.asJson.noSpaces, req.replyTo))
                .recoverWith(handleError(req))
            case Left(err) =>
            Future.failed(new Exception(s"Invalid payload for fetchName: $err"))
        }

        case "insertGameState" =>
        decode[GameStateRequest](req.payload) match {
            case Right(GameStateRequest(gameId, gameState, step)) =>
            Future.fromTry(daoInterface.insertGameState(gameId, gameState, step))
                .flatMap(response => sendResponse(req.id, response.asJson.noSpaces, req.replyTo))
                .recoverWith(handleError(req))
            case Left(err) =>
            Future.failed(new Exception(s"Invalid payload for insertGameState: $err"))
        }

        case unknown =>
        system.log.warning(s"Unknown action received: $unknown")
        Future.unit
        }
    }


  def sendResponse(id: String, payload: String, replyTo: String): Future[Unit] = {
    val response = KafkaMessage(id, action = "", payload = payload, replyTo = "")
    val record = new ProducerRecord[String, String](replyTo, response.asJson.noSpaces)
    Source.single(record).runWith(Producer.plainSink(producerSettings)).map(_ => ())
  }

  def handleError(req: KafkaMessage): PartialFunction[Throwable, Future[Unit]] = {
    case ex =>
      system.log.error(s"Error processing ${req.action}: ${ex.getMessage}")
      val errorResponse = KafkaMessage(req.id, "", s"""{"error":"${ex.getMessage}"}""", "")
      val record = new ProducerRecord[String, String](req.replyTo, errorResponse.asJson.noSpaces)
      Source.single(record).runWith(Producer.plainSink(producerSettings)).map(_ => ())
  }
}
