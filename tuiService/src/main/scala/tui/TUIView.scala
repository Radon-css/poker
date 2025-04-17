package de.htwg.poker.tui

import de.htwg.poker.model.GameState
import de.htwg.poker.model.Player
import de.htwg.poker.model.Card

object TUIView {
  def update(gameState: GameState): String = {
    val stringBuilder = new StringBuilder
    val indexedPlayerList =
      gameState.players.getOrElse(List.empty[Player]).zipWithIndex
    val Print = new Print(gameState)

    // clear console
    // print("\u001b[2J\u001b[H")
    // case mehr als 3 Spieler
    if (gameState.players.getOrElse(List.empty[Player]).size > 3) {
      val TopRowPlayerList =
        gameState.players
          .getOrElse(List.empty[Player])
          .take(
            scala.math
              .ceil(
                gameState.players
                  .getOrElse(List.empty[Player])
                  .size
                  .toDouble / 2
              )
              .toInt
          )
      val BottomRowPlayerList =
        gameState.players
          .getOrElse(List.empty[Player])
          .drop(
            scala.math
              .ceil(
                gameState.players
                  .getOrElse(List.empty[Player])
                  .size
                  .toDouble / 2
              )
              .toInt
          )
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
      sb.append(Print.printBoard(gameState.board, TopRowApproxLength))
      sb.append(Print.printPlayerBets(BottomRowPlayerList))
      sb.append(Print.printPlayerCards(BottomRowPlayerList))
      sb.append(Print.printPlayerNames(BottomRowIndexedPlayerList))
      sb.append(Print.printBalances(BottomRowPlayerList))
      sb.toString
      // case weniger als 3 Spieler
    } else {
      val TopRowPlayerList = gameState.players.getOrElse(List.empty[Player])
      val TopRowIndexedPlayerList = TopRowPlayerList.zipWithIndex
      val TopRowApproxLength =
        (TopRowIndexedPlayerList.size - 1) * 14 + 7

      val sb = new StringBuilder
      sb.append(Print.printBalances(TopRowPlayerList))
      sb.append(Print.printPlayerNames(TopRowIndexedPlayerList))
      sb.append(Print.printPlayerCards(TopRowPlayerList))
      sb.append(Print.printPlayerBets(TopRowPlayerList))
      sb.append(Print.printPot(TopRowApproxLength))
      sb.append(Print.printBoard(gameState.board, TopRowApproxLength))
      sb.toString
    }
  }
}
class Print(gameState: GameState) {
  def printBalances(playerList: List[Player]): String = {
    val sb = new StringBuilder
    for (player <- playerList) {
      val spaces = " " * (14 - player.balanceToString.length)
      sb.append(s"${player.balanceToString}$spaces")
    }
    sb.append("\n")
    sb.toString
  }

  def printPlayerNames(indexedPlayerList: List[(Player, Int)]): String = {
    val sb = new StringBuilder
    for (playerWithIndex <- indexedPlayerList) {
      if (playerWithIndex._2 == gameState.playerAtTurn) {
        val boldPlayer = playerWithIndex._1.playername
        if (playerWithIndex == indexedPlayerList.last) {
          sb.append(
            s">$boldPlayer<"
          )
        } else {
          val spaces =
            " " * (14 - playerWithIndex._1.playername.length)
          sb.append(
            s">$boldPlayer<$spaces"
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
        " " * (14 - (player.card1.toString.length) - (player.card2.toString.length))
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
    val potLength = gameState.pot.toString.length + 3
    val padding =
      math.max(0, playerLengthApprox - potLength) / 2
    sb.append("\n")
    sb.append(" " * padding)
    sb.append("(" + gameState.pot + "$)")
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
