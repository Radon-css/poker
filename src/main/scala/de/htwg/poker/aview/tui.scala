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


  def processInput(input: String): Boolean = {
    val inputList = input.split(" ").toList
    inputList(0) match {
      case "start" => controller.startGame(inputList.tail)
      true
    }
  }
}
