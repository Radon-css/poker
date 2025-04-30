package de.htwg.poker.tui

import akka.actor.ActorSystem
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object TUIService {
  @main def startTuiServer: Unit = {
    implicit val system: ActorSystem = ActorSystem("PokerTUIService")
    implicit val ec: ExecutionContext = system.dispatcher

    TUIHttpServer.run(system, ec, port = 8082)

    Await.result(Future.never, Duration.Inf)
  }
}
