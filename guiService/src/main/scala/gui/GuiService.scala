/*package de.htwg.poker.gui

import akka.actor.ActorSystem
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object GUIService {
  @main def startGuiServer: Unit = {
    // Create the ActorSystem
    implicit val system: ActorSystem = ActorSystem("PokerGUIService")
    // Use the system's dispatcher as ExecutionContext
    implicit val ec: ExecutionContext = system.dispatcher

    // Explicitly pass both parameters to avoid ambiguity
    GUIHttpServer.run(system, ec)

    // Keep the server running
    Await.result(Future.never, Duration.Inf)
  }
} */
