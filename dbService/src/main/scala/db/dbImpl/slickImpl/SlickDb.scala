package de.htwg.poker.db.dbImpl.slickImpl

import de.htwg.poker.db.dbImpl.slickImpl.ConnectorInterface
import de.htwg.poker.db.dbImpl.DAOInterface
import org.slf4j.LoggerFactory
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

object SlickDb:
  def apply(dbConnector: ConnectorInterface): DAOInterface = new Slickb(dbConnector)

  private class Slickb(dbConnector: ConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)
    private def playerTable = TableQuery[PlayerTable](new PlayerTable(_))

    override def insertPlayer(playerId: String): Try[Int] = Try {
      val action = playerTable += (0, playerId, 100000, "Guest")
      Await.result(dbConnector.db.run(action), 5.seconds)
    }

    override def updateBalance(playerId: String, balance: Int): Try[Int] = Try {
      val query = playerTable.filter(_.playerId === playerId).map(_.balance)
      val futureCurrentBalance = dbConnector.db.run(query.result.headOption)

      val currentBalance = Await.result(futureCurrentBalance, 5.seconds) match {
        case Some(b) => b
        case None    => throw new NoSuchElementException(s"Player $playerId not found")
      }

      val newBalance = currentBalance + balance
      val updateAction = query.update(newBalance)
      Await.result(dbConnector.db.run(updateAction), 5.seconds)
    }

    override def fetchBalance(playerId: String): Try[Int] = Try {
      val action = playerTable.filter(_.playerId === playerId).map(_.balance).result.headOption
      Await.result(dbConnector.db.run(action), 5.seconds) match
        case Some(balance) => balance
        case None          => throw new NoSuchElementException(s"Player $playerId not found")
    }

    override def updateName(playerId: String, name: String): Try[Int] = Try {
      println(s"Updating name for player $playerId to $name")
      val action = playerTable.filter(_.playerId === playerId).map(_.name).update(name)
      Await.result(dbConnector.db.run(action), 5.seconds)
    }

    override def fetchName(playerId: String): Try[String] = Try {
      val action = playerTable.filter(_.playerId === playerId).map(_.name).result.headOption
      Await.result(dbConnector.db.run(action), 5.seconds) match
        case Some(name) => name
        case None       => throw new NoSuchElementException(s"Player $playerId not found")
    }
