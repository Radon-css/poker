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
      /* start a game by typing start followed by the name of the players participating and finally the amount of the small Blind and big Blind
        (Blinds are the cost for the players to participate in the next round).
         for example, type "start player1 player2 player3 player4 10 20"*/
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
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // start a quick example game without inserting playernames and so on.
      case "x" =>
        val result: Try[Boolean] = Try(
          controller.createGame(
            List("Henrik", "Julian", "Till", "Julian2", "Dominik", "Luuk"),
            "10",
            "20"
          )
        )
        result match {
          case Success(value)     => return true
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // make a monetary contribution to the pot by calling "bet (amount)". This indicates that you are confident in your current hand.
      case "bet" =>
        val result: Try[Boolean] = Try(controller.bet(inputList(1).toInt))
        result match {
          case Success(value)     => return true
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // fold to to discard your hand and forfeit any further involvement in the current round. Do this if your hand is weak.
      case "fold" =>
        val result: Try[Boolean] = Try(controller.fold)
        result match {
          case Success(value)     => return true
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // call to match the current bet to continue in the round and see the next community cards. You can only call if there were bets beforehand.
      case "call" =>
        val result: Try[Boolean] = Try(controller.call)
        result match {
          case Success(value)     => return true
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // check to dismiss your right to bet and pass the action to the next player. You can only do this if there were no bets in the current state of the game.
      case "check" =>
        val result: Try[Boolean] = Try(controller.check)
        result match {
          case Success(value)     => return true
          case Failure(exception) => showError(exception.getMessage())
        }
        false
      // close the game
      case "q" =>
        sys.exit()
        true
      // undo the last move
      case "u" =>
        controller.undo
        true
      // redo the move
      case "r" =>
        controller.redo
        true
      case _ =>
        println("invalid command")
        false
    }
  }

  def showError(errMessage: String): Unit = {
    println(s"Error: ${errMessage}")
  }
}
