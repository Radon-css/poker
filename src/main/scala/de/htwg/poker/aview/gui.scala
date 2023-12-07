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
import util.Observer

import javafx.concurrent.Worker.State
import netscape.javascript.JSObject

// ... (Ihre vorhandenen Importe)

class ScalaFXHelloWorld(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  override def update: Unit = {}

  // Scala-Funktion, die von JavaScript aufgerufen wird
  class External {
  def externalFunction(): Unit = {
    println("Externe Funktion aufgerufen!")
    test = "Clicked"
  }
}

  var test = "Click me"

  override def start(): Unit = {
    val gameState = controller.gameState

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
                <div class="rounded-full bg-teal-600 h-1/3 w-1/2 border-8 border-teal-400">
                </div>
                 <button class="space-y-3 text-slate-100 rounded-lg outline outline-slate-100"onclick="callScalaFunction()">$test</button>
              </div>
              <script>
                // JavaScript-Funktion, um Scala-Funktion direkt aufzurufen
                function callScalaFunction() {
                  // Rufen Sie die Scala-Funktion direkt auf
                  invoke.externalFunction();
                }
              </script>
            </body>
          </html>
        """
      )
      prefWidth = 800
      prefHeight = 600
      // Fügen Sie einen Listener hinzu, um die Scala-Funktion von JavaScript aus zu aktivieren
      engine.getLoadWorker.stateProperty().addListener((_, _, newValue) => {
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