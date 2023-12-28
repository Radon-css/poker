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
import util.Observer
import scalafx.application.Platform
import scala.util.{Try, Success, Failure}
import javafx.concurrent.Worker.State
import netscape.javascript.JSObject
import javafx.scene.web.WebEngine
import scala.compiletime.ops.boolean

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)
  var webEngine: WebEngine = _

  override def start(): Unit = {
    val webView = new WebView {
      prefWidth = 1000
      prefHeight = 800
    }
    // webview setup
    webView.engine.loadContent(render)

    webView.engine.getLoadWorker
      .stateProperty()
      .addListener((_, _, newValue) => {
        if (newValue == State.SUCCEEDED) {
          val window =
            webView.engine.executeScript("window").asInstanceOf[JSObject]
          window.setMember("invoke", new External)
        }
      })

    // stage setup
    val stage = new JFXApp3.PrimaryStage {
      title = "Poker"
      scene = new Scene {
        content = new HBox {
          children = Seq(webView)
        }
      }
    }
    // bind engine to webview
    webEngine = webView.engine
  }

  override def update: Unit = {
    Platform.runLater(() => updateGui())
  }

  def updateGui() = {
    webEngine.loadContent(render)
  }

  def render: String = {

    val gameState = controller.gameState
    val playerListHtml = updatePlayerListHtml(gameState)
    val cardListHtml = updateCardListHtml(gameState)
    val boardListHtml = updateBoardListHtml(gameState)
    val betListHtml = updateBetListHtml(gameState)
    val gameStarted = gameState.getPlayers.size != 0

    return s"""
    ${
        if (gameStarted) {
          s"""
          <!DOCTYPE html>
          <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <script src="https://cdn.tailwindcss.com"></script>
            </head>
            <body class="flex flex-col">
            <div class="flex flex-col justify-center items-center h-screen w-full bg-gray-700 space-y-5">
              <div class="flex items-center justify-between w-full h-14">
              <div class="flex space-x-2 ml-2 ">
                <button class="mt-4 ml-4 font-extrabold h-12 w-16 my-5 text-slate-100 bg-gray-600 rounded-full hover:text-gray-700 hover:bg-slate-100 flex justify-center items-center" onclick="undo()">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-undo"><path d="M3 7v6h6"/><path d="M21 17a9 9 0 0 0-9-9 9 9 0 0 0-6 2.3L3 13"/></svg>
                </button>
                <button class="mt-4 font-extrabold h-12 w-16 my-5 text-slate-100 rounded-full bg-gray-600 hover:text-gray-700 hover:bg-slate-100 flex justify-center items-center" onclick="redo()">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-redo"><path d="M21 7v6h-6"/><path d="M3 17a9 9 0 0 1 9-9 9 9 0 0 1 6 2.3l3 2.7"/></svg>
                </button>
              </div>
                <button class="mt-4 mr-4 font-bold h-12 w-28 my-5 text-slate-100 rounded-full bg-gray-600 hover:text-gray-700 hover:bg-slate-100" onclick="startGame()">RESTART</button>
            </div>
            <div class="rounded-full bg-gray-500">
              <h1 class="text-gray-100">Current Hand: ${gameState.getCurrentHand}</h1>
            </div>
                <div class="flex space-x-56">
                ${playerListHtml(0)}
                ${playerListHtml(1)}
              </div>
              <div class="flex justify-center items-center h-64 w-full">
                ${playerListHtml(5)}
                <div class="flex flex-col items-center rounded-full bg-teal-600 h-72 w-3/5 border-8 border-teal-400 shadow-[inset_0_-2px_8px_rgba(0,0,0,0.8)]">
                    <div class="flex mt-4 space-x-48">
                      <div class="flex h-10 w-12">
                        ${cardListHtml(0)._1}
                        ${cardListHtml(0)._2}
                      </div>
                      <div class="flex h-10 w-12">
                        ${cardListHtml(1)._1}
                        ${cardListHtml(1)._2}
                      </div>
                    </div>

                  <div class ="flex space-x-36">
                      ${betListHtml(0)}
                      ${betListHtml(1)}
                  </div>

                  <div class = "flex justify-center items-center space-x-12 mt-6">

                    <div class=" flex items-center space-x-2">
                      <div class="flex h-10 w-12">
                        ${cardListHtml(5)._1}
                        ${cardListHtml(5)._2}
                      </div>
                        ${betListHtml(5)}
                    </div>

                      <div class="flex flex-col items-center space-y-2">
                        <p class="rounded-full bg-slate-100 px-2">${gameState.getPot + "$"}
                        </p>
                        <div class="flex px-16">
                        ${boardListHtml(0)}
                        ${boardListHtml(1)}
                        ${boardListHtml(2)}
                        ${boardListHtml(3)}
                        ${boardListHtml(4)}
                        </div>
                      </div>

                    <div class=" flex items-center space-x-2">
                        ${betListHtml(2)}
                      <div class="flex h-10 w-12">
                        ${cardListHtml(2)._1}
                        ${cardListHtml(2)._2}
                      </div>
                    </div>
                    </div>

                    <div class ="flex space-x-36 mt-6">
                        ${betListHtml(4)}
                        ${betListHtml(3)}
                  </div>

                    <div class = "flex mb-4 space-x-48 mt-1">
                    <div class="flex h-10 w-12">
                            ${cardListHtml(4)._1}
                            ${cardListHtml(4)._2}
                    </div>
                    <div class="flex h-10 w-12">
                            ${cardListHtml(3)._1}
                            ${cardListHtml(3)._2}
                    </div>
                    </div>
                </div>
                ${playerListHtml(2)}
              </div>
              <div class="flex space-x-56">
                ${playerListHtml(4)}
                ${playerListHtml(3)}
              </div>
              <div class="flex space-x-8 items-center">
              <button class="w-28 h-12 font-bold my-5 bg-red-600 text-slate-100  rounded-full hover:text-gray-700 hover:bg-slate-100" onclick="fold()">
                <div class="flex justify-center items-center">FOLD</div>
              </button>
              <button class="w-28 h-12 font-bold my-5 bg-blue-600 text-slate-100 rounded-full  hover:text-gray-700 hover:bg-slate-100" onclick="check()">CHECK</button>
              <button class="w-28 h-12 font-bold my-5 bg-green-600 text-slate-100 rounded-full hover:text-gray-700 hover:bg-slate-100" onclick="call()">CALL ${gameState.getHighestBetSize + "$"}</button>
              <form onsubmit="bet()" class="flex flex-row items-center">
                <input type="submit" value="BET" class="w-28 h-12 font-bold my-5 bg-yellow-600 text-slate-100 rounded-l-full hover:text-gray-700 hover:bg-slate-100">
                <input type="number" id="betInput" name="fname" placeholder="Enter betsize" class=" h-12 w-28 bg-slate-600 rounded-r-full px-2 py-1 focus:none text-white">
              </form>
              </div>
              <script>
                function startGame() {
                  invoke.startGame();
                }
                function call() {
                  invoke.call();
                }
                function check() {
                  invoke.check();
                }
                function fold()  {
                  invoke.fold();
                }
                function undo() {
                  invoke.undo();
                }
                function redo() {
                  invoke.redo();
                }
                function bet() {
                  invoke.bet(document.getElementById("betInput").value);
                }
              </script>
              </body>
          </html>
              """
        } else {
          s"""
              <!DOCTYPE html>
          <html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.tailwindcss.com"></script>
  </head>
  <body class="flex flex-col">
   <form onsubmit="startGame()">
    <div class="flex flex-col justify-center items-center h-screen w-full bg-gray-700 space-y-5">
      <div class="flex flex-col justify-center items-center">
      <h1 class="text-xl font-bold text-gray-300/80">Insert Playernames</h1>
      <h1 class="text-xl font-bold text-gray-300/80">Insert small and big Blind</h1>
      <h1 class="text-xl font-bold text-gray-300/80 mb-4">Press start to play</h1>
    </div>
      <div class="flex space-x-56">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm1()">
        <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16 ">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form1" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    <div class="flex flex-col justify-center items-center">
      <button onClick="revealForm2()">
      <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
      <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
        <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
      </svg>
    </div>
  </button>
    <input type="string" id="form2" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
  </div>
    </div>
    <div class="flex justify-center items-center h-64 w-full">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm3()">
        <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form3" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
      <div class="flex flex-col items-center justify-center rounded-full bg-teal-600 h-72 w-3/5 border-8 border-teal-400 shadow-[inset_0_-2px_8px_rgba(0,0,0,0.8)]">
      <h1 class="text-9xl font-extrabold text-black/20 italic">POKER</h1>
      <div class="flex justify-center items-center w-full space-x-1 mt-4">
        <input type="number" id="smallBlind" name="fname" placeholder="smallBlind" class="h-8 w-24 bg-black/30 rounded-full focus:none text-white text-center hover:bg-black/20">
        <input type="number" id="bigBlind" name="fname" placeholder="bigBlind" class="h-8 w-24 bg-black/30 rounded-full focus:none text-white text-center hover:bg-black/20">
      </div>
      </div>
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm4()">
        <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form4" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    </div>
    <div class="flex space-x-56">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm5()">
        <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form5" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    <div class="flex flex-col justify-center items-center">
      <button onClick="revealForm6()">
      <div class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
      <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
        <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
      </svg>
    </div>
  </button>
    <input type="string" id="form6" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
  </div>
    </div>
    <div class="flex space-x-8 items-center">
     <button type="submit" class="w-28 h-12 font-bold my-5 bg-gray-300/80 text-slate-700 rounded-full hover:text-gray-100 hover:bg-gray-600 shadow-lg" onclick="startGame()">
      <div class="flex justify-center items-center space-x-1">
      <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-play-fill hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z"/>
      </svg>
    </div>
  </button>
  </div>
</form>
    <script>
  function startGame() {
    invoke.toList(
      document.getElementById("form1").value,
      document.getElementById("form2").value,
      document.getElementById("form3").value,
      document.getElementById("form4").value,
      document.getElementById("form5").value,
      document.getElementById("form6").value,
      document.getElementById("smallBlind").value,
      document.getElementById("bigBlind").value
    );
  }
  function revealForm1(){
    event.preventDefault();
    var element = document.getElementById("form1");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  function revealForm2(){
    event.preventDefault();
    var element = document.getElementById("form2");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  function revealForm3(){
    event.preventDefault();
    var element = document.getElementById("form3");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  function revealForm4(){
    event.preventDefault();
    var element = document.getElementById("form4");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  function revealForm5(){
    event.preventDefault();
    var element = document.getElementById("form5");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  function revealForm6(){
    event.preventDefault();
    var element = document.getElementById("form6");
    element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
  }
  </script>
  </body>
</html>
        """
        }
      }
    """
  }

  class External {
    def startGame(args: List[String]): Unit = {
      val allEmpty = args.forall(_.isEmpty)
      if (!allEmpty) {
        val args2 = args.toList
        val result: Try[Boolean] = Try(
          controller.createGame(
            args2.dropRight(2),
            args2.init.last,
            args2.last
          )
        )
        result match {
          case Success(value)     => return
          case Failure(exception) => println(s"Error: ${exception.getMessage}")
        }
      } else {
        controller.createGame(
          List("Henrik", "Julian", "Till", "Julian", "Dominik", "Luuk"),
          "10",
          "20"
        )
      }
    }
    def call(): Unit = {
      controller.call()
    }
    def check(): Unit = {
      controller.check()
    }
    def fold(): Unit = {
      controller.fold()
    }
    def undo(): Unit = {
      controller.undo
    }
    def redo(): Unit = {
      controller.redo
    }
    def bet(amount: Int): Unit = {
      controller.bet(amount)
    }
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
  }

  def getHiddenCardHtml: String =
    "<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>"

  def updatePlayerListHtml(gameState: GameState): List[String] = {
    val playerList = gameState.getPlayers
    val newPlayerList = playerList.map(player => player.toHtml)
    val defaultPlayerListHtml = List.fill(6)("<div class=\"hidden\"></div>")
    defaultPlayerListHtml.patch(0, newPlayerList, newPlayerList.size)
  }

  def updateCardListHtml(gameState: GameState): List[(String, String)] = {
    val playerList = gameState.getPlayers.zipWithIndex
    val playerAtTurn = gameState.getPlayerAtTurn

    val newCardList = playerList.collect {
      case (player, index) if index == playerAtTurn =>
        (player.card1.toHtml, player.card2.toHtml)
      case (player, index) =>
        (getHiddenCardHtml, getHiddenCardHtml)
    }
    val defaultCardListHtml = List.fill(6)(
      ("<div class=\"hidden\"> </div>", "<div class=\"hidden\"> </div>")
    )
    defaultCardListHtml.toList.patch(0, newCardList, newCardList.size)
  }

  def updateBoardListHtml(gameState: GameState): List[String] = {
    val boardList = gameState.getBoard

    val newBoardList = boardList.map(card => card.toHtml)
    val invisBoardList =
      List.fill(5)("<div class=\"hidden\"> </div>")
    val hiddenBoardList =
      List.fill(5)("<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>")

    if (gameState.getPlayers == Nil) {
      return invisBoardList
    }
    hiddenBoardList.patch(0, newBoardList, newBoardList.size)
  }

  def updateBetListHtml(gameState: GameState): List[String] = {
    val playerList = gameState.getPlayers

    val newBetList =
      playerList.map(player => player.betSizeToHtml)
    val hiddenBetList =
      List.fill(6)("<div class=\"hidden\"> </div>")
    hiddenBetList.patch(0, newBetList, playerList.size)
  }
}
