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

  override def update: Unit = {
    main(Array())
  }

  def changeColor(button: Button): Unit = {
    if (button.styleClass.contains("bg-blue-500")) {
      button.styleClass = List("bg-red-500")
    } else {
      button.styleClass = List("bg-blue-500")
    }
  }

  override def start(): Unit = {
    val gameState = controller.gameState
    val test0 = "Cock"
    val button = new Button("Cock")
    button.styleClass = List("bg-blue-500")

    button.onAction = _ => changeColor(button)

    val stage = new JFXApp3.PrimaryStage {
      scene = new Scene {
        content = new HBox {
          children = Seq(
            new WebView {
              engine.loadContent(
                s"""
                  <html>
                    <head>
                      <link rel="stylesheet" type="text/css" href="https://unpkg.com/tailwindcss@%5E1.0/dist/tailwind.min.css">
                    </head>
                    <body>
                      <h1 class="text-3xl font-bold hover:underline text-red-600">$test0</h1>
                      <button id="colorButton" class="bg-blue-500">Cock</button>
                      <script>
                        var button = document.getElementById('colorButton');
                        button.addEventListener('click', function() {
                          if (button.classList.contains('bg-blue-500')) {
                            button.classList = ['bg-red-500'];
                          } else {
                            button.classList = ['bg-blue-500'];
                          }
                        });
                      </script>
                    </body>
                  </html>
                  """.stripMargin
              )
              prefWidth = 400
              prefHeight = 200
            }
          )
        }
      }
    }
  }
}
