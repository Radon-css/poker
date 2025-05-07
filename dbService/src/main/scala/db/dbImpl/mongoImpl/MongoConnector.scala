package de.htwg.poker.db.dbImpl.mongoImpl

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import DatabaseConfig._
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import de.htwg.poker.db.dbImpl.ConnectorInterface

class MongoDBConnector extends DBConnectorInterface:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-mongoDB-connection") { () =>
    disconnect
  }

  private lazy val client: MongoClient =
    println("Db Service - Creating MongoDB client object...")
    MongoClient(DB_MONGO_URL)

  override val db: MongoDatabase = client.getDatabase(DB_MONGO_DATABASE_NAME)

  override def connect: Unit =
    println("Db Service - Connecting to MongoDB...")
    retry(DB_MONGO_CONN_RETRY_ATTEMPTS)

  private def retry(retries: Int): Unit =
    Try {
      Await.result(db.runCommand(Document("ping" -> 1)).toFuture(), 5.seconds)
    } match
      case Success(_) => println("Db Service - MongoDB connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Db Service - MongoDB connection failed - retrying... (${DB_MONGO_CONN_RETRY_ATTEMPTS - retries + 1}/$DB_MONGO_CONN_RETRY_ATTEMPTS): ${exception.getMessage}")
        Thread.sleep(DB_MONGO_CONN_RETRY_WAIT_TIME)
        retry(retries - 1)
      case Failure(exception) => println(s"Db Service - Could not establish a connection to MongoDB: ${exception.getMessage}")

  override def disconnect: Future[Done] =
    println("Db Service - Closing MongoDB connection...")
    client.close()
    Future.successful(Done)