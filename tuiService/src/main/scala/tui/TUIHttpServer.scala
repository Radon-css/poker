package de.htwg.poker.tui

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object TUIHttpServer {
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run(
      system: ActorSystem,
      ec: ExecutionContext,
      port: Int = 8082
  ): Future[ServerBinding] = {
    val routes = new tuiRoutes().routes
    val host = "0.0.0.0"

    implicit val classicSystem: ClassicActorSystemProvider = system
    implicit val executionContext: ExecutionContext = ec

    val serverBinding = Http()
      .newServerAt(host, port)
      .bind(routes)

    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseServiceStop,
      "shutdown-server"
    ) { () =>
      shutdown(serverBinding)(system, ec)
    }

    serverBinding.onComplete {
      case Success(binding) =>
        logger.info(s"TUI Service running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        logger.error("TUI Service failed to start", ex)
        system.terminate()
    }

    serverBinding
  }

  private def shutdown(
      serverBinding: Future[ServerBinding]
  )(implicit system: ActorSystem, ec: ExecutionContext): Future[Done] = {
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Shutting down TUI Service...")
        system.terminate()
        Done
      }
    }
  }
}
