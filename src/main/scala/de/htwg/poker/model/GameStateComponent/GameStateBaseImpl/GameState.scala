package de.htwg.poker.model.GameStateComponent.GameStateBaseImpl
import scala.math
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.PlayersComponent.PlayerInterface
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Deck
import de.htwg.poker.model.CardsComponent.CardInterface as Card
import de.htwg.poker.model.GameStateComponent.GameStateInterface

import de.htwg.poker.model.CardsComponent.CardsAdvancedImpl.Card as CardsAdvancedImpl

case class GameState(
    originalPlayers: List[PlayerInterface],
    players: Option[List[PlayerInterface]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[Card] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0
) extends GameStateInterface {

  def getPlayers: List[PlayerInterface] =
    players.getOrElse(List.empty[PlayerInterface])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getHighestBetSize: Int = currentHighestBetSize
  def getBoard: List[Card] = board
  def getSmallBlind: Int = smallBlind
  def getBigBlind: Int = bigBlind
  def getPot: Int = pot
  def getOriginalPlayers: List[PlayerInterface] = originalPlayers
  def getSmallBlindPointer = smallBlindPointer

  override def toString(): String = {
    val ANSI_COLORED = "\u001b[34m"
    val ANSI_RESET = "\u001b[0m"
    val stringBuilder = new StringBuilder
    val indexedPlayerList = getPlayers.zipWithIndex

    // clear console
    print("\u001b[2J\u001b[H")
    // case mehr als 3 Spieler
    if (getPlayers.size > 3) {
      val TopRowPlayerList =
        getPlayers.take(scala.math.ceil(getPlayers.size.toDouble / 2).toInt)
      val BottomRowPlayerList =
        getPlayers.drop(scala.math.ceil(getPlayers.size.toDouble / 2).toInt)
      val TopRowIndexedPlayerList = TopRowPlayerList.zipWithIndex
      val BottomRowIndexedPlayerList = BottomRowPlayerList.zipWithIndex.map {
        case (element, index) => (element, index + TopRowPlayerList.size)
      }
      val TopRowApproxLength =
        (TopRowIndexedPlayerList.size - 1) * 14 + 7

      val sb = new StringBuilder
      sb.append(Print.printBalances(TopRowPlayerList))
      sb.append(Print.printPlayerNames(TopRowIndexedPlayerList))
      sb.append(Print.printPlayerCards(TopRowPlayerList))
      sb.append(Print.printPlayerBets(TopRowPlayerList))
      sb.append(Print.printPot(TopRowApproxLength))
      sb.append(Print.printBoard(getBoard, TopRowApproxLength))
      sb.append(Print.printPlayerBets(BottomRowPlayerList))
      sb.append(Print.printPlayerCards(BottomRowPlayerList))
      sb.append(Print.printPlayerNames(BottomRowIndexedPlayerList))
      sb.append(Print.printBalances(BottomRowPlayerList))
      sb.toString
      // case weniger als 3 Spieler
    } else {
      val TopRowPlayerList = getPlayers
      val TopRowIndexedPlayerList = TopRowPlayerList.zipWithIndex
      val TopRowApproxLength =
        (TopRowIndexedPlayerList.size - 1) * 14 + 7

      val sb = new StringBuilder
      sb.append(Print.printBalances(TopRowPlayerList))
      sb.append(Print.printPlayerNames(TopRowIndexedPlayerList))
      sb.append(Print.printPlayerCards(TopRowPlayerList))
      sb.append(Print.printPlayerBets(TopRowPlayerList))
      sb.append(Print.printPot(TopRowApproxLength))
      sb.append(Print.printBoard(getBoard, TopRowApproxLength))
      sb.toString
    }
  }
  def createGame(
      playerNameList: List[String],
      smallBlind: Int,
      bigBlind: Int
  ): GameState = {

    val shuffledDeck = Deck.shuffleDeck

    print(playerNameList.size)

    val playerList0 = playerNameList.zipWithIndex.map {
      case (playerName, index) =>
        new Player(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          playerName,
          1000,
          0
        )
    }

    val playerList = playerNameList.zipWithIndex.map {
      case (playerName, index) =>
        playerList0(index).createPlayer(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          playerName,
          1000,
          0
        )
    }
    val newShuffledDeck = shuffledDeck.drop(playerList.size * 2)

    val smallBlindPlayer = playerList.head.createPlayer(
      playerList.head.card1,
      playerList.head.card2,
      playerList.head.playername,
      1000 - smallBlind,
      smallBlind
    )

    val bigBlindPlayer = playerList(1).createPlayer(
      playerList(1).card1,
      playerList(1).card2,
      playerList(1).playername,
      1000 - bigBlind,
      bigBlind
    )

    val playerListWithBlinds0 = playerList.updated(0, smallBlindPlayer)
    val playerListWithBlinds =
      playerListWithBlinds0.updated(1, bigBlindPlayer)

    GameState(
      originalPlayers,
      Some(playerListWithBlinds),
      Some(newShuffledDeck),
      if (playerList.size < 3) 0 else 2,
      bigBlind,
      Nil,
      smallBlind + bigBlind,
      smallBlind,
      bigBlind
    )
  }

  def bet(amount: Int): GameState = {

    val currentTurnPlayer = getPlayers(getPlayerAtTurn)

    val updatedPlayer = currentTurnPlayer.createPlayer(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).balance - amount,
      getPlayers(playerAtTurn).currentAmountBetted + amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      amount,
      getBoard,
      getPot + amount,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def allIn(): GameState = {
    val currentTurnPlayer = getPlayers(getPlayerAtTurn)
    val updatedPlayer = currentTurnPlayer.createPlayer(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      0,
      getPlayers(playerAtTurn).currentAmountBetted + getPlayers(
        playerAtTurn
      ).balance
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      getPlayers(playerAtTurn).balance,
      getBoard,
      getPot + getPlayers(playerAtTurn).balance,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayerWhenFold,
      getHighestBetSize,
      getBoard,
      getPot,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def call(): GameState = {
    val currentTurnPlayer = getPlayers(getPlayerAtTurn)
    val updatedPlayer = currentTurnPlayer.createPlayer(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).balance - (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted),
      getPlayers(
        playerAtTurn
      ).currentAmountBetted + (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted)
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val nextPlayer = getNextPlayer
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard,
      getPot + getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def check(): GameState = {
    val nextPlayer = getNextPlayer
    GameState(
      getOriginalPlayers,
      Some(getPlayers),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard,
      getPot,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def updateBoard(): GameState = {
    updateStrategy.strategy
  }

  object updateStrategy {
    var strategy: GameState =
      if (getBoard.size == 0) flop
      else if (getBoard.size == 3) turn
      else if (getBoard.size == 4) river
      else startRound

    def startRound: GameState = {
      val shuffledDeck = Deck.shuffleDeck

      val newPlayerList = getOriginalPlayers.zipWithIndex.map {
        case (playerName, index) =>
          getOriginalPlayers(index).createPlayer(
            shuffledDeck(index * 2),
            shuffledDeck(index * 2 + 1),
            getOriginalPlayers(index).playername,
            getOriginalPlayers(index).balance,
            0 // Assuming 0 is the initial value for currentAmountBetted
          )
      }

      val smallBlindPlayer = newPlayerList(0).createPlayer(
        newPlayerList(0).card1,
        newPlayerList(0).card2,
        newPlayerList(0).playername,
        newPlayerList(0).balance - smallBlind,
        newPlayerList(0).currentAmountBetted + smallBlind
      )

      val bigBlindPlayer = newPlayerList(1).createPlayer(
        newPlayerList(1).card1,
        newPlayerList(1).card2,
        newPlayerList(1).playername,
        newPlayerList(1).balance - bigBlind,
        newPlayerList(1).currentAmountBetted + bigBlind
      )

      val newShuffledDeck = shuffledDeck.drop(newPlayerList.size * 2)

      val playerListWithBlinds0 =
        newPlayerList.updated(0, smallBlindPlayer)
      val playerListWithBlinds =
        playerListWithBlinds0.updated(1, bigBlindPlayer)

      GameState(
        getOriginalPlayers,
        Some(playerListWithBlinds),
        Some(newShuffledDeck),
        if (getOriginalPlayers.size < 3) 0 else 2,
        getBigBlind,
        Nil,
        getSmallBlind + getBigBlind,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }

    def flop: GameState = {
      val newBoard = getDeck.take(3)
      val newPlayerList =
        getPlayers.map(player =>
          player.createPlayer(
            player.card1,
            player.card2,
            player.playername,
            player.balance,
            player.currentAmountBetted
          )
        )
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(3)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
    def turn: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player =>
          player.createPlayer(
            player.card1,
            player.card2,
            player.playername,
            player.balance,
            player.currentAmountBetted
          )
        )
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
    def river: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player =>
          player.createPlayer(
            player.card1,
            player.card2,
            player.playername,
            player.balance,
            player.currentAmountBetted
          )
        )
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
  }

  // Hilfsfunktionen
  def getNextPlayer: Int = {
    if (getPlayers.length - 1 == getPlayerAtTurn) {
      return 0
    }
    return getPlayerAtTurn + 1
  }

  def getNextPlayerWhenFold: Int = {
    if (getPlayers.length - 1 == getPlayerAtTurn) {
      return 0
    }
    return getPlayerAtTurn
  }

  def getPreviousPlayer: Int = {
    if (getPlayerAtTurn == 0) {
      return getPlayers.length - 1
    }
    return getPlayerAtTurn - 1
  }

  object Print {
    def printBalances(playerList: List[PlayerInterface]): String = {
      val sb = new StringBuilder
      for (player <- playerList) {
        val spaces = " " * (14 - player.balanceToString().length)
        sb.append(s"${player.balanceToString()}$spaces")
      }
      sb.append("\n")
      sb.toString
    }

    def printPlayerNames(
        indexedPlayerList: List[(PlayerInterface, Int)]
    ): String = {
      val sb = new StringBuilder
      val ANSI_COLORED = "\u001b[34m"
      val ANSI_RESET = "\u001b[0m"
      for (playerWithIndex <- indexedPlayerList) {
        if (playerWithIndex._2 == getPlayerAtTurn) {
          val boldPlayer = playerWithIndex._1.playername
          if (playerWithIndex == indexedPlayerList.last) {
            sb.append(
              s"$ANSI_COLORED$boldPlayer$ANSI_RESET"
            )
          } else {
            val spaces =
              " " * (14 - playerWithIndex._1.playername.length)
            sb.append(
              s"$ANSI_COLORED$boldPlayer$ANSI_RESET$spaces"
            )
          }
        } else {
          if (playerWithIndex == indexedPlayerList.last) {
            sb.append(s"${playerWithIndex._1.playername}")
          } else {
            val spaces =
              " " * (14 - playerWithIndex._1.playername.length)
            sb.append(s"${playerWithIndex._1.playername}$spaces")
          }
        }
      }
      sb.append("\n")
      sb.toString
    }
    def printPlayerCards(playerList: List[PlayerInterface]): String = {
      val sb = new StringBuilder
      for (player <- playerList) {
        val spaces =
          " " * (14 - (player.card1.toString.length - 9) - (player.card2.toString.length - 9))
        sb.append(
          s"${player.card1.toString}${player.card2.toString}$spaces"
        )
      }
      sb.append("\n")
      sb.toString
    }

    def printPlayerBets(playerList: List[PlayerInterface]): String = {
      val sb = new StringBuilder
      for (player <- playerList) {
        val spaces =
          " " * (14 - player.currentAmountBetted.toString().length - 1)
        sb.append(s"${player.currentAmountBetted.toString}$$$spaces")
      }
      sb.append("\n")
      sb.toString
    }

    def printPot(playerLengthApprox: Int): String = {
      val sb = new StringBuilder
      val potLength = getPot.toString.length + 3
      val padding =
        math.max(0, playerLengthApprox - potLength) / 2
      sb.append("\n")
      sb.append(" " * padding)
      sb.append("(" + getPot + "$)")
      sb.append("\n")
      sb.toString
    }
    def printBoard(
        cardList: List[Card],
        playerLengthApprox: Int
    ): String = {
      val sb = new StringBuilder

      if (cardList.size == 0) {
        sb.append("[*] " * 5)
        val boardLength = 19
        val padding =
          math.floor((playerLengthApprox - boardLength) / 2).toInt
        sb.insert(0, " " * padding)
      } else if (cardList.size == 3) {
        for (card <- cardList) {
          sb.append(card.toString() + " ")
        }
        sb.append("[*] " * 2)
        val boardLength =
          22 + cardList.count(card => card.rank.toString() == "TEN")
        val padding =
          math.floor((playerLengthApprox - boardLength) / 2).toInt
        sb.insert(0, " " * padding)
      } else if (cardList.size == 4) {
        for (card <- cardList) {
          sb.append(card.toString() + " ")
        }
        sb.append("[*] ")
        val boardLength =
          23 + cardList.count(card => card.rank.toString() == "TEN")
        val padding =
          math.floor((playerLengthApprox - boardLength) / 2).toInt
        sb.insert(0, " " * padding)
      } else {
        for (card <- cardList) {
          sb.append(card.toString() + " ")
        }
        val boardLength =
          24 + cardList.count(card => card.rank.toString() == "TEN")
        val padding =
          math.floor((playerLengthApprox - boardLength) / 2).toInt
        sb.insert(0, " " * padding)
      }
      sb.append("\n\n")
      sb.toString
    }
  }
}
