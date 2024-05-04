package de.htwg.poker;
import controller.Controller
import scala.io.StdIn.readLine
import model.GameState
import aview.GUI
import de.htwg.poker.util.Evaluator

import scala.concurrent.Await
import scala.concurrent.Future

object Poker {
  // here we need to pass in a stub of a GameState in order to initially create our controller
  val controller = new Controller(
    new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
  )
  val gui = new GUI(controller)

  @main
  def run: Unit = {
    // read and save Hashes for WinnerEvaluation
    Evaluator.readHashes
    gui.main(Array[String]())
  }
}
