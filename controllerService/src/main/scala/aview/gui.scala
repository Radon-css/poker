package de.htwg.poker
package aview

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.web.WebView
import scalafx.scene.control.Button
import controller.Controller
import model.GameState
import model.Card
import model.Player
import util.Observer
import scalafx.application.Platform
import scala.util.{Try, Success, Failure}
import javafx.concurrent.Worker.State
import netscape.javascript.JSObject
import javafx.scene.web.WebEngine
import scala.compiletime.ops.boolean
import de.htwg.poker.util.GUIView

/* For our GUI, we decided to use ScalaFx in combination with the WebView method
  from the ScalaFx Library so we can design our entire GUI with HTML and CSS.
  The class GUI sets up the webView and contains methods to update the GUI and
  also to update our GameState from within the GUI. To update the GUI, we simply call
  the render method of the Object GUIView in the folder util that contains all of our Html and Javascript code.*/

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  // webview setup

  var webEngine: WebEngine = _
  override def start(): Unit = {
    // Creating a WebView with pre-set width and height
    val webView = new WebView {
      prefWidth = 1000
      prefHeight = 800
    }

    // Loading content using the GUIView.render method
    webView.engine.loadContent(GUIView.render)

    // Adding a listener for the load state of the WebEngine
    webView.engine.getLoadWorker.stateProperty.addListener { (_, _, newValue) =>
      if (newValue == State.SUCCEEDED) {
        // On successful load, adding a JavaScript function
        val window =
          webView.engine.executeScript("window").asInstanceOf[JSObject]
        window.setMember("invoke", new External)
      }
    }

    // Configuring the primary stage
    val stage = new JFXApp3.PrimaryStage {
      title = "Poker"
      scene = new Scene {
        // Adding the WebView to the scene
        content = new HBox(webView)
      }
    }

    // Assigning the WebEngine for later use
    webEngine = webView.engine
  }

  // here we call GUIView.render to update our GUI
  override def update: Unit = {
    Platform.runLater(() => webEngine.loadContent(GUIView.render))
  }

  /* Because we used Html and Javascript for our GUI, we need to make upcalls
     from JavaScript to Scala, so that a push of a html button can result in a
     call to a Scala function and thus an update of the GameState. All methods
     in class External are called from inside Javascript and then
     call a Scala function to update the GameState.*/

  class External {
    def call(): Unit = controller.call
    def check(): Unit = controller.check
    def fold(): Unit = controller.fold
    def undo(): Unit = controller.undo
    def redo(): Unit = controller.redo
    def bet(amount: Int): Unit = controller.bet(amount)
    def restartGame(): Unit = controller.restartGame

    /* we need the toList method so that a user of our game can
      enter playerNames as well as the amount of the big and smallBlind
      in the start screen of our game. the toList methods reads in the
      according Html Text Input Fields for this to work*/

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

  /* these are methods to create the new Html Code that has to be displayed in the GUI when the GameState is updated.
     in GUIView, we then simply pass the new Html Code that has been created by these methods into our static Html code with the help of String Variables.*/

  def updatePlayersHtml(gameState: GameState): List[String] = {
    val newPlayerList = gameState.players.getOrElse(List.empty[Player]).map(_.toHtml)
    List
      .fill(6)(HiddenHtml)
      .patch(0, newPlayerList, newPlayerList.size)
  }

  def updateCardsHtml(gameState: GameState): List[(String, String)] = {
    val playerList = gameState.players.getOrElse(List.empty[Player]).zipWithIndex
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
