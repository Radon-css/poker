package de.htwg.poker.kafka

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
import de.htwg.poker.eval.types.{EvalGameState, EvalPlayer, EvalCard, EvalHandRequest}
import de.htwg.poker.eval.{HandInfo, Evaluator}

class EvalKafkaWorker(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("eval-worker")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  def run(): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics("core-eval-requests"))
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

  private def handleRequest(req: KafkaMessage): Future[Unit] = {
    req.action match {
      case "calcWinner" =>
        decode[EvalGameState](req.payload) match {
          case Right(gameState) =>
            val players = gameState.players.get
            val boardCards = gameState.board
            val winners = Evaluator.calcWinner(players, boardCards) // List[EvalPlayer]
            sendResponse(req.id, winners.asJson.noSpaces, req.replyTo)
          case Left(err) =>
            sendError(req, s"Invalid payload for calcWinner: ${err.getMessage}")
        }

      case "evalHand" =>
        decode[EvalHandRequest](req.payload) match {
          case Right(EvalHandRequest(gameState, player)) =>
            val playerCards = List(gameState.players.get(player).card1, gameState.players.get(player).card2)
            val boardCards = gameState.board
            val result = HandInfo.evalHand(playerCards, boardCards) // String oder Json
            sendResponse(req.id, result.asJson.noSpaces, req.replyTo)
          case Left(err) =>
            sendError(req, s"Invalid payload for evalHand: ${err.getMessage}")
        }

      case unknown =>
        system.log.warning(s"Unknown action received: $unknown")
        Future.unit
    }
  }

  private def sendResponse(id: String, payload: String, replyTo: String): Future[Unit] = {
    val response = KafkaMessage(id, action = "", payload = payload, replyTo = "")
    val record = new ProducerRecord[String, String](replyTo, response.asJson.noSpaces)
    Source.single(record).runWith(Producer.plainSink(producerSettings)).map(_ => ())
  }

  private def sendError(req: KafkaMessage, message: String): Future[Unit] = {
    system.log.error(message)
    val errorPayload = s"""{"error":"$message"}"""
    val errorResponse = KafkaMessage(req.id, action = "", payload = errorPayload, replyTo = "")
    val record = new ProducerRecord[String, String](req.replyTo, errorResponse.asJson.noSpaces)
    Source.single(record).runWith(Producer.plainSink(producerSettings)).map(_ => ())
  }
}
