package de.htwg.poker.util

import scala.io.Source
import de.htwg.poker.model.*
import de.htwg.poker.util.Ranks

@main
def run2: Unit = {
  val playerCards = List(
    (Card(Suit.Spades, Rank.Ace), Card(Suit.Clubs, Rank.Four)),
    (Card(Suit.Hearts, Rank.Five), Card(Suit.Hearts, Rank.Four)),
    (Card(Suit.Diamonds, Rank.Three), Card(Suit.Hearts, Rank.Ten))
  )
  val boardCards = List(
    Card(Suit.Spades, Rank.Three),
    Card(Suit.Spades, Rank.Jack),
    Card(Suit.Hearts, Rank.Queen),
    Card(Suit.Clubs, Rank.Seven),
    Card(Suit.Diamonds, Rank.Nine)
  )

  val evalTest = new EvalTest

  evalTest.calcWinner(playerCards, boardCards)
}

class EvalTest {

  def calcWinner(
      playerCards: List[(Card, Card)],
      boardCards: List[Card]
  ): Unit = {

    for (playerHand <- playerCards) {
      var bestRank = Integer.MAX_VALUE

      val cards = boardCards ++ playerHand.toList
      val combinations = getCombinations(5, cards)
      val uniqueValues =
        combinations.map(combination => combination.map(_.rank.prime).product)

      for (value <- uniqueValues) {
        if (
          cards(0).suit.id == cards(1).suit.id && cards(
            1
          ).suit.id == cards(2).suit.id && cards(
            2
          ).suit.id == cards(
            3
          ).suit.id && cards(3).suit.id == cards(
            4
          ).suit.id
        ) {
          val rank = binarySearch(Ranks.flush, value)
          if (rank < bestRank)
            bestRank = rank
        } else {
          val rank = binarySearch(Ranks.notflush, value)
          if (rank < bestRank)
            bestRank = rank
        }
      }
      println(bestRank)
    }
  }

  def binarySearch(list: List[(Int, String, Int)], target: Int): Int = {
    def binarySearchR(low: Int, high: Int): Int = {
      val mid = low + (high - low) / 2
      val midValue = list(mid)._3
      if (midValue == target) {
        list(mid)._1 // Element gefunden
      } else if (midValue < target) {
        binarySearchR(mid + 1, high)
      } else {
        binarySearchR(low, mid - 1)
      }
    }

    val sortedList =
      list.sortBy(_._3) // Liste nach dem dritten Eintrag sortieren
    binarySearchR(0, sortedList.length - 1)
  }

  def getCombinations[T](n: Int, lst: List[T]): List[List[T]] = {
    if (n == 0) List(Nil)
    else
      lst match {
        case Nil => Nil
        case head :: tail =>
          getCombinations(n - 1, tail).map(head :: _) ++ getCombinations(
            n,
            tail
          )
      }
  }

}
