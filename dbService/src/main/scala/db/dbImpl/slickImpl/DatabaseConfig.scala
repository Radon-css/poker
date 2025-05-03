package de.htwg.poker.db.dbImpl.slickImpl

object DatabaseConfig {
  val DB_POSTGRES_URL: String = "jdbc:postgresql://db:5432/pokerdb"
  //val DB_POSTGRES_USER: String = sys.env("POSTGRES_USER")
  val DB_POSTGRES_USER: String = "pokeruser"
  //val DB_POSTGRES_PASS: String = sys.env("POSTGRES_PASSWORD")
  val DB_POSTGRES_PASS: String = "pokerpassword"
  val DB_POSTGRES_DRIVER: String = "org.postgresql.Driver"
}
