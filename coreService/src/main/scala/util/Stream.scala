package de.htwg.poker.util

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import de.htwg.poker.model.GameState
import io.circe.syntax._
import io.circe.generic.auto._
import de.htwg.poker.util.Observer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import de.htwg.poker.controllers.Controller
import de.htwg.poker.Client

class Stream(controller: Controller)(using system: ActorSystem) extends Observer:

  private given ExecutionContext = system.dispatcher

  // Stream als Observer beim Controller anmelden
  controller.add(this)

  override def update: Unit =
    val gameState = controller.gameState
    val gameId = controller.gameId

    println("Stream update")

    // JSON bauen
    val jsonString = Map(
      "gameId" -> gameId.asJson,
      "gameState" -> gameState.asJson,
      "step" -> System.currentTimeMillis().asJson
    ).asJson.noSpaces

    // Akka Stream aus JSON-String (optional, wenn du Insert als Stream verarbeiten willst)
    val source: Source[String, NotUsed] = Source.single(jsonString)

    val dbFlow: Flow[String, Try[Unit], NotUsed] = Flow[String].mapAsync(1) { json =>
      // Client.insertGameState erwartet JSON-String direkt
      Client.insertGameState(json).transform {
        case Success(_) => Success(Success(()))
        case Failure(ex) =>
          println(s"DB error: ${ex.getMessage}")
          Success(Failure(ex))
      }
    }

    val sink: Sink[Try[Unit], Future[Unit]] =
      Sink.head.mapMaterializedValue(_.map {
        case Success(_)  => println("Successfully saved GameState")
        case Failure(ex) => println(s"Error saving GameState: ${ex.getMessage}")
      })

    val graph: RunnableGraph[Future[Unit]] = source.via(dbFlow).toMat(sink)(Keep.right)

    graph.run()
