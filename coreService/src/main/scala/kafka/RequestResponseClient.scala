package de.htwg.poker.kafka

import java.util.UUID
import scala.concurrent.{Future, Promise}
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.circe._
import io.circe.parser._
import io.circe.syntax._

object RequestResponseClient {
  private val pending = TrieMap[String, Promise[Json]]()

  def sendRequest(requestType: String, payload: Json, topic: String)
                 (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext): Future[Json] = {
    val correlationId = UUID.randomUUID().toString
    val replyTo = "core-responses"

    val message = Json.obj(
      "type" -> Json.fromString(requestType),
      "correlationId" -> Json.fromString(correlationId),
      "replyTo" -> Json.fromString(replyTo),
      "payload" -> payload
    )

    val promise = Promise[Json]()
    pending.put(correlationId, promise)

    ProducerWrapper.send(topic, correlationId, message.noSpaces)
    promise.future
  }

  def startResponseListener()(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext): Unit = {
    GenericConsumer.start("core-responses", "core-response-group") { msg =>
      parse(msg).toOption.foreach { json =>
        val cursor = json.hcursor
        val correlationId = cursor.get[String]("correlationId").getOrElse("")
        pending.remove(correlationId).foreach(_.success(cursor.downField("payload").focus.get))
      }
    }
  }
}