package de.htwg.poker.util
import scala.io.Source
import de.htwg.poker.model.*

object Evaluator {

  var flushHash: List[(Int, String, Int)] = Nil
  var nonFlushHash: List[(Int, String, Int)] = Nil

  def readHashes = {
    val flushHashName = "src/main/scala/de/htwg/poker/util/FlushHash.txt"
    val nonFlushHashName = "src/main/scala/de/htwg/poker/util/nonFlushHash.txt"
    val sourceFlushHash = Source.fromFile(flushHashName)
    val sourceNonFlushHash = Source.fromFile(nonFlushHashName)

    val flushHashList = sourceFlushHash.getLines().toList.map { line =>
      val values = line.split(",")
      val value = values(0).trim.toInt
      val category = values(1).trim
      val id = values(2).trim.toInt
      (value, category, id)
    }

    val nonFlushHashList = sourceNonFlushHash.getLines().toList.map { line =>
      val values = line.split(",")
      val value = values(0).trim.toInt
      val category = values(1).trim
      val id = values(2).trim.toInt
      (value, category, id)
    }

    flushHash = flushHashList
    nonFlushHash = nonFlushHashList

    sourceFlushHash.close()
    sourceNonFlushHash.close()
  }

  def calcWinner(
      players: List[Player],
      boardCards: List[Card]
  ): List[Player] = {
    var playerHandRanks: List[(String, Int)] = List()
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
          val rank = binarySearch(flushHash, value)
          if (rank < bestRank)
            bestRank = rank
        } else {
          val rank = binarySearch(nonFlushHash, value)
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
