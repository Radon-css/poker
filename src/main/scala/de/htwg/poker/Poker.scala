package de.htwg.poker
import controller.ControllerComponent.ControllerInterface
import aview.TUI
import scala.io.StdIn.readLine
import model.GameStateComponent.GameStateBaseImpl.GameState
import aview.GUI
import com.google.inject.Guice
import scala.concurrent.Await
import scala.concurrent.Future

object Poker {
  val injector = Guice.createInjector(new PokerModule)
  val controller = injector.getInstance(classOf[ControllerInterface])
  controller.createNewGameState()
  val tui = new TUI(controller)
  val gui = new GUI(controller)

  @main
  def run: Unit = {
    implicit val context = scala.concurrent.ExecutionContext.global
    val f = Future {
      gui.main(Array[String]())
    }
    tui.gameLoop()
    Await.ready(f, scala.concurrent.duration.Duration.Inf)
  }
}
