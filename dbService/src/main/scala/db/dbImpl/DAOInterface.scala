package de.htwg.poker.db.dbImpl

import scala.concurrent.Future
import scala.util.Try

trait DAOInterface:
  def insertPlayer(playerId: String): Try[Int]
  def updateBalance(playerId: String, balance: Int): Try[Int]
  def fetchBalance(playerId: String): Try[Int]
  def updateName(playerId: String, name: String): Try[Int]
