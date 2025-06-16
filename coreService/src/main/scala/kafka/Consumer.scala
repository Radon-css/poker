package de.htwg.poker.kafka

import akka.actor.ActorSystemAdd
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import common.config.KafkaConfig.{KAFKA_BOOTSTRAP_SERVER_ADDRESS, KAFKA_METRIC_UPDATES_TOPIC, KAFKA_METRIC_FETCH_GROUP_ID}
import io.circe.generic.auto._
import io.circe.parser.decode
import java.util.UUID
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContextExecutor, Future, Await}
import scala.concurrent.duration._

object KafkaConsumer:
  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  private def consumerSettings(groupId: String): ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(KAFKA_BOOTSTRAP_SERVER_ADDRESS)
      .withGroupId(groupId)

  def fetchCurrentGameStats: GameStats =
    val groupId = KAFKA_METRIC_FETCH_GROUP_ID.concat(UUID.randomUUID.toString)
    val futureResult = Consumer
      .plainSource(consumerSettings(groupId), Subscriptions.topics(KAFKA_METRIC_UPDATES_TOPIC))
      .filter(_.key == "stats")
      .runWith(Sink.headOption)
      .map {
        case Some(record) =>
          decode[GameStats](record.value) match
            case Right(gameStats) => Some(gameStats)
            case Left(err) =>
              logger.error(s"Failed to parse GameStats: ${err.getMessage}")
              None
        case None =>
          logger.warn("No current GameStats entry found.")
          None
      }

    Await.result(futureResult, 20.seconds) match
      case Some(gameStats) => gameStats
      case None            => throw new RuntimeException("No valid GameStats found.")