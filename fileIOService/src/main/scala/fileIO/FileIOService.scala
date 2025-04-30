package de.htwg.poker.fileIO

import akka.actor.ActorSystem
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object FileIOService {
  @main def startFileIOServer: Unit = {
    implicit val system: ActorSystem = ActorSystem("PokerFileIOService")
    implicit val ec: ExecutionContext = system.dispatcher

    FileIOHttpServer.run(system, ec, port = 8084)

    Await.result(Future.never, Duration.Inf)
  }
}
