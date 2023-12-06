package de.htwg.poker
package aview
import controller.Controller
import util.Observer
import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

class TUI(controller: Controller) extends Observer {
  controller.add(this)

  override def update: Unit = {
    println(controller.toString)
  }

  def gameLoop(): Unit = {

    while (true) {
      val input = readLine()
      processInput(input)
    }
  }

  def processInput(input: String): Boolean = {
    val inputList = input.split(" ").toList
    inputList(0) match {
      case "start" =>
        val result: Try[Boolean] = Try(
          controller.createGame(
            inputList.tail.dropRight(2),
            inputList.init.last,
            inputList.last
          )
        )
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "x" =>
        val result: Try[Boolean] = Try(
          controller.createGame(
            List("Henrik", "Julian", "Till", "Julian", "Dominik", "Luuk"),
            "10",
            "20"
          )
        )
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "bet" =>
        val result: Try[Boolean] = Try(controller.bet(inputList(1).toInt))
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "allin" =>
        val result: Try[Boolean] = Try(controller.allin())
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "fold" =>
        val result: Try[Boolean] = Try(controller.fold())
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "call" =>
        val result: Try[Boolean] = Try(controller.call())
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "check" =>
        val result: Try[Boolean] = Try(controller.check())
        result match {
          case Success(value)     => return true
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
        false
      case "q" =>
        sys.exit()
        true
      case "u" =>
        controller.undo
        true
      case "r" =>
        controller.redo
        true
      case _ =>
        println("invalid command")
        false
    }
  }
}
