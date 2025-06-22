package de.htwg.poker.db.dbImpl.mongoImpl

import DatabaseConfig._
import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import de.htwg.poker.db.dbImpl.mongoImpl.ConnectorInterface
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase, ObservableFuture}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class MongoConnector extends ConnectorInterface:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)

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

  private def retry(retries: Int): Unit = {
    Try {
      val result = Await.result(db.runCommand(Document("ping" -> 1)).toFuture(), 5.seconds)
      println(s"Ping result: $result")
      result
    } match {
      case Success(_) =>
        println("Db Service - MongoDB connection established")
      case Failure(exception) if retries > 0 =>
        println(s"Db Service - MongoDB connection failed - retrying... (${5 - retries + 1}/5): ${exception.getMessage}")
        Thread.sleep(1000)
        retry(retries - 1)
      case Failure(exception) =>
        println(s"Db Service - Could not establish a connection to MongoDB: ${exception.getMessage}" + exception.getMessage)
    }
  }

  override def disconnect: Future[Done] =
    println("Db Service - Closing MongoDB connection...")
    client.close()
    Future.successful(Done)
