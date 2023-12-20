package de.htwg.poker.model.PlayersComponent

import de.htwg.poker.model.CardsComponent.CardInterface as Card

trait PlayerInterface {
  def card1: Card
  def card2: Card
  def playername: String
  def balance: Int
  def currentAmountBetted: Int
  def createPlayer(
      card1: Card,
      card2: Card,
      playername: String,
      balance: Int,
      currentAmountBetted: Int
  ): PlayerInterface
  def balanceToString(): String
  def toHtml: String
}
