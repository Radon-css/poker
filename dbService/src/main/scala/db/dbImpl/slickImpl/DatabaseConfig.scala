package de.htwg.poker.db.dbImpl.slickImpl

object DatabaseConfig {
  val DB_POSTGRES_URL: String = "jdbc:postgresql://ep-dry-dust-a2v3ufk8.eu-central-1.pg.koyeb.app/koyebdb?user=koyeb-adm&password=npg_W3Ky7hxguGVQ"
  //val DB_POSTGRES_USER: String = sys.env("POSTGRES_USER")
  val DB_POSTGRES_USER: String = "koyeb-adm"
  //val DB_POSTGRES_PASS: String = sys.env("POSTGRES_PASSWORD")
  val DB_POSTGRES_PASS: String = "npg_W3Ky7hxguGVQ"
  val DB_POSTGRES_DRIVER: String = "org.postgresql.Driver"
}
