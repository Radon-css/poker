package de.htwg.poker.db.dbImpl.slickImpl

import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import de.htwg.poker.db.dbImpl.ConnectorInterface
import de.htwg.poker.db.dbImpl.DAOInterface

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try}
import scala.concurrent.Future

object SlickDb:
  def apply(dbConnector: ConnectorInterface): DAOInterface = new Slickb(dbConnector)

  private class Slickb(dbConnector: ConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)
    private def playerTable = TableQuery[PlayerTable](new PlayerTable(_))

    override def insertPlayer(playerId: String): Try[Int] = Try {
      val action = playerTable += (0, playerId, 100000)
      Await.result(dbConnector.db.run(action), 5.seconds)
    }

    override def updateBalance(playerId: String, balance: Int): Try[Int] = Try {
      val action = playerTable.filter(_.playerId === playerId).map(_.balance).update(balance)
      Await.result(dbConnector.db.run(action), 5.seconds)
    }

    override def fetchBalance(playerId: String): Try[Int] = Try {
      val action = playerTable.filter(_.playerId === playerId).map(_.balance).result.headOption
      Await.result(dbConnector.db.run(action), 5.seconds) match
        case Some(balance) => balance
        case None => throw new NoSuchElementException(s"Player $playerId not found")
    }

