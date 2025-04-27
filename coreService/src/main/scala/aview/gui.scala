package de.htwg.poker
package aview

import de.htwg.poker.Client
import de.htwg.poker.controllers.Controller
import javafx.concurrent.Worker.State
import javafx.scene.web.WebEngine
import model.Card
import model.GameState
import model.Player
import netscape.javascript.JSObject
import scala.compiletime.ops.boolean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.web.WebView
import util.Observer

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  // webview setup
  var webEngine: WebEngine = _

  override def start(): Unit = {
    val webView = new WebView {
      prefWidth = 1000
      prefHeight = 800
    }

    // Load initial content
    loadContent(webView)

    val stage = new JFXApp3.PrimaryStage {
      title = "Poker"
      scene = new Scene {
        content = new HBox(webView)
      }
    }

    webEngine = webView.engine
  }

  private def loadContent(webView: WebView): Unit = {
    controller.gameState.getHandEval(controller.gameState.playerAtTurn).onComplete {
      case Success(eval) =>
        Client.getGUIView(controller.gameState, eval).onComplete {
          case Success(content) =>
            Platform.runLater(() => {
              webView.engine.loadContent(content)
              webView.engine.getLoadWorker.stateProperty.addListener { (_, _, newValue) =>
                if (newValue == State.SUCCEEDED) {
                  val window = webView.engine.executeScript("window").asInstanceOf[JSObject]
                  window.setMember("invoke", new External)
                }
              }
            })
          case Failure(ex) =>
            Platform.runLater(() => println(s"Failed to load GUI content: ${ex.getMessage}"))
        }
      case Failure(ex) =>
        Platform.runLater(() => println(s"Failed to evaluate hand: ${ex.getMessage}"))
    }
  }

  override def update: Unit = {
    controller.gameState.getHandEval(controller.gameState.playerAtTurn).onComplete {
      case Success(eval) =>
        Client.getGUIView(controller.gameState, eval).onComplete {
          case Success(content) =>
            Platform.runLater(() => webEngine.loadContent(content))
          case Failure(ex) =>
            Platform.runLater(() => println(s"Failed to update GUI: ${ex.getMessage}"))
        }
      case Failure(ex) =>
        Platform.runLater(() => println(s"Failed to evaluate hand: ${ex.getMessage}"))
    }
  }

  class External {
    def call(): Unit = controller.call
    def check(): Unit = controller.check
    def fold(): Unit = controller.fold
    def bet(amount: Int): Unit = controller.bet(amount)
    def restartGame(): Unit = controller.restartGame

    def toList(
        name1: String,
        name2: String,
        name3: String,
        name4: String,
        name5: String,
        name6: String,
        smallBlind: String,
        bigBlind: String
    ): Unit = {
      val names =
        List(name1, name2, name3, name4, name5, name6).filter(_.nonEmpty)
      startGame(names :+ smallBlind :+ bigBlind)
    }

    def startGame(args: List[String]): Unit = {
      val allEmpty = args.forall(_.isEmpty)
      if (!allEmpty) {
        val result: Try[Boolean] = Try {
          controller.createGame(args.dropRight(2), args.init.last, args.last)
        }
        result match {
          case Success(_)         =>
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
      } else {
        controller.createGame(
          List("Henrik", "Julian", "Till", "Noah", "Dominik", "Luuk"),
          "10",
          "20"
        )
      }
    }
  }

  def updatePlayersHtml(gameState: GameState): List[String] = {
    val newPlayerList =
      gameState.players.getOrElse(List.empty[Player]).map(_.toHtml)
    List
      .fill(6)(HiddenHtml)
      .patch(0, newPlayerList, newPlayerList.size)
  }

  def updateCardsHtml(gameState: GameState): List[(String, String)] = {
    val playerList =
      gameState.players.getOrElse(List.empty[Player]).zipWithIndex
    val playerAtTurn = gameState.playerAtTurn
    val newCardList = playerList.map {
      case (player, index) if index == playerAtTurn =>
        (player.card1.toHtml, player.card2.toHtml)
      case _ =>
        (HiddenPlayerCardHtml, HiddenHtml)
    }

    val defaultCardListHtml = List.fill(6)(
      (HiddenHtml, HiddenHtml)
    )

    defaultCardListHtml.patch(0, newCardList, newCardList.size)
  }

  def updateBoardHtml(gameState: GameState): List[String] = {
    val boardList = gameState.board
    val newBoardList = boardList.map(_.toHtml)
    val invisBoardList = List.fill(5)(HiddenHtml)
    val hiddenBoardList =
      List.fill(5)(HiddenBoardCardHtml)

    if (gameState.players.getOrElse(List.empty[Player]).isEmpty) invisBoardList
    else hiddenBoardList.patch(0, newBoardList, newBoardList.size)
  }

  def updateBetsHtml(gameState: GameState): List[String] = {
    val playerList = gameState.players.getOrElse(List.empty[Player])
    val newBetList = playerList.map(_.betSizeToHtml)
    val hiddenBetList = List.fill(6)(HiddenHtml)

    hiddenBetList.patch(0, newBetList, playerList.size)
  }

  val HiddenPlayerCardHtml =
    """<svg class="ml-3" width="42" height="42" viewBox="0 0 119 119" fill="none" xmlns="http://www.w3.org/2000/svg">
     |  <path fill-rule="evenodd" clip-rule="evenodd" d="M38.9596 14H14C6.26801 14 0 20.268 0 28V105C0 112.732 6.26801 119 14 119H44.4789L16.2797 110.439C8.88111 108.193 5.16362 100.534 7.97644 93.3319L38.9596 14Z" fill="#2dd4bf"/>
     |  <rect width="67.2466" height="106.582" rx="14" transform="matrix(0.95688 0.290485 -0.363791 0.931481 54.6531 0)" fill="#2dd4bf"/>
     |</svg>""".stripMargin

  val HiddenBoardCardHtml =
    "<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>"

  val HiddenHtml = "<div class=\"hidden\"> </div>"
}
