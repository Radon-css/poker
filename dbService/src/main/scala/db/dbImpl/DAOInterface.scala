package de.htwg.poker.db.dbImpl

import scala.concurrent.Future
import scala.util.Try

trait DAOInterface:
  def insertNewPlayer(playerId: String): Try[Unit]
  def updateBalance(playerId: String, balance: Int): Try[Unit]
  def fetchBalance(playerId: String): Try[Int]