package de.htwg.poker.kafka

import com.typesafe.config.{Config, ConfigFactory}

object KafkaConfig:
  private val config: Config = ConfigFactory.load()

  val KAFKA_BOOTSTRAP_SERVER_ADDRESS  = bootstrapServerAddress

  val KAFKA_METRIC_MOVE_TOPIC         = "metric-move-topic"
  val KAFKA_METRIC_UPDATES_TOPIC      = "metric-updates-topic"
  val KAFKA_METRIC_GROUP_ID           = "metric-consumer-group"
  val KAFKA_METRIC_FETCH_GROUP_ID     = "metric-fetch-"

  private def host: String =
    config.getString("kafka.host")

  private def port: Int =
    config.getInt("kafka.port")

  private def bootstrapServerAddress: String =
    s"$host:$port"