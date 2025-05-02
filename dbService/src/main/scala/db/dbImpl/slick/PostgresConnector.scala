package de.htwg.poker.db.dbImpl.slick

import DatabaseConfig._
import de.htwg.poker.db.ConnectorInterface
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.{Database, JdbcDatabaseDef}

class PostgresConnector extends ConnectorInterface:
  override val db = Database.forURL(
    url = DB_POSTGRES_URL,
    user = DB_POSTGRES_USER,
    password = DB_POSTGRES_PASS,
    driver = DB_POSTGRES_DRIVER
  )

  override def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    logger.info("Db Service -- Connecting to postgres database...")
    retry(DB_POSTGRES_CONN_RETRY_ATTEMPTS, setup)(db)

  private def retry(retries: Int, setup: DBIOAction[Unit, NoStream, Effect.Schema])(database: => JdbcDatabaseDef): Unit =
    Try(Await.result(database.run(setup), 5.seconds)) match
      case Success(_) => logger.info("Db Service -- Postgres database connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Db Service -- Postgres database connection failed - retrying... (${DB_POSTGRES_CONN_RETRY_ATTEMPTS - retries + 1}/$DB_POSTGRES_CONN_RETRY_ATTEMPTS): ${exception.getMessage}")
        Thread.sleep(DB_POSTGRES_CONN_RETRY_WAIT_TIME)
        retry(retries - 1, setup)(database)
      case Failure(exception) => logger.error(s"Db Service -- Could not establish a connection to the postgres database: ${exception.getMessage}")

  override def disconnect: Unit =
    logger.info("Db Service -- Closing postgres database connection...")
    db.close
