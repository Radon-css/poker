package de.htwg.poker.model
import scala.util.Random

val deck: List[Card] = {
  for {
    rank <- Rank.values.toList
    suit <- Suit.values.toList
  } yield new Card(suit, rank)
}

def shuffleDeck: List[Card] = {
  val random = new Random
  random.shuffle(deck)
}

def removeCards(deck: List[Card], n: Int): List[Card] = {
  val newCardList = deck.drop(n);
  newCardList
}
