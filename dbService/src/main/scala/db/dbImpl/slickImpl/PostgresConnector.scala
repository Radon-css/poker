package de.htwg.poker.db.dbImpl.slickImpl

import DatabaseConfig._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.{Database, JdbcDatabaseDef}
import de.htwg.poker.db.dbImpl.slickImpl.ConnectorInterface

class PostgresConnector extends ConnectorInterface:
  override val db = Database.forURL(
    url = DB_POSTGRES_URL,
    user = DB_POSTGRES_USER,
    password = DB_POSTGRES_PASS,
    driver = DB_POSTGRES_DRIVER
  )

  override def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    println("Db Service -- Connecting to postgres database...")
    retry(5, setup)(db)

  private def retry(retries: Int, setup: DBIOAction[Unit, NoStream, Effect.Schema])(database: => JdbcDatabaseDef): Unit =
    Try(Await.result(database.run(setup), 5.seconds)) match
      case Success(_) => println("Db Service -- Postgres database connection established")
      case Failure(exception) if retries > 0 =>
        println(s"Db Service -- Postgres database connection failed - retrying... (${5 - retries + 1}/ 5): ${exception.getMessage}")
        Thread.sleep(2000)
        retry(retries - 1, setup)(database)
      case Failure(exception) => println(s"Db Service -- Could not establish a connection to the postgres database: ${exception.getMessage}")

  override def disconnect: Unit =
    println("Db Service -- Closing postgres database connection...")
    db.close
