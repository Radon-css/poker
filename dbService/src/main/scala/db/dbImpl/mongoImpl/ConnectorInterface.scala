package de.htwg.poker.db.dbImpl.mongoImpl

import akka.Done
import org.mongodb.scala.MongoDatabase
import scala.concurrent.Future

trait ConnectorInterface:
  val db: MongoDatabase
  def connect: Unit
  def disconnect: Future[Done]