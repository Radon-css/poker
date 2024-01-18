package de.htwg.poker.util

import de.htwg.poker.model.*

/* this class contains an Algorithm that can evaluate Poker Hands. It compares the two Cards the player
  holds with the community Cards that are currently revealed to find the combination of player cards and community cards that is worth most.
   The Evaluator is called whenever a new Player is at turn to show what Kind of Hand a player currently holds (High, Pair, TwoPair, Triples
  , Straight, Flush etc...). A Flush is worth more than a Straight which is worth more than triples etc...
  Furthermore, the Evaluator decides which player wins the pot at the end of every round.*/

@main
def run: Unit = {
  val cards1 = (
    List(
      Card(Suit.Spades, Rank.Two),
      Card(Suit.Spades, Rank.Three),
      Card(Suit.Spades, Rank.Four),
      Card(Suit.Spades, Rank.Five)
    ),
    0
  )
  val cards2 = (
    List(
      Card(Suit.Spades, Rank.Two),
      Card(Suit.Spades, Rank.Three),
      Card(Suit.Spades, Rank.Four),
      Card(Suit.Spades, Rank.Ace)
    ),
    1
  )
  val evaluator = new Evaluator
  print(evaluator.getHighestKicker(List(cards1, cards2)))
}

class Evaluator() {

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

    // check for Quads, FullHouse, Trips, TwoPair, Pair
    if (rankHistogramm(0)._2 == 4 && rankHistogramm(1)._2 == 1)
      returnValue = Type.Quads
    if (rankHistogramm(0)._2 == 3 && rankHistogramm(1)._2 == 2)
      returnValue = Type.FullHouse
    if (
      rankHistogramm(0)._2 == 3 && rankHistogramm(
        1
      )._2 == 1 && rankHistogramm(
        2
      )._2 == 1
    )
      returnValue = Type.Triples
    if (
      rankHistogramm(0)._2 == 2 && rankHistogramm(
        1
      )._2 == 2 && rankHistogramm(
        2
      )._2 == 1
    )
      returnValue = Type.TwoPair
    if (rankHistogramm.size == 4)
      returnValue = Type.Pair
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
    // check for Straights
    val sortedCards = combination.sorted(Ordering.by(_.rank.strength)).reverse
    if (
      sortedCards.head.rank.strength - sortedCards.last.rank.strength == 4 || sortedCards.head.rank == Rank.Ace && sortedCards(
        1
      ).rank == Rank.Five
    )
      returnValue = Type.Straight
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

  def getWinner(players: List[Player], boardCards: List[Card]): List[Card] = {
    val playerCards = players.map(player => (player.card1, player.card2))
    val hands =
      playerCards.map(playerCards => evalHand(playerCards.toList, boardCards))
    val handsAndTypesWithIndexes = hands.zipWithIndex
    val types = hands.map(_._2)
    val highestType = types.maxBy(_.strength)
    val handsWithHighestTypeWithType =
      handsAndTypesWithIndexes.filter(_._1._2 == highestType)
    val handsWithHighestType =
      handsWithHighestTypeWithType.map(hand => (hand._1._1, hand._2))

    /*if(handsWithHighestType.size == 1)
      return getHighestKicker(handsWithHighestType)

    if()
    val types: List[Type] = hands.map(_._2)
    val highestType: Type = types.maxBy(_.strength)
    val handsWithHighestType: List[List[Card]] =
      hands.filter(_._2 == highestType).map(_._1)

    if (handsWithHighestType.size == 1)
      return handsWithHighestType.head
    if (highestType == Type.High)
      return getHighestKicker(handsWithHighestType)
    hands.head._1*/
    Nil
  }

  def getHighestKicker(hands: List[(List[Card], Int)]): Int = {
    def sortCardsInHand(hand: (List[Card], Int)): (List[Card], Int) = {
      val index = hand._2
      val cards = hand._1
      val sortedCards = cards.sortWith(_.rank.strength > _.rank.strength)
      (sortedCards, index)
    }
    def findHighestCard(hand: List[Card]): Card = {
      hand.maxBy(_.rank.strength)
    }

    val sortedCards = hands.map(hand => sortCardsInHand(hand))

    for (i <- 0 to hands(0).size) {
      val currentIndexCards = sortedCards.map(cards => cards._1(i))
      val sortedIndexCards =
        currentIndexCards.sortWith(_.rank.strength > _.rank.strength)
    }
    0
  }
}
