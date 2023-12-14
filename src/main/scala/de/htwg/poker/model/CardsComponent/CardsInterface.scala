package de.htwg.poker.model.CardsComponent

import CardsBaseImpl.*

trait CardInterface {
  def suit: Suit
  def rank: Rank
  def toString: String
  def CardToHtml: String
  def SuitToHtml(suit: Suit): String
}

trait DeckInterface {
  def deck: List[Card]
  def shuffleDeck: List[Card]
  def removeCards(deck: List[Card], n: Int): List[Card]
}
