package de.htwg.poker.model.CardsComponent.CardsMockImpl
import de.htwg.poker.model.CardsComponent.CardInterface
import de.htwg.poker.model.CardsComponent.DeckInterface
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Rank
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Suit
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card

trait CardsMockInterface extends CardInterface {
  override def toString: String = ""
  def CardToHtml: String = ""
  def SuitToHtml(suit: String): String = ""
}

trait DeckMockInterface extends DeckInterface {
  val deck: List[Card] = Nil

  def shuffleDeck: List[Card] = Nil

  def removeCards(deck: List[Card], n: Int): List[Card] = Nil
}
