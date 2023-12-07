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

// ... (Ihre vorhandenen Importe)

class ScalaFXHelloWorld(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  override def update: Unit = {
    Platform.runLater(() => start())
  }

  // Scala-Funktion, die von JavaScript aufgerufen wird
  class External {
    def startGame(): Unit = {
      println("Externe Funktion aufgerufen!")
      controller.createGame(
        List("Henrik", "Julian", "Till", "Julian", "Dominik", "Luuk"),
        "10",
        "20"
      )
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
    playerListHtml.patch(0, newPlayerList, newPlayerList.size)
  }

  var playerListHtml: List[String] =
    List.fill(6)("<div class=\"hidden\"></div>")

  def getCardHtml(card: Card): String =
    s""

  def getPlayerHtml(name: String, balance: Int, betSize: Int): String =
    s"<div class=\"text-slate-100 px-24 py-8\"><h1>(${balance}$$)</h1><h1>$name</h1><h1>$betSize</h1></div>"

  val rank =
    "<svg class=\"mt-1\"xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-club-fill\" viewBox=\"0 0 16 16\"><path d=\"M11.5 12.5a3.493 3.493 0 0 1-2.684-1.254 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907 3.5 3.5 0 1 1-2.538-5.743 3.5 3.5 0 1 1 6.708 0A3.5 3.5 0 1 1 11.5 12.5\"/></svg>"

  override def start(): Unit = {
    val gameState = controller.gameState
    if (gameState.getPlayers != Nil)
      playerListHtml = updatePlayerListHtml(gameState)

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
                <div class="flex">
                ${playerListHtml(0)}
                ${playerListHtml(1)}
              </div>
              <div class="flex justify-center items-center h-64 w-full">
                ${playerListHtml(2)}
                <div class="flex flex-col items-center rounded-full bg-teal-600 h-52 w-96 border-8 border-teal-400">
                    <div class="flex mt-4 space-x-24">
                    <div class="flex h-10 w-12">
                            <div class="rounded-lg bg-slate-100 w-6 h-9 flex flex-col justify-center items-center shadow-xl shadow-black/50"> 
                            <img class="h-3 w-3 mt-1"src="suit-club-fill.svg">
                            <h1 class="font-bold ">K</h1>
                          </div>
                          <div class="rounded-lg bg-slate-100 w-6 h-9 flex flex-col justify-center items-center shadow-xl shadow-black/50"> 
                            $rank
                            <h1 class="font-bold ">K</h1>
                          </div>
                        </div>
                    <div class="bg-red-700 h-8 w-8"></div>
                    </div>
                    <div class = "flex mt-8 space-x-64">
                    <div class="bg-red-700 h-8 w-8"></div>
                    <div class="bg-red-700 h-8 w-8"></div>
                    </div>
                    <div class = "flex mt-8 space-x-24">
                    <div class="bg-red-700 h-8 w-8"></div>
                    <div class="bg-red-700 h-8 w-8"></div>
                    </div>
                </div>
                ${playerListHtml(3)}
                </div>
              <div class="flex">
                ${playerListHtml(4)}
                ${playerListHtml(5)}
              </div>
              <button class="font-bold h-6 w-10 my-5 text-slate-100 outline outline-slate-100 hover:text-gray-700 hover:bg-slate-100 "onclick="startGame()">start</button>
              <script>
                function startGame() {
                  invoke.startGame();
                }
              </script>
            </body>
          </html>
        """
      )
      prefWidth = 800
      prefHeight = 600
      engine.getLoadWorker
        .stateProperty()
        .addListener((_, _, newValue) => {
          if (newValue == State.SUCCEEDED) {
            val window = engine.executeScript("window").asInstanceOf[JSObject]
            window.setMember("invoke", new External)
          }
        })
    }

    // Fügen Sie den WebView der Szene hinzu
    val stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new HBox {
          children = Seq(webView)
        }
      }
    }
  }
}
