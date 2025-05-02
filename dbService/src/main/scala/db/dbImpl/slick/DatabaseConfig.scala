package de.htwg.poker.db.dbImpl.slick

object DatabaseConfig {
  val DB_POSTGRES_URL: String = "jdbc:postgresql://db:5432/pokerdb"
  val DB_POSTGRES_USER: String = sys.env("POSTGRES_USER")
  val DB_POSTGRES_PASS: String = sys.env("POSTGRES_PASSWORD")
  val DB_POSTGRES_DRIVER: String = "org.postgresql.Driver"
  val DB_POSTGRES_CONN_RETRY_ATTEMPTS: Int = 5
  val DB_POSTGRES_CONN_RETRY_WAIT_TIME: Int = 2000 // milliseconds
}
