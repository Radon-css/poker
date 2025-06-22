package de.htwg.poker.eval

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import de.htwg.poker.kafka.EvalKafkaWorker
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object EvalHttpServer {

  def run(
      system: ActorSystem,
      ec: ExecutionContext,
      port: Int = 8083
  ): Future[ServerBinding] = {
    val routes = new evalRoutes().routes
    val host = "0.0.0.0"

    implicit val actorSystem: ActorSystem = system
    implicit val classicSystem: ClassicActorSystemProvider = system
    implicit val executionContext: ExecutionContext = ec

    val kafkaWorker = new EvalKafkaWorker()
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
        println(s"Eval Service running at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        println("Eval Service failed to start" + ex.getMessage)
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
