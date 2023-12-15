package de.htwg.poker.model
package CardsComponent.CardsAdvancedImpl

import scala.util.Random
import de.htwg.poker.model.CardsComponent.CardInterface
import de.htwg.poker.model.CardsComponent.DeckInterface
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit

class Card(val suit: Suit, val rank: Rank) extends CardInterface {
  override def toString: String = "[" + rank.toString + suit.toString + "]"
  def CardToHtml: String = {
    s"<div class=\"rounded-lg bg-slate-700 w-6 h-9 hover:scale-125 flex flex-col justify-center items-center shadow-xl shadow-black/50\">${SuitToHtml(suit)}<h1 class=\"font-bold \">${rank.toString}</h1></div>"
  }

  def SuitToHtml(suit: Suit): String =
    suit match {
      case Suit.Clubs =>
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-club-fill\" viewBox=\"0 0 16 16\"><path d=\"M11.5 12.5a3.493 3.493 0 0 1-2.684-1.254 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907 3.5 3.5 0 1 1-2.538-5.743 3.5 3.5 0 1 1 6.708 0A3.5 3.5 0 1 1 11.5 12.5\"/></svg>"
      case Suit.Spades =>
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-spade-fill\" viewBox=\"0 0 16 16\"><path d=\"M7.184 11.246A3.5 3.5 0 0 1 1 9c0-1.602 1.14-2.633 2.66-4.008C4.986 3.792 6.602 2.33 8 0c1.398 2.33 3.014 3.792 4.34 4.992C13.86 6.367 15 7.398 15 9a3.5 3.5 0 0 1-6.184 2.246 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907\"/></svg>"
      case Suit.Diamonds =>
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-diamond-fill\" viewBox=\"0 0 16 16\"><path fill-rule=\"evenodd\" d=\"M6.95.435c.58-.58 1.52-.58 2.1 0l6.515 6.516c.58.58.58 1.519 0 2.098L9.05 15.565c-.58.58-1.519.58-2.098 0L.435 9.05a1.482 1.482 0 0 1 0-2.098L6.95.435z\"/></svg>"
      case Suit.Hearts =>
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-suit-heart-fill\" viewBox=\"0 0 16 16\"><path d=\"M4 1c2.21 0 4 1.755 4 3.92C8 2.755 9.79 1 12 1s4 1.755 4 3.92c0 3.263-3.234 4.414-7.608 9.608a.513.513 0 0 1-.784 0C3.234 9.334 0 8.183 0 4.92 0 2.755 1.79 1 4 1\"/></svg>"
    }
}
