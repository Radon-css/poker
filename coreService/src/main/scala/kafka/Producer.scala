package de.htwg.poker.kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.ProducerAdd
import akka.stream.scaladsl.Source
import common.config.KafkaConfig.KAFKA_BOOTSTRAP_SERVER_ADDRESS
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object KafkaProducer:
  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)

  private val producerSettings =
    ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(KAFKA_BOOTSTRAP_SERVER_ADDRESS)

  def send(record: ProducerRecord[String, String]): Future[Done] =
    Source.single(record).runWith(Producer.plainSink(producerSettings))

  def send(records: ListBuffer[ProducerRecord[String, String]]): Future[Done] =
    Source(records.toList).runWith(Producer.plainSink(producerSettings))