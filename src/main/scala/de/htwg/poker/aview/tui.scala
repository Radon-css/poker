package de.htwg.poker
package aview
import controller.Controller
import util.Observer
import scala.io.StdIn.readLine

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
        controller.startGame(inputList.tail)
        true
      case "x" =>
        controller.startGame(List("Henrik","Julian","Till"))
        true
      case "bet" =>
        val bet = new controller.bet(inputList(1).toInt)
        val (isValid, errorMessage) = bet.execute()
        if(!isValid) {
          println(errorMessage)
          return false
        }
        true
      case "fold" =>
        val fold = new controller.fold()
        val (isValid, errorMessage) = fold.execute()
        if(!isValid) {
          println(errorMessage)
          return false
        }
        true
      case "call" =>
        val call = new controller.call()
        val (isValid, errorMessage) = call.execute()
        if(!isValid) {
          println(errorMessage)
          return false
        }
        true
      case "check" =>
        val check = new controller.check()
        val (isValid, errorMessage) = check.execute()
        if(!isValid) {
          println(errorMessage)
          return false
        }
          true
      case "q" =>
        sys.exit()
        true
      case _ =>
        println("invalid command")
        false
    }
  }
}
