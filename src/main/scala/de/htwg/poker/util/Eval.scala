package de.htwg.poker.util
import scala.io.Source
import de.htwg.poker.model.*
import de.htwg.poker.util.Flush
import de.htwg.poker.util.NotFlush2
import de.htwg.poker.util.NotFlush1
import de.htwg.poker.util.NotFlush3
import de.htwg.poker.util.NotFlush4

object Eval {

  def calcWinner(
      players: List[Player],
      boardCards: List[Card]
  ): List[Player] = {
    var playerHandRanks: List[(String, Int)] = List()
    println("Hallo")
    for (player <- players) {
      var bestRank = Integer.MAX_VALUE
      val playerCards = List(player.card1, player.card2)
      val cards = boardCards ++ playerCards
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
      playerHandRanks = playerHandRanks :+ (player.playername, bestRank)
    }
    val bestRank = playerHandRanks.map(_._2).min
    val winners = playerHandRanks.filter { case (_, rank) =>
      rank == bestRank
    }
    val winnerPlayers = players.filter { case player =>
      winners.exists { case (str, _) =>
        str == player.playername
      }
    }
    for (player <- winnerPlayers)
      println(player.playername)
    winnerPlayers
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
