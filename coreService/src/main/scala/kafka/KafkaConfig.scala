package de.htwg.poker.kafka

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig

object KafkaConfig {
  val bootstrapServers = "localhost:9092"

  def producerSettings: Map[String, Object] = Map(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer]
  )

  def consumerSettings(groupId: String): Map[String, Object] = Map(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.GROUP_ID_CONFIG -> groupId,
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )
}

