package de.htwg.poker.db.dbImpl

import de.htwg.poker.db.dbImpl.DAOInterface
import de.htwg.poker.db.dbImpl.slickImpl.SlickDb
import de.htwg.poker.db.dbImpl.slickImpl.PostgresConnector
import de.htwg.poker.db.dbImpl.mongoImpl.MongoDb
import de.htwg.poker.db.dbImpl.slickImpl.MongoConnector

object InjectDbImpl:
  given DAOInterface = MongoDb(new MongoConnector)