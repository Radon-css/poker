package persistence.databaseComponent.mongoDB.base

import akka.Done
import org.mongodb.scala.MongoDatabase
import scala.concurrent.Future

trait DBConnectorInterface:
  val db: MongoDatabase
  def connect: Unit
  def disconnect: Future[Done]