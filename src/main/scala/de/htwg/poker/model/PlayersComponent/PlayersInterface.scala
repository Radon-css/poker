package de.htwg.poker.model.PlayersComponent

import de.htwg.poker.model.CardsComponent.CardInterface as Card

trait PlayerInterface {
  def Player(
      card1: Card,
      card2: Card,
      playername: String,
      balance: Int,
      currentAmountBetted: Int
  ): Unit

  def balanceToString(): String
  def toHtml: String
}
