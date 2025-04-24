package de.htwg.poker.eval

import de.htwg.poker.eval.types.EvalCard
import de.htwg.poker.eval.types.EvalRank
import de.htwg.poker.eval.types.EvalSuit
import de.htwg.poker.eval.types.EvalRank.*
import de.htwg.poker.eval.types.EvalSuit.*

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

  def evalHand(playerCards: List[EvalCard], boardCards: List[EvalCard]): String = {
    val (bestHand, bestType) = evalHand0(playerCards, boardCards)
    bestType.toString
  }

  def evalHand0(
      playerCards: List[EvalCard],
      boardCards: List[EvalCard]
  ): (List[EvalCard], Type) = {
    val allCards = boardCards ++ playerCards
    val allCombinations = combinations(5, allCards)
    var bestHand: List[EvalCard] = boardCards
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

  def evalFiveCards(combination: List[EvalCard]): Type = {

    var returnValue = Type.High

    // chaining
    val rankHistogramm: List[(EvalRank, Int)] = combination
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
    val sortedCards =
      combination.sortBy(card => -getStrength(card.rank))
    if (
      getStrength(sortedCards.head.rank) == getStrength(sortedCards(1).rank) + 1
      && getStrength(sortedCards(1).rank) == getStrength(
        sortedCards(2).rank
      ) + 1
      && getStrength(sortedCards(2).rank) == getStrength(
        sortedCards(3).rank
      ) + 1
      && getStrength(sortedCards(3).rank) == getStrength(
        sortedCards(4).rank
      ) + 1
      || (sortedCards.head.rank == EvalRank.Ace && sortedCards(
        1
      ).rank == EvalRank.Five && sortedCards(2).rank == EvalRank.Four && sortedCards(
        3
      ).rank == EvalRank.Three && sortedCards(
        4
      ).rank == EvalRank.Two)
    )
      returnValue = Type.Straight

      // check for Flush
    if (
      getId(combination(0).suit) == getId(combination(1).suit) && getId(
        combination(
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
      getId(combination(0).suit) == getId(combination(1).suit) && getId(
        combination(
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
      ) && (
        getStrength(sortedCards.head.rank) == getStrength(
          sortedCards(1).rank
        ) + 1
          && getStrength(sortedCards(1).rank) == getStrength(
            sortedCards(2).rank
          ) + 1
          && getStrength(sortedCards(2).rank) == getStrength(
            sortedCards(3).rank
          ) + 1
          && getStrength(sortedCards(3).rank) == getStrength(
            sortedCards(4).rank
          ) + 1
          || (sortedCards.head.rank == EvalRank.Ace && sortedCards(
            1
          ).rank == EvalRank.Five && sortedCards(
            2
          ).rank == EvalRank.Four && sortedCards(
            3
          ).rank == EvalRank.Three && sortedCards(
            4
          ).rank == EvalRank.Two)
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

  def getStrength(rank: EvalRank): Int = rank match {
    case Two   => 1
    case Three => 2
    case Four  => 3
    case Five  => 4
    case Six   => 5
    case Seven => 6
    case Eight => 7
    case Nine  => 8
    case Ten   => 9
    case Jack  => 10
    case Queen => 11
    case King  => 12
    case Ace   => 13
  }

  def getId(suit: EvalSuit): Int = suit match {
    case Clubs    => 1
    case Spades   => 2
    case Diamonds => 3
    case Hearts   => 4
  }
}
