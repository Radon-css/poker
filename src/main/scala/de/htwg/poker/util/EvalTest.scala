package de.htwg.poker.util

import scala.io.Source
import de.htwg.poker.model.*
import de.htwg.poker.util.NotFlush2
import de.htwg.poker.util.NotFlush1
import de.htwg.poker.util.NotFlush3
import de.htwg.poker.util.NotFlush4
import de.htwg.poker.util.Flush

@main
def run2: Unit = {
  val playerCards = List(
    (Card(Suit.Spades, Rank.Ace), Card(Suit.Spades, Rank.Four)),
    (Card(Suit.Hearts, Rank.Five), Card(Suit.Hearts, Rank.Four)),
    (Card(Suit.Diamonds, Rank.Three), Card(Suit.Hearts, Rank.Ten))
  )
  val boardCards = List(
    Card(Suit.Spades, Rank.Three),
    Card(Suit.Spades, Rank.Jack),
    Card(Suit.Hearts, Rank.Queen),
    Card(Suit.Clubs, Rank.Seven),
    Card(Suit.Spades, Rank.Nine)
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

      for (combination <- combinations) {
        val value = combination.map(_.rank.prime).product
        if (
          combination(0).suit.id == combination(1).suit.id && cards(
            1
          ).suit.id == combination(2).suit.id && combination(
            2
          ).suit.id == combination(
            3
          ).suit.id && combination(3).suit.id == combination(
            4
          ).suit.id
        ) {
          val rank = binarySearch(Flush.flush, value)
          if (rank < bestRank)
            bestRank = rank
        } else if (value <= 78625) {
          val rank = binarySearch(NotFlush1.notflush1, value)
          if (rank < bestRank)
            bestRank = rank
        } else if (value <= 437437) {
          val rank = binarySearch(NotFlush2.notflush2, value)
          if (rank < bestRank)
            bestRank = rank
        } else if (value <= 2052665) {
          val rank = binarySearch(NotFlush3.notflush3, value)
          if (rank < bestRank)
            bestRank = rank
        } else {
          val rank = binarySearch(NotFlush4.notflush4, value)
          if (rank < bestRank)
            bestRank = rank
        }
      }
    }
  }

  def binarySearch(list: List[(Int, String, Int)], target: Int): Int = {
    def binarySearchR(low: Int, high: Int): Int = {
      if (low <= high) {
        val mid = low + (high - low) / 2
        val midValue = list(mid)._3
        if (midValue == target) {
          list(mid)._1 // Element gefunden
        } else if (midValue < target) {
          binarySearchR(mid + 1, high)
        } else {
          binarySearchR(low, mid - 1)
        }
      } else {
        if (target < list.head._3) {
          list.head._1 // Zielnummer kleiner als das erste Element
        } else {
          list.last._1 // Zielnummer größer als das letzte Element
        }
      }
    }

    binarySearchR(0, list.length - 1)
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
