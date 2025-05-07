package de.htwg.poker.db.dbImpl.mongoImpl

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase, ObservableFuture}
import DatabaseConfig._
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import de.htwg.poker.db.dbImpl.ConnectorInterface

class MongoConnector extends ConnectorInterface:
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
    retry(5)

  private def retry(retries: Int): Unit =
    Try {
      Await.result(db.runCommand(Document("ping" -> 1)).toFuture(), 5.seconds)
    } match
      case Success(_) => println("Db Service - MongoDB connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Db Service - MongoDB connection failed - retrying... (${5 - retries + 1}/5): ${exception.getMessage}")
        Thread.sleep(1000)
        retry(retries - 1)
      case Failure(exception) => println(s"Db Service - Could not establish a connection to MongoDB: ${exception.getMessage}")

  override def disconnect: Future[Done] =
    println("Db Service - Closing MongoDB connection...")
    client.close()
    Future.successful(Done)