package de.htwg.poker.db

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import de.htwg.poker.kafka.DbKafkaWorker
import akka.stream.Materializer

object DbHttpServer {
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run(
      system: ActorSystem,
      ec: ExecutionContext,
      port: Int = 8084
  ): Future[ServerBinding] = {
    val routes = new DbRoutes().routes
    val host = "0.0.0.0"

    implicit val actorSystem: ActorSystem = system
    implicit val classicSystem: ClassicActorSystemProvider = system
    implicit val materializer: Materializer = Materializer(classicSystem)
    implicit val executionContext: ExecutionContext = ec

    val kafkaWorker = new DbKafkaWorker()

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
        logger.info(s"Db Service running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        logger.error("Db Service failed to start", ex)
        system.terminate()
    }

    serverBinding
  }

  private def shutdown(
      serverBinding: Future[ServerBinding]
  )(implicit system: ActorSystem, ec: ExecutionContext): Future[Done] = {
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Shutting down Eval Service...")
        system.terminate()
        Done
      }
    }
  }
}
