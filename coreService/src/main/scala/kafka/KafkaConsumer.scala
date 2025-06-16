package de.htwg.poker.kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContext

object GenericConsumer {
  def start(topic: String, groupId: String)(handle: String => Unit)
           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext): Unit = {

    val settings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withProperties(KafkaConfig.consumerSettings(groupId))

    Consumer
      .plainSource(settings, Subscriptions.topics(topic))
      .map(_.value())
      .runWith(Sink.foreach(handle))
  }
}
