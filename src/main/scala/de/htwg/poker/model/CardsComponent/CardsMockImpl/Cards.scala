package de.htwg.poker.model.CardsComponent.CardsMockImpl
import de.htwg.poker.model.CardsComponent.CardInterface
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card

trait CardsMockInterface extends CardInterface {
  override def toString: String = ""
  def CardToHtml: String = ""
  def SuitToHtml(suit: String): String = ""
}
