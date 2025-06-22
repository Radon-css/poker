package de.htwg.poker.db

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import de.htwg.poker.kafka.DbKafkaWorker
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object DbHttpServer {

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
    kafkaWorker.run()

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
        println(s"Db Service running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        println("Db Service failed to start + " + ex.getMessage)
        system.terminate()
    }

    serverBinding
  }

  private def shutdown(
      serverBinding: Future[ServerBinding]
  )(implicit system: ActorSystem, ec: ExecutionContext): Future[Done] = {
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        println("Shutting down Eval Service...")
        system.terminate()
        Done
      }
    }
  }
}
