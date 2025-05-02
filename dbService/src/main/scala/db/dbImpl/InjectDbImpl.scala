package de.htwg.poker.db.dbImpl

import de.htwg.poker.db.dbImpl.DAOInterface
import de.htwg.poker.db.dbImpl.slick.SlickDb
import de.htwg.poker.db.dbImpl.slick.PostgresConnector

object InjectDbImpl:
  given DAOInterface = SlickDb(new PostgresConnector)