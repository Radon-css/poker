package de.htwg.poker
package aview

import de.htwg.poker.Client
import de.htwg.poker.controllers.Controller
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}
import util.Observer

class TUI(controller: Controller) extends Observer {
  controller.add(this)

  override def update: Unit = {
  val tuiViewFuture = Client.getTUIView(controller.gameState)
  val evalHandFuture = Client.evalHand(controller.gameState)

  // Beide Futures zusammenfassen
  val combinedFuture = for {
    view <- tuiViewFuture
    handEval <- evalHandFuture
  } yield (view, handEval)

  combinedFuture.onComplete {
    case Success((view, handEval)) =>
      print("\u001b[2J\u001b[H")
      println(view)
      println()
      println("current hand:" + handEval)
    case Failure(ex) =>
      println(s"Error during update: ${ex.getMessage}")
  }
}

  def gameLoop(): Unit = {
    while (true) {
    try {
      val inputOpt = Option(readLine()).map(_.trim).filter(_.nonEmpty)
      inputOpt.foreach { input =>
        processInput(input)
      }
    } catch {
      case ex: Exception =>
        println(s"An error occurred: ${ex.getMessage}")
    }
  }
  }

  def processInput(input: String): Boolean = {
    val inputList = input.split(" ").toList
    inputList(0) match {
      case "start" =>
        handleCreateGame(
          inputList.tail.dropRight(2),
          inputList.init.last,
          inputList.last
        )
      case "x" =>
        handleCreateGame(
          List("Henrik", "Julian", "Till", "Julian2", "Dominik", "Luuk"),
          "10",
          "20"
        )
      case "bet" =>
        handleControllerCall(controller.bet(inputList(1).toInt))
      case "fold" =>
        handleControllerCall(controller.fold)
      case "call" =>
        handleControllerCall(controller.call)
      case "check" =>
        handleControllerCall(controller.check)
      case "q" =>
        sys.exit()
        true
      case _ =>
        println("invalid command")
        false
    }
  }

  private def handleCreateGame(players: List[String], smallBlind: String, bigBlind: String): Boolean = {
    val result: Try[Boolean] = Try(controller.createGame(players, smallBlind, bigBlind))
    result match {
      case Success(value) => true
      case Failure(exception) =>
        println(s"Error: ${exception.getMessage}")
        false
    }
  }

  private def handleControllerCall(operation: => Boolean): Boolean = {
    val result: Try[Boolean] = Try(operation)
    result match {
      case Success(value) => true
      case Failure(exception) =>
        println(s"Error: ${exception.getMessage}")
        false
    }
  }
}
