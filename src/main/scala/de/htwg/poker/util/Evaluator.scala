package de.htwg.poker.util

import de.htwg.poker.model.*

@main
def run(): Unit = {
  val evaluator = new Evaluator
  evaluator.evaluate(
    List(new Card(Suit.Hearts, Rank.Jack), new Card(Suit.Hearts, Rank.Two)),
    List(
      new Card(Suit.Hearts, Rank.Ace),
      new Card(Suit.Hearts, Rank.Four),
      new Card(Suit.Spades, Rank.Five),
      new Card(Suit.Spades, Rank.Ten),
      new Card(Suit.Spades, Rank.Six)
    )
  )
}

class Evaluator() {

  /*val boardCards: List[Card] = gameState.getBoard
  val playerCards: Map[Player, (Card, Card)] = gameState.getPlayers
    .map(player => player -> (player.card1, player.card2))
    .toMap*/

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

  def evaluate(playerCards: List[Card], boardCards: List[Card]): Unit = {
    val (bestHand, bestType) = evalHand(playerCards, boardCards)
    for (card <- bestHand) {
      println(card.toString)
    }
    println(bestType.toString())
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

    val rankHistogramm: List[(Rank, Int)] = combination
      .map(_.rank)
      .groupBy(identity)
      .mapValues(_.size)
      .toList
      .sortBy(-_._2)

    // check for Quads, FullHouse, Trips, TwoPair, Pair
    if (rankHistogramm(0)._2 == 4 && rankHistogramm(1)._2 == 1)
      return Type.Quads
    if (rankHistogramm(0)._2 == 3 && rankHistogramm(1)._2 == 2)
      return Type.FullHouse
    if (
      rankHistogramm(0)._2 == 3 && rankHistogramm(
        1
      )._2 == 1 && rankHistogramm(
        2
      )._2 == 1
    )
      return Type.Triples
    if (
      rankHistogramm(0)._2 == 2 && rankHistogramm(
        1
      )._2 == 2 && rankHistogramm(
        2
      )._2 == 1
    )
      return Type.TwoPair
    if (rankHistogramm.size == 4)
      return Type.Pair
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
      return Type.Flush
    // check for Straights
    val sortedCards = combination.sorted(Ordering.by(_.rank.strength)).reverse
    if (
      sortedCards.head.rank.strength - sortedCards.last.rank.strength == 4 || sortedCards.head.rank == Rank.Ace && sortedCards(
        1
      ).rank == Rank.Five
    )
      return Type.Straight
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
        sortedCards.head.rank.strength - sortedCards.last.rank.strength == 4 || sortedCards.head.rank == Rank.Ace && sortedCards(
          1
        ) == Rank.Five
      )
    )
      return Type.StraightFlush
    return Type.High
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

  def compareHands(hands: List[(List[Card], Type)]): List[Card] = {
    val types: List[Type] = hands.map(_._2)
    val highestType: Type = types.maxBy(_.strength)
    val handsWithHighestType: List[List[Card]] =
      hands.filter(_._2 == highestType).map(_._1)

    if (handsWithHighestType.size == 1)
      return handsWithHighestType.head
    if (highestType == Type.High)
      return getHighestKicker(handsWithHighestType)
    hands.head._1
  }

  def getHighestKicker(cards: List[List[Card]]): List[Card] = {
    val sortedCardLists =
      cards.map(list => list.sorted(Ordering.by(_.rank.strength)).reverse)
    val highestKickerList =
      cards.reduceLeft((acc, current) => {
        val maxAcc = acc.map(_.rank.strength).max
        val maxCurrent = current.map(_.rank.strength).max

        if (maxAcc > maxCurrent) acc
        else if (maxAcc < maxCurrent) current
        else Nil
      })
    highestKickerList
  }
}
