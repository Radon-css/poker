package de.htwg.poker.util
import scala.io.Source
import de.htwg.poker.model.*

class HashEval {
  var playerHandRanks: List[(String, Int)] = List()

  def combinations(input: String): List[String] = {
    require(input.length == 7, "Input string must have exactly 7 characters")

    def combinationsHelper(chars: List[Char], length: Int): List[List[Char]] = {
      if (length == 0) List(Nil)
      else
        chars.flatMap(c =>
          combinationsHelper(chars.filterNot(_ == c), length - 1).map(c :: _)
        )
    }

    combinationsHelper(input.toList, 5).map(_.mkString)
  }
  def getHandRank(inputString: String): Option[Int] = {
    // Laden der Daten aus der Textdatei
    val lines = Source.fromFile("PokerHash.txt").getLines().toList

    // Überprüfen, ob der eingegebene String mit einem String aus der zweiten Spalte übereinstimmt
    val matchingRow =
      lines.find(line => line.split("\\s+").drop(1).mkString == inputString)

    // Extrahieren der Zahl aus der ersten Spalte der übereinstimmenden Zeile
    matchingRow.map(row => row.split("\\s+")(0).toInt)
  }

  def calcWinner(players: List[Player], boardCards: List[Card]): Player = {
    for (player <- players) {
      val playerID =
        players
          .map(player => player.card1.toString + player.card2.toString)
          .mkString
      val playerCards = List(player.card1, player.card2)
      val allCards = boardCards ++ playerCards
      val allCardsString =
        allCards.map(card => card.rank.toString(0)).mkString
      val allCombinations = combinations(allCardsString)
      val allPlayerHandRanks =
        allCombinations.map(combination => getHandRank(combination))
      val bestPlayerHandRank =
        allPlayerHandRanks.flatten.maxOption.getOrElse(-1)
      val playerTuple = (playerID, bestPlayerHandRank)
      playerHandRanks = playerTuple :: playerHandRanks
    }
    val winner: String = playerHandRanks.minBy(_._2)._1
    players(1)
  }
}
