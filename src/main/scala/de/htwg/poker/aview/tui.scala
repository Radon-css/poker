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
      if (!processInput(input))
        println("ungültiger Befehl")
    }
  }

  def processInput(input: String): Boolean = {
    val inputList = input.split(" ").toList
    inputList(0) match {
      case "start" =>
        controller.startGame(inputList.tail)
        true
      case "bet" =>
        controller.bet(inputList(1).toInt)
        true
      case _ =>
        false
    }
  }
}
