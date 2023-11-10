package de.htwg.poker.model
import scala.util.Random

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield Card(suit, rank)
}

val shuffledDeck: List[Card] = Random.shuffle(deck)

def removeCards(deck: List[Card], n: Int): List[Card] = {
  val newCardList = deck.drop(n);
  newCardList
}
