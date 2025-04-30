package de.htwg.poker

import akka.actor.ActorSystem
import akka.stream.Materializer

object AppConfig {
  // Create the ActorSystem and Materializer once
  implicit val system: ActorSystem = ActorSystem("PokerClientSystem")
  implicit val materializer: Materializer = Materializer(system)

  // Add shutdown hook
  sys.addShutdownHook {
    system.terminate()
  }
}
