package de.htwg.poker.model
import scala.math

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[Card] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20
) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getHighestBetSize: Int = currentHighestBetSize
  def getBoard: List[Card] = board
  def getSmallBlind: Int = smallBlind
  def getBigBlind: Int = bigBlind
  def getPot: Int = pot

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
  def bet(amount: Int): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).balance - amount,
      getPlayers(playerAtTurn).currentAmountBetted + amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      amount,
      getBoard,
      getPot + amount
    )
  }

  def allIn(): GameState = {
    val updatedPlayer = new Player(
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
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      getPlayers(playerAtTurn).balance,
      getBoard,
      getPot + getPlayers(playerAtTurn).balance
    )
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    GameState(
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayerWhenFold,
      getHighestBetSize,
      getBoard,
      getPot
    )
  }

  def call(): GameState = {
    val updatedPlayer = new Player(
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
      Some(newPlayerList),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard,
      getPot + getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted
    )
  }

  def check(): GameState = {
    val nextPlayer = getNextPlayer
    GameState(
      Some(getPlayers),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard,
      getPot
    )
  }

  object updateBoard {
    var strategy: GameState =
      if (getBoard.size == 0) flop
      else if (getBoard.size == 3) turn
      else if (getBoard.size == 4) river
      else restartGame

    def restartGame: GameState = {
      GameState(
        Some(getPlayers),
        Some(getDeck),
        0,
        0,
        getBoard,
        getPot
      )
    }
    def flop: GameState = {
      val newBoard = getDeck.take(3)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        Some(newPlayerList),
        Some(getDeck.drop(3)),
        0,
        0,
        getBoard ::: newBoard,
        getPot
      )
    }
    def turn: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot
      )
    }
    def river: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot
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
    def printBalances(playerList: List[Player]): String = {
      val sb = new StringBuilder
      for (player <- playerList) {
        val spaces = " " * (14 - player.balanceToString().length)
        sb.append(s"${player.balanceToString()}$spaces")
      }
      sb.append("\n")
      sb.toString
    }

    def printPlayerNames(indexedPlayerList: List[(Player, Int)]): String = {
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
    def printPlayerCards(playerList: List[Player]): String = {
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

    def printPlayerBets(playerList: List[Player]): String = {
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
