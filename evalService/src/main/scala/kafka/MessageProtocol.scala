package de.htwg.poker.kafka

// MessageProtocol.scala
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._

case class KafkaMessage(id: String, action: String, payload: String, replyTo: String)
object KafkaMessage {
  implicit val format: Decoder[KafkaMessage] = deriveDecoder
  implicit val encoder: Encoder[KafkaMessage] = deriveEncoder
}

def toKafkaMessage[T: Encoder](id: String, action: String, data: T, replyTo: String): KafkaMessage =
  KafkaMessage(id, action, data.asJson.noSpaces, replyTo)

def decodePayload[T: Decoder](payload: String): Either[Error, T] =
  decode[T](payload)