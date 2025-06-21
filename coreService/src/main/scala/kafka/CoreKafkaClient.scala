package de.htwg.poker.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import io.circe.parser._
import io.circe.syntax._
import java.util.UUID
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import scala.collection.concurrent.TrieMap
import scala.concurrent.{Future, Promise}

class CoreKafkaClient(implicit system: ActorSystem, mat: Materializer) {
  import system.dispatcher

  val bootstrapServers = "localhost:9092"
  val responseTopic = "core-responses"

  val promises = TrieMap[String, Promise[String]]()

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServers)
    .withGroupId("core-response-listener")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)

  // Start response listener
  Consumer
    .plainSource(consumerSettings, Subscriptions.topics(responseTopic))
    .mapAsync(1) { msg =>
      decode[KafkaMessage](msg.value()) match {
        case Right(kafkaMsg) =>
          promises.remove(kafkaMsg.id).foreach(_.success(kafkaMsg.payload))
        case Left(_) =>
          system.log.warning(s"Invalid message received: ${msg.value()}")
      }
      Future.unit
    }
    .runWith(Sink.ignore)

  def sendAndAwait(action: String, payload: String, targetTopic: String): Future[String] = {
    println(s"Sending action: $action with payload: $payload to topic: $targetTopic")
    val id = UUID.randomUUID().toString
    val promise = Promise[String]()
    promises.put(id, promise)

    val message = Map(
      "id" -> id.asJson,
      "action" -> action.asJson,
      "payload" -> payload.asJson,
      "responseTopic" -> responseTopic.asJson
    ).asJson.noSpaces
    val record = new ProducerRecord[String, String](targetTopic, message)

    Source
      .single(record)
      .runWith(Producer.plainSink(producerSettings))

    promise.future
  }
}
