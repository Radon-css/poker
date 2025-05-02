package de.htwg.poker.db
import akka.actor.ActorSystem
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object DbService {
  @main def startDbServer: Unit = {
    implicit val system: ActorSystem = ActorSystem("PokerDbService")
    implicit val ec: ExecutionContext = system.dispatcher

    // Start on port 8084 (different from other services)
    DbHttpServer.run(system, ec, port = 8084)

    Await.result(Future.never, Duration.Inf)
  }
}
