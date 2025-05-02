package de.htwg.poker.db.dbImpl

import de.htwg.poker.db.dbImpl.DAOInterface
import de.htwg.poker.db.dbImpl.slickImpl.SlickDb
import de.htwg.poker.db.dbImpl.slickImpl.PostgresConnector

object InjectDbImpl:
  given DAOInterface = SlickDb(new PostgresConnector)