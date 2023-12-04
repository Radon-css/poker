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

object ScalaFXHelloWorld extends JFXApp3 {
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Hello World"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new HBox {
          padding = Insets(50, 80, 50, 80)
          children = Seq(
            new Text {
              text = "Scala"
              style = "-fx-font: normal bold 100pt sans-serif"
              fill = new LinearGradient(endX = 0, stops = Stops(Red, DarkRed))
            },
            new WebView {
              engine.loadContent("""
                  <html>
                    <head>
                      <link rel="stylesheet" type="text/css" href="https://unpkg.com/tailwindcss@%5E1.0/dist/tailwind.min.css">
                    </head>
                    <body>
                      <h1 class="text-3xl font-bold hover:underline text-red-600">Hello, HTML in ScalaFX!</h1>
                  <Button> Cock </Button>
                    </body>
                  </html>
                  """.stripMargin)
              prefWidth = 400
              prefHeight = 200
            }
          )
        }
      }
    }
  }
}
