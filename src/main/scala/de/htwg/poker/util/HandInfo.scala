package de.htwg.poker.util

import de.htwg.poker.model.*

/* this class contains an Algorithm that can evaluate Poker Hands. It compares the two Cards the player
  holds with the community Cards that are currently revealed to find the combination of player cards and community cards that is worth most.
   The Evaluator is called whenever a new Player is at turn to show what Kind of Hand a player currently holds (High, Pair, TwoPair, Triples
  , Straight, Flush etc...). A Flush is worth more than a Straight which is worth more than triples etc...
  Furthermore, the Evaluator decides which player wins the pot at the end of every round.*/

object HandInfo {

  enum Type:
    case High, Pair, TwoPair, Triples, Straight, Flush, FullHouse, Quads,
      StraightFlush
    def strength: Int = this match {
      case High          => 1
      case Pair          => 2
      case TwoPair       => 3
      case Triples       => 4
      case Straight      => 5
      case Flush         => 6
      case FullHouse     => 7
      case Quads         => 8
      case StraightFlush => 9
    }

  def evaluate(playerCards: List[Card], boardCards: List[Card]): String = {
    val (bestHand, bestType) = evalHand(playerCards, boardCards)
    bestType.toString
  }

  def evalHand(
      playerCards: List[Card],
      boardCards: List[Card]
  ): (List[Card], Type) = {
    val allCards = boardCards ++ playerCards
    val allCombinations = combinations(5, allCards)
    var bestHand: List[Card] = boardCards
    var bestType: Type = Type.High

    for (combination <- allCombinations) {
      val currentType = evalFiveCards(combination)
      // hier fehlen noch edge cases wie flush bei 6 Karten
      if (currentType.strength > bestType.strength)
        bestHand = combination
        bestType = currentType
    }
    (bestHand, bestType)
  }

  def evalFiveCards(combination: List[Card]): Type = {

    var returnValue = Type.High

    val rankHistogramm: List[(Rank, Int)] = combination
      .map(_.rank)
      .groupBy(identity)
      .mapValues(_.size)
      .toList
      .sortBy(-_._2)

    // check for Pair
    if (rankHistogramm.size == 4)
      returnValue = Type.Pair

    // check for TwoPair
    if (
      rankHistogramm(0)._2 == 2 && rankHistogramm(
        1
      )._2 == 2 && rankHistogramm(
        2
      )._2 == 1
    )
      returnValue = Type.TwoPair

      // check for Triples
      if (
        rankHistogramm(0)._2 == 3 && rankHistogramm(
          1
        )._2 == 1 && rankHistogramm(
          2
        )._2 == 1
      )
        returnValue = Type.Triples

    // check for Straights
    val sortedCards = combination.sorted(Ordering.by(_.rank.strength)).reverse
    if (
      sortedCards.head.rank.strength == sortedCards(1).rank.strength + 1
      && sortedCards(1).rank.strength == sortedCards(2).rank.strength + 1
      && sortedCards(2).rank.strength == sortedCards(3).rank.strength + 1
      && sortedCards(3).rank.strength == sortedCards(4).rank.strength + 1
      || (sortedCards.head.rank == Rank.Ace && sortedCards(
        1
      ).rank == Rank.Five && sortedCards(2).rank == Rank.Four && sortedCards(
        3
      ).rank == Rank.Three && sortedCards(
        4
      ).rank == Rank.Two)
    )
      returnValue = Type.Straight

      // check for Flush
    if (
      combination(0).suit.id == combination(1).suit.id && combination(
        1
      ).suit.id == combination(2).suit.id && combination(
        2
      ).suit.id == combination(
        3
      ).suit.id && combination(3).suit.id == combination(
        4
      ).suit.id
    )
      returnValue = Type.Flush

      // check for FullHouse
      if (rankHistogramm(0)._2 == 3 && rankHistogramm(1)._2 == 2)
        returnValue = Type.FullHouse

      // check for Quads
      if (rankHistogramm(0)._2 == 4 && rankHistogramm(1)._2 == 1)
        returnValue = Type.Quads
    // check for StraightFlush
    if (
      combination(0).suit.id == combination(1).suit.id && combination(
        1
      ).suit.id == combination(2).suit.id && combination(
        2
      ).suit.id == combination(
        3
      ).suit.id && combination(3).suit.id == combination(
        4
      ).suit.id && (
        sortedCards.head.rank.strength == sortedCards(1).rank.strength + 1
          && sortedCards(1).rank.strength == sortedCards(2).rank.strength + 1
          && sortedCards(2).rank.strength == sortedCards(3).rank.strength + 1
          && sortedCards(3).rank.strength == sortedCards(4).rank.strength + 1
          || (sortedCards.head.rank == Rank.Ace && sortedCards(
            1
          ).rank == Rank.Five && sortedCards(
            2
          ).rank == Rank.Four && sortedCards(
            3
          ).rank == Rank.Three && sortedCards(
            4
          ).rank == Rank.Two)
      )
    )
      returnValue = Type.StraightFlush
    returnValue
  }

  // Funktion um alle möglichen 5-Karten-Kombinationen meiner 7 Karten (2 Spielerkarten + 5 Boardkarten) zu bekommen
  def combinations[T](n: Int, lst: List[T]): List[List[T]] = {
    if (n == 0) List(Nil)
    else
      lst match {
        case Nil => Nil
        case head :: tail =>
          combinations(n - 1, tail).map(head :: _) ++ combinations(n, tail)
      }
  }
}
