/*package de.htwg.poker.gui

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GUIHttpServer {
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run(
      system: ActorSystem,
      ec: ExecutionContext
  ): Future[ServerBinding] = {
    val routes = new GuiRoutes().routes
    val port = 8081
    val host = "localhost"

    // The critical fix - provide the system as a ClassicActorSystemProvider
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
        logger.info(s"GUI Service running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        logger.error("GUI Service failed to start", ex)
        system.terminate()
    }

    serverBinding
  }

  private def shutdown(
      serverBinding: Future[ServerBinding]
  )(implicit system: ActorSystem, ec: ExecutionContext): Future[Done] = {
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Shutting down GUI Service...")
        system.terminate()
        Done
      }
    }
  }
} */
