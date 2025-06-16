package de.htwg.poker.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

object ProducerWrapper {
  def send(topic: String, key: String, value: String)(implicit system: ActorSystem, mat: Materializer): Future[akka.Done] = {
    val settings = ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withProperties(KafkaConfig.producerSettings)

    Source.single(new ProducerRecord[String, String](topic, key, value))
      .runWith(Producer.plainSink(settings))
  }
}
