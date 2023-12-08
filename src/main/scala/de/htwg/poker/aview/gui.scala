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

  def updatePlayerListHtml(gameState: GameState): List[String] = {
    val playerList = gameState.getPlayers
    val newPlayerList = playerList.map(player =>
      getPlayerHtml(
        player.playername,
        player.balance,
        player.currentAmountBetted
      )
    )
    val defaultPlayerListHtml = List.fill(6)("<div class=\"hidden\"></div>")
    defaultPlayerListHtml.patch(0, newPlayerList, newPlayerList.size)
  }

  def updateCardListHtml(gameState: GameState): List[(String, String)] = {
    val playerList = gameState.getPlayers.zipWithIndex
    val playerAtTurn = gameState.getPlayerAtTurn

    val newCardList = playerList.collect {
      case (player, index) if index == playerAtTurn =>
        (getCardHtml(player.card1), getCardHtml(player.card2))
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

    val newBoardList = boardList.map(card => getCardHtml(card))
    val hiddenBoardList =
      List.fill(5)("<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>")
    hiddenBoardList.patch(0, newBoardList, newBoardList.size)
  }

  def getCardHtml(card: Card): String =
    s"<div class=\"rounded-lg bg-slate-100 w-6 h-9 flex flex-col justify-center items-center shadow-xl shadow-black/50\">${card.suit.toHtml}<h1 class=\"font-bold \">${card.rank.toString}</h1></div>"

  def getHiddenCardHtml: String =
    "<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>"

  def getPlayerHtml(
      name: String,
      balance: Int,
      betSize: Int
  ): String =
    s"""<div class=\"flex flex-col items-center justify-center space-x-2\">
                <div class=\"rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white\">
                  <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-person-fill\" viewBox=\"0 0 16 16\">
                    <path d=\"M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6\"/>
                  </svg>
                </div>
                <div class=\"flex flex-col justify-center items-center text-slate-100\">
                    <p class=\"p-1\">$name</p>
                    <div class=\"rounded-full bg-slate-100 text-gray-400\">
                      <p class=\"p-1\">$balance</p>
                    </div>
                    <div>$betSize</div>
                </div>
              </div>"""

  override def start(): Unit = {
    val gameState = controller.gameState
    val playerListHtml = updatePlayerListHtml(gameState)
    val cardListHtml = updateCardListHtml(gameState)
    val boardListHtml = updateBoardListHtml(gameState)

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
            <body>
              <div class="flex flex-col justify-center items-center h-screen w-full bg-gray-700">
                <div class="flex space-x-56">
                ${playerListHtml(0)}
                ${playerListHtml(1)}
              </div>
              <div class="flex justify-center items-center h-64 w-full">
                ${playerListHtml(5)}
                <div class="flex flex-col items-center rounded-full bg-teal-600 h-52 w-96 border-8 border-teal-400 shadow-[inset_0_-2px_8px_rgba(0,0,0,0.8)]">
                    <div class="flex mt-4 space-x-24">
                    <div class="flex h-10 w-12">
                            ${cardListHtml(0)._1}
                            ${cardListHtml(0)._2}
                        </div>
                        <div class="flex h-10 w-12">
                            ${cardListHtml(1)._1}   
                            ${cardListHtml(1)._2}
                    </div>
                    </div>
                    <div class = "flex mt-6">
                    <div class="flex h-10 w-12">
                            ${cardListHtml(5)._1}   
                            ${cardListHtml(5)._2}
                    </div>
                    <div class="flex px-16">
                    ${boardListHtml(0)}   
                    ${boardListHtml(1)}
                    ${boardListHtml(2)}
                    ${boardListHtml(3)}
                    ${boardListHtml(4)}
                    </div>
                    <div class="flex h-10 w-12">
                            ${cardListHtml(2)._1}   
                            ${cardListHtml(2)._2}
                    </div>
                    </div>
                    <div class = "flex mt-8 space-x-24">
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
            if (gameState.getPlayers.size == 0)
              "<button class=\"font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100\" onclick=\"startGame()\">start</button>"
            else
              """
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="startGame()">start</button>
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="call()">call</button>
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="check()">check</button>
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="fold()">fold</button>
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="undo()">undo</button>
              <button class="font-bold h-6 w-12 my-5 text-slate-100 rounded-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100" onclick="redo()">redo</button>
              <form onsubmit="bet()" class="flex flex-row items-center">
                <input type="number" id="betInput" name="fname" placeholder="Enter betsize" class="bg-transparent rounded-l-md ring w-16 ring-slate-100 px-2 py-1 focus:none text-white">
                <input type="submit" value="Bet" class="font-bold h-8 w-6 my-5 text-slate-100 rounded-r-md ring ring-slate-100 hover:text-gray-700 hover:bg-slate-100 px-4">
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
}
