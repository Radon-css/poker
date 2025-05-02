package de.htwg.poker.db.dbImpl.slick

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class PlayerRow(id: Int, playerId: String, balance: Int)

class PlayerTable(tag: Tag) extends Table[PlayerRow](tag, "players") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def playerId: Rep[String] = column[String]("player_id", O.Unique)
  def balance: Rep[Int] = column[Int]("balance")

  def * : ProvenShape[PlayerRow] = (id, playerId, balance) <> (PlayerRow.tupled, PlayerRow.unapply)
}
