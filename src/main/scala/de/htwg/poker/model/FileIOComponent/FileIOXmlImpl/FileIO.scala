package de.htwg.poker.model.FileIOComponent.FileIOXmlImpl

import de.htwg.poker.model.FileIOComponent.FileIOInterface
import de.htwg.poker.model.GameStateComponent.GameStateInterface
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit

import scala.xml.{ Node,NodeSeq, PrettyPrinter }

class FileIO extends FileIOInterface {

    def load: GameStateInterface = {
        val file = scala.xml.XML.loadFile("gameState.xml")
        parseGameState(file)
    }

    private def parseGameState(xml: Node): GameStateInterface = {
        val players = (xml \ "players" \ "player").map(parsePlayer).toList
        val deck = (xml \ "deck" \ "card").map(parseCard).toList
        val playerAtTurn = (xml \ "playerAtTurn").text.toInt
        val currentHighestBetSize = (xml \ "currentHighestBetSize").text.toInt
        val board = (xml \ "board" \ "card").map(parseCard).toList
        val pot = (xml \ "pot").text.toInt
        val smallBlind = (xml \ "smallBlind").text.toInt
        val bigBlind = (xml \ "bigBlind").text.toInt
        val smallBlindPointer = (xml \ "smallBlindPointer").text.toInt

        new GameState(Nil, Some(players), Some(deck), playerAtTurn, currentHighestBetSize, board, pot, smallBlind, bigBlind, smallBlindPointer)
    }

    private def parsePlayer(node: Node): Player = {
        val card1Option = (node \ "card1").headOption.map(parseCard)
        val card2Option = (node \ "card2").headOption.map(parseCard)

        val playername = (node \ "playername").text
        val balance = (node \ "balance").text.toInt
        val currentAmountBetted = (node \ "currentAmountBetted").text.toInt

        val card1 = card1Option.getOrElse(throw new RuntimeException("Missing card1"))
        val card2 = card2Option.getOrElse(throw new RuntimeException("Missing card2"))

        new Player(card1, card2, playername, balance, currentAmountBetted)
    }


    private def parseCard(node: Node): Card = {
        val rank = (node \ "rank").text
        val suit = (node \ "suit").text

        new Card(Suit.valueOf(reverseSuit(suit)), Rank.valueOf(reverseRank(rank)))
    }

    def reverseSuit(suit: String): String = {
    suit match {
      case "♣" => "Clubs"
      case "♠" => "Spades"
      case "♢" => "Diamonds"
      case "♡" => "Hearts"
    }
  }

    def reverseRank(rank: String): String = {
        rank match {
        case "2"  => "Two"
        case "3"  => "Three"
        case "4"  => "Four"
        case "5"  => "Five"
        case "6"  => "Six"
        case "7"  => "Seven"
        case "8"  => "Eight"
        case "9"  => "Nine"
        case "10" => "Ten"
        case "J"  => "Jack"
        case "Q"  => "Queen"
        case "K"  => "King"
        case "A"  => "Ace"
        }
    }

    def save(gameState: GameStateInterface): Unit = {
        import java.io._
        val pw = new PrintWriter(new File("gameState.xml"))
        val prettyPrinter = new PrettyPrinter(120, 4)
        val xml = prettyPrinter.format(gameStateToXml(gameState))
        pw.write(xml)
        pw.close
    }

    def gameStateToXml(gameState: GameStateInterface) = {
        <gameState>
        <players>
        {
        for {
            player <- gameState.getPlayers
          } yield {
            <player>
                <card1>
                <rank>{player.card1.rank.toString}</rank>
                <suit>{player.card1.suit.toString1}</suit>
                </card1>
                <card2>
                <rank>{player.card2.rank.toString}</rank>
                <suit>{player.card2.suit.toString1}</suit>
                </card2>
                <playername>{player.playername}</playername>
                <balance>{player.balance}</balance>
                <currentAmountBetted>{player.currentAmountBetted}</currentAmountBetted>
            </player>
          }
        }
        </players>
        <deck>
        {
        for {
            card <- gameState.getDeck
        } yield {
            <card>
            <rank>{card.rank.toString}</rank>
            <suit>{card.suit.toString1}</suit>
            </card>
        }
    }
    </deck>
    <playerAtTurn>{gameState.getPlayerAtTurn}</playerAtTurn>
    <currentHighestBetSize>{gameState.getHighestBetSize}</currentHighestBetSize>
    <board>
    {
    for {
            card <- gameState.getBoard
        } yield {
            <card>
            <rank>{card.rank.toString}</rank>
            <suit>{card.suit.toString1}</suit>
            </card>
        }
    }
    </board>
    <pot>{gameState.getPot}</pot>
    <smallBlind>{gameState.getSmallBlind}</smallBlind>
    <bigBlind>{gameState.getBigBlind}</bigBlind>
    <smallBlindPointer>{gameState.getSmallBlindPointer}</smallBlindPointer>
        </gameState>
    }
}