package de.htwg.poker.db.dbImpl.mongoImpl

import org.mongodb.scala._
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.ReplaceOptions
import org.mongodb.scala.model.Updates._
import org.slf4j.LoggerFactory
import de.htwg.poker.db.dbImpl.mongoImpl.ConnectorInterface
import de.htwg.poker.db.dbImpl.DAOInterface
import de.htwg.poker.db.dbImpl.mongoImpl.DatabaseConfig._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

object MongoDb:
  def apply(dbConnector: ConnectorInterface): DAOInterface = new MongoDb(dbConnector)

  private class MongoDb(dbConnector: ConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    dbConnector.connect
    private val playerCollection: MongoCollection[Document] = dbConnector.db.getCollection(DB_MONGO_COLLECTION_NAME)

    override def insertPlayer(playerId: String): Try[Int] = Try {
      val doc = Document(
        "player_id" -> playerId,
        "balance"   -> 100000,
        "name"      -> "Guest"
      )
      Await.result(
        playerCollection.replaceOne(equal("player_id", playerId), doc, ReplaceOptions().upsert(true)).toFuture,
        5.seconds
      )
      1
    }

    override def updateBalance(playerId: String, balance: Int): Try[Int] = Try {
        Await.result(
            playerCollection.updateOne(
            equal("player_id", playerId),
            inc("balance", balance) // <- statt set(...) verwenden wir inc(...)
            ).toFuture,
            5.seconds
        )
        1
    }


    override def fetchBalance(playerId: String): Try[Int] = Try {
      val doc = Await.result(
        playerCollection.find(equal("player_id", playerId)).first().toFuture,
        5.seconds
      )
      doc.getInteger("balance")
    }

    override def updateName(playerId: String, name: String): Try[Int] = Try {
      Await.result(
        playerCollection.updateOne(equal("player_id", playerId), set("name", name)).toFuture,
        5.seconds
      )
      1
    }

    override def fetchName(playerId: String): Try[String] = Try {
      println(s"Fetching name for player $playerId")
      val doc = Await.result(
        playerCollection.find(equal("player_id", playerId)).first().toFuture,
        5.seconds
      )
      doc.getString("name")
    }
