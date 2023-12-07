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

class ScalaFXHelloWorld(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  override def update: Unit = {}

  def externalFunction(): Unit = {
    println("Externe Funktion aufgerufen!")
    test0 = "fetterCock"
  }
  var test0 = "Cock"
  override def start(): Unit = {
    val gameState = controller.gameState

    val button = new Button("Cock")

    val stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new HBox {
          children = Seq(
            new WebView {
              engine.loadContent(
                """
                  <!doctype html>
                  <html>
                  <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <script src="https://cdn.tailwindcss.com"></script>
                          <body >
                              <div class="flex justify-center items-center h-screen w-full bg-slate-700">
                            <div class="rounded-full bg-teal-600 h-1/3 w-1/2 border-8 border-teal-400">
                          </div>
                            </div>
                          </body>
                        </html>
                  """.stripMargin
              )
              prefWidth = 800
              prefHeight = 600
            }
          )
        }
      }
    }
  }
}
