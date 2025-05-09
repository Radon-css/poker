package de.htwg.poker.eval
import scala.io.Source
import scala.math.Ordering

import de.htwg.poker.eval.types.EvalCard
import de.htwg.poker.eval.types.EvalPlayer

import de.htwg.poker.eval.types.EvalRank
import de.htwg.poker.eval.types.EvalSuit
import de.htwg.poker.eval.types.EvalRank.*
import de.htwg.poker.eval.types.EvalSuit.*

object Evaluator {

  val categoryComparator = new Ordering[String] {
    val orderList =
      List("HC", "1P", "2P", "3K", "S", "F", "FH", "4K", "SF")
    private val orderMap = orderList.zipWithIndex.toMap
    def compare(x: String, y: String): Int = {
      val xIndex = orderMap.getOrElse(
        x,
        -1
      )
      val yIndex = orderMap.getOrElse(y, -1)
      xIndex.compare(yIndex)
    }
  }

  var flushHash: List[(Int, String, Int)] = Nil
  var nonFlushHash: List[(Int, String, Int)] = Nil

  def readHashes = {
    val sourceFlushHash = Source.fromResource("FlushHash.txt")
    val sourceNonFlushHash = Source.fromResource("NonFlushHash.txt")

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
      players: List[EvalPlayer],
      boardCards: List[EvalCard]
  ): List[EvalPlayer] = {
    if (flushHash.isEmpty || nonFlushHash.isEmpty) {
      readHashes
    }

    var playerHandRanks: List[(String, String, Int)] = List()
    for (player <- players) {
      var bestRank = Integer.MAX_VALUE
      var bestCategory = "HC"
      val playerCards = List(player.card1, player.card2)
      val cards = boardCards ++ playerCards
      val combinations = getCombinations[EvalCard](5, cards)

      for (combination <- combinations) {
        val value = combination.map(card => getPrime(card.rank)).product[Int]
        if (
          getId(combination(0).suit) == getId(combination(1).suit) && getId(
            cards(
              1
            ).suit
          ) == getId(combination(2).suit) && getId(
            combination(
              2
            ).suit
          ) == getId(
            combination(
              3
            ).suit
          ) && getId(combination(3).suit) == getId(
            combination(
              4
            ).suit
          )
        ) {
          val (rank, category) = binarySearch(flushHash, value)
          if (
            rank < bestRank && categoryComparator.compare(
              category,
              bestCategory
            ) >= 0
          )
            bestRank = rank
            bestCategory = category
        } else {
          val (rank, category) = binarySearch(nonFlushHash, value)
          if (
            rank < bestRank && categoryComparator.compare(
              category,
              bestCategory
            ) >= 0
          )
            bestRank = rank
            bestCategory = category
        }
      }
      playerHandRanks =
        playerHandRanks :+ (player.playername, bestCategory, bestRank)
    }
    // chaining
    val bestRank = playerHandRanks.map(_._3).min
    val bestCategory = playerHandRanks.map(_._2).max(categoryComparator)
    val playersWithBestCategory = playerHandRanks.filter {
      case (name, category, rank) =>
        category == bestCategory
    }
    var winners = playersWithBestCategory
    if (playersWithBestCategory.length != 1) {
      winners = playersWithBestCategory.filter { case (name, category, rank) =>
        rank == bestRank
      }
    }

    val winnerPlayers = players.filter { case player =>
      winners.exists { case (str, _, _) =>
        str == player.playername
      }
    }
    winnerPlayers
  }

  def binarySearch(
      list: List[(Int, String, Int)],
      target: Int
  ): (Int, String) = {
    def binarySearchR(low: Int, high: Int): (Int, String) = {
      val mid = low + (high - low) / 2
      val midValue = list(mid)._3
      if (midValue == target) {
        (list(mid)._1, list(mid)._2) // Element gefunden
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

  def getId(suit: EvalSuit): Int = suit match {
    case Clubs    => 1
    case Spades   => 2
    case Diamonds => 3
    case Hearts   => 4
  }

  def getPrime(rank: EvalRank): Int = rank match {
    case Two   => 2
    case Three => 3
    case Four  => 5
    case Five  => 7
    case Six   => 11
    case Seven => 13
    case Eight => 17
    case Nine  => 19
    case Ten   => 23
    case Jack  => 29
    case Queen => 31
    case King  => 37
    case Ace   => 41
  }
}
