package de.htwg.poker.db.dbImpl

import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.JdbcDatabaseDef

trait ConnectorInterface:
  val db: JdbcDatabaseDef
  def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit
  def disconnect: Unit