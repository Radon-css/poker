package de.htwg.poker
package aview
import scala.swing._
import controller.Controller

import java.awt.Color

class GUI(controller: Controller) extends Frame {
  title = "Poker"
  listenTo(controller)

  def table: BoxPanel = new BoxPanel(Orientation.Horizontal) {
    background = java.awt.Color.GREEN
    preferredSize = new Dimension(400, 300)
  }
  contents = new FlowPanel {
    contents += table
    preferredSize = new Dimension(600, 400)
  }
  centerOnScreen()
  open()
}
