package de.htwg.poker.db.dbImpl.slickImpl

import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

class PlayerTable(tag: Tag) extends Table[(Int, String, Int)](tag, "players"):
  def id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def playerId = column[String]("player_id", O.Unique)
  def balance  = column[Int]("balance")

  override def * = (id, playerId, balance)
