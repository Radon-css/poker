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

  def externalFunction(): Unit = {
    println("Externe Funktion aufgerufen!")
    // Hier können Sie den Code für die externe Funktion hinzufügen
  }

  override def start(): Unit = {
    val gameState = controller.gameState
    val test0 = "Cock"
    val button = new Button("Cock")

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
                          javafxBridge.externalFunction();
                        });
                      </script>
                    </body>
                  </html>
                  """.stripMargin
              )
              prefWidth = 400
              prefHeight = 200
              engine.executeScript(
                "window.javafxBridge = { externalFunction: function() { javafxBridge.externalFunction(); } }"
              )
            }
          )
        }
      }
    }
  }
}
