package controllers

import scala.swing.Publisher
import scala.swing.event.Event
import de.htwg.poker.controller.Controller

// Define an event class
case class GameEvent(eventType: String)

// Wrapper class for the Controller
class PokerControllerPublisher(val controller: Controller) extends Publisher {

  def gameState = controller.gameState

  def createGame(
      players: List[String],
      smallBlind: String,
      bigBlind: String
  ): Unit = {
    controller.createGame(players, smallBlind, bigBlind)
    publish(
      new Event { override def toString: String = "GameCreated" }
    ) // Publish an event when a new game is created
  }

  def bet(amount: Int): Unit = {
    controller.bet(amount)
    publish(new Event {
      override def toString: String = "BetPlaced"
    }) // Publish an event when a bet is placed
  }

  // Add methods for other actions, publishing appropriate events
  def allIn(): Unit = {
    controller.allIn()
    publish(new Event {
      override def toString: String = "AllIn"
    }) // Publish an event for all-in action
  }


  def fold(): Unit = {
    println("fold Event")
    controller.fold
    publish(new Event {
      override def toString: String = "Folded"
    }) // Publish an event for fold action
  }

  def call(): Unit = {
    controller.call
    publish(new Event {
      override def toString: String = "Called"
    }) // Publish an event for call action
  }

  def check(): Unit = {
    controller.check
    publish(new Event {
      override def toString: String = "Checked"
    }) // Publish an event for check action
  }

  def leave(): Unit = {
    publish(new Event {
      override def toString: String = "Left"
    }) // Publish an event for leave action
  }

  def restartGame(): Unit = {
    controller.restartGame
    publish(new Event {
      override def toString: String = "GameRestarted"
    }) // Publish an event for game restart
  }

  def lobby(): Unit = {
    publish(new Event {
      override def toString: String = "Lobby"
    })
  }

  def connectionEvent(): Unit = {
    publish(new Event {
      override def toString: String = "ConnectionEvent"
    })
  }

  // Add more methods as needed for your application
}
