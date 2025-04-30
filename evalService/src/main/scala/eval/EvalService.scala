package de.htwg.poker.eval

import akka.actor.ActorSystem
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object EvalService {
  @main def startEvalServer: Unit = {
    implicit val system: ActorSystem = ActorSystem("PokerEvalService")
    implicit val ec: ExecutionContext = system.dispatcher

    // Start on port 8083 (different from other services)
    EvalHttpServer.run(system, ec, port = 8083)

    Await.result(Future.never, Duration.Inf)
  }
}
