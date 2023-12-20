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

import javafx.concurrent.Worker.State
import netscape.javascript.JSObject
import javafx.scene.web.WebEngine

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  override def update: Unit = {
    Platform.runLater(() => start())
  }

  class External {
    def startGame(): Unit = {
      controller.createGame(
        List("Henrik", "Julian", "Till", "Julian", "Dominik", "Luuk"),
        "10",
        "20"
      )
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
  }

  def getHiddenCardHtml: String =
    "<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>"

  override def start(): Unit = {
    val gameState = controller.gameState
    val playerListHtml = updatePlayerListHtml(gameState)
    val cardListHtml = updateCardListHtml(gameState)
    val boardListHtml = updateBoardListHtml(gameState)
    val betListHtml = updateBetListHtml(gameState)
    val gameStarted = gameState.getPlayers.size != 0

    val webView = new WebView {
      engine.loadContent(
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
            ${
            if (gameStarted) {
              """
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
              """
            } else {
              ""
            }
          }
              
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
                        <p class="rounded-full bg-slate-100 px-2">${
            if (gameStarted) { gameState.getPot + "$" }
            else { "" }
          }
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
                ${
            if (!gameStarted) {
              """ 
                <button class="w-28 h-12 font-bold my-5 bg-gray-700 text-slate-100 ring ring-slate-100 rounded-full hover:text-gray-700 hover:bg-slate-100" onclick="startGame">
                <div class="flex justify-center items-center">START</div>
              </button>
            """
            } else
              s"""
              <button class="w-28 h-12 font-bold my-5 bg-red-600 text-slate-100 rounded-full hover:text-gray-700 hover:bg-slate-100" onclick="fold()">
                <div class="flex justify-center items-center">FOLD</div>
              </button>
              <button class="w-28 h-12 font-bold my-5 bg-blue-600 text-slate-100 rounded-full  hover:text-gray-700 hover:bg-slate-100" onclick="check()">CHECK</button>
              <button class="w-28 h-12 font-bold my-5 bg-green-600 text-slate-100 rounded-full hover:text-gray-700 hover:bg-slate-100" onclick="call()">CALL ${gameState.getHighestBetSize + "$"}</button>
              <form onsubmit="bet()" class="flex flex-row items-center">
                <input type="submit" value="BET" class="w-28 h-12 font-bold my-5 bg-yellow-600 text-slate-100 rounded-l-full hover:text-gray-700 hover:bg-slate-100">
                <input type="number" id="betInput" name="fname" placeholder="Enter betsize" class=" h-12 w-28 bg-slate-600 rounded-r-full px-2 py-1 focus:none text-white">
              </form>
              </div>
                """
          }
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
      )
      prefWidth = 1000
      prefHeight = 800
      engine.getLoadWorker
        .stateProperty()
        .addListener((_, _, newValue) => {
          if (newValue == State.SUCCEEDED) {
            val window = engine.executeScript("window").asInstanceOf[JSObject]
            window.setMember("invoke", new External)
          }
        })
    }

    val stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new HBox {
          children = Seq(webView)
        }
      }
    }

  }
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
