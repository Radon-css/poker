package de.htwg.poker.db.dbImpl

import de.htwg.poker.db.types.DbGameState
import scala.concurrent.Future
import scala.util.Try

trait DAOInterface:
  def insertPlayer(playerId: String): Try[Int]
  def updateBalance(playerId: String, balance: Int): Try[Int]
  def fetchBalance(playerId: String): Future[Int]
  def updateName(playerId: String, name: String): Future[Int]
  def fetchName(playerId: String): Try[String]

  def insertGameState(gameId: String, gameState: DbGameState, step: Long): Try[Int]
  def fetchGameHistory(gameId: String): Future[Seq[(Int, String)]]
