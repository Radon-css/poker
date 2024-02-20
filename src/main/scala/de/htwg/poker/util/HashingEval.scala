package de.htwg.poker.util
import scala.io.Source
import de.htwg.poker.model.*

class HashEval {

  var playerHandRanks: List[(String, Int)] = List()

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

  def calcWinner(players: List[Player], boardCards: List[Card]): Player = {
    for (player <- players) {
      val playerID =
        players
          .map(player => player.card1.toString + player.card2.toString)
          .mkString
      val playerCards = List(player.card1, player.card2)
      val cards = boardCards ++ playerCards
      val combinations = getCombinations(5, cards)
      val uniqueIntegers =
        combinations.map(combination => combination.map(_.rank.prime).product)

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
      ) {} else {}
    }
    players(1)
  }

  def binarySearch(list: List[(Int, String, Int)], target: Int): Option[Int] = {
    def binarySearchR(low: Int, high: Int): Option[Int] = {
      if (low > high) {
        None // Element nicht gefunden
      } else {
        val mid = low + (high - low) / 2
        val midValue = list(mid)._3

        if (midValue == target) {
          Some(list(mid)._1) // Element gefunden
        } else if (midValue < target) {
          binarySearchR(mid + 1, high)
        } else {
          binarySearchR(low, mid - 1)
        }
      }
    }

    val sortedList =
      list.sortBy(_._3) // Liste nach dem dritten Eintrag sortieren
    binarySearch(0, sortedList.length - 1)
  }

}
