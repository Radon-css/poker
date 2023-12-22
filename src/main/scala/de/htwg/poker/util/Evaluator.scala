package de.htwg.poker.util

import de.htwg.poker.model.*

class Evaluator(gameState: GameState) {

    enum Type:
        case High, Pair, DoublePair, Triples, Straight, Flush, FullHouse, Quads, StraightFlush, RoyalFlush
        def strength: Int = this match {
            case High => 1
            case Pair => 2
            case DoublePair => 3
            case Triples => 4
            case Straight => 5
            case Flush => 6
            case FullHouse => 7
            case Quads => 8
            case StraightFlush => 9
            case RoyalFlush => 10
    }    
   val boardCards: List[Card] = gameState.getBoard
   val playerCards: Map[Player, (Card,Card)] = gameState.getPlayers.map(player => player -> (player.card1,player.card2)).toMap

    def evaluate(): Player = {
        val bestPlayerHands: Map[Player, List[Card]] = playerCards.map {case (player,(card1,card2)) => player -> evalHand((card1,card2))}
        evalWinner(bestPlayerHands)
    }

    def EvalHand(cards: (Card, Card)): List[Card] = {
        val (card1, card2) = cards

        val rankHistogramm: List[(Rank,Int)] = boardCards.map(_.rank).groupBy(identity).mapValues(_.size).toList.sortBy(-_._2)

        if (rankHistogramm(0)._2 == 4 && rankHistogramm(1)._2 == 1) 
            

    }

    def evalWinner(playerBestHands: Map[Player, List[Card]]): Player = {

    }

    }
