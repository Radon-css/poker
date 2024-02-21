package de.htwg.poker.util
import scala.io.Source
import de.htwg.poker.model.*
import de.htwg.poker.util.Flush
import de.htwg.poker.util.NotFlush2
import de.htwg.poker.util.NotFlush1

class HashEval {

  def calcWinner(players: List[Player], boardCards: List[Card]): Player = {
    var playerHandRanks: List[(String, Int)] = List()

    for (player <- players) {
      var bestRank = Integer.MAX_VALUE

      val playerID =
        players
          .map(player => player.card1.toString + player.card2.toString)
          .mkString
      val playerCards = List(player.card1, player.card2)
      val cards = boardCards ++ playerCards
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
          val rank = binarySearch(Flush.flush, value)
          if (rank < bestRank)
          bestRank = rank
        } else if (value <= 437255){
          val rank = binarySearch(NotFlush1.notflush1, value)
          if (rank < bestRank)
          bestRank = rank
        } else {
          val rank = binarySearch(NotFlush2.notflush2, value)
          if (rank < bestRank)
          bestRank = rank
        }
      }

    }
    players(1)
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
