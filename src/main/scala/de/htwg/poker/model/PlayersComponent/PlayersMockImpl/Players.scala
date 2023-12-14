package de.htwg.poker.model.PlayersComponent.PlayersMockImpl

import de.htwg.poker.model.PlayersComponent.PlayerInterface
import de.htwg.poker.model.CardsComponent.CardInterface as Card

trait PlayerInterface {
  def card1: Card
  def card2: Card
  def playername: String
  def balance: Int
  def currentAmountBetted: Int

  def balanceToString(): String
  def toHtml: String
}

trait PlayersMockInterface extends PlayerInterface {
  def card1: Card = null
  def card2: Card = null
  def playername: String = ""
  def balance: Int = 0
  def currentAmountBetted: Int = 0
  def balanceToString(): String = ""
  def toHtml: String = ""

}
