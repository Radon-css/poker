package de.htwg.poker.model
import scala.math

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[Card] = Nil,
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
        getPlayers.take(scala.math.ceil(getPlayers.size / 2).toInt)
      val BottomRowPlayerList =
        getPlayers.drop(scala.math.ceil(getPlayers.size / 2).toInt)

      val sb = new StringBuilder
      sb.append(Print.printBalances(TopRowPlayerList))
      sb.append(Print.printPlayerNames(TopRowPlayerList))
      sb.append(Print.printPlayerCards(TopRowPlayerList))
      sb.append(Print.printPlayerBets(TopRowPlayerList))
      sb.append(Print.printBoard(getBoard))
      sb.append(Print.printPlayerBets(BottomRowPlayerList))
      sb.append(Print.printPlayerCards(BottomRowPlayerList))
      sb.append(Print.printPlayerNames(BottomRowPlayerList))
      sb.append(Print.printBalances(BottomRowPlayerList))
      sb.toString
      // case weniger als 3 Spieler
    } else {
      val TopRowPlayerList = getPlayers

      val sb = new StringBuilder
      sb.append(Print.printBalances(TopRowPlayerList))
      sb.append(Print.printPlayerNames(TopRowPlayerList))
      sb.append(Print.printPlayerCards(TopRowPlayerList))
      sb.append(Print.printPlayerBets(TopRowPlayerList))
      sb.append(Print.printBoard(getBoard))
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
      getBoard
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
      getBoard
    )
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    val nextPlayer = getNextPlayer
    GameState(
      Some(newPlayerList),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard
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
      getBoard
    )
  }

  def check(): GameState = {
    val nextPlayer = getNextPlayer
    GameState(
      Some(getPlayers),
      Some(getDeck),
      nextPlayer,
      getHighestBetSize,
      getBoard
    )
  }

  object updateBoard {
    var strategy: GameState =
      if (getBoard.size == 0) flop
      else if (getBoard.size == 3) turn
      else river

    def flop: GameState = {
      val newBoard = getDeck.take(3)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        Some(newPlayerList),
        Some(getDeck.drop(3)),
        0,
        0,
        getBoard ::: newBoard
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
        getBoard ::: newBoard
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
        getBoard ::: newBoard
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

    def printPlayerNames(playerList: List[Player]): String = {
      val sb = new StringBuilder
      val indexedPlayerList = playerList.zipWithIndex
      val ANSI_COLORED = "\u001b[34m"
      val ANSI_RESET = "\u001b[0m"

      for (playerWithIndex <- indexedPlayerList) {
        if (playerWithIndex._2 == getPlayerAtTurn) {
          val spaces =
            " " * (14 - playerWithIndex._1.playername.length)
          val boldPlayer = playerWithIndex._1.playername
          sb.append(
            s"$ANSI_COLORED$boldPlayer$ANSI_RESET$spaces"
          )
        } else {
          val spaces =
            " " * (14 - playerWithIndex._1.playername.length)
          sb.append(s"${playerWithIndex._1.playername}$spaces")
        }
      }
      sb.append("\n")
      sb.toString
    }
    def printPlayerCards(playerList: List[Player]): String = {
      val sb = new StringBuilder
      for (player <- playerList) {
        val spaces =
          " " * (14 - player.card1.toString.length - player.card2.toString.length)
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
    def printBoard(cardList: List[Card]): String = {
      val sb = new StringBuilder
      sb.append("\n")
      for (card <- cardList) {
        sb.append(card.toString() + " ")
      }
      sb.append("\n\n")
      sb.toString
    }
  }
}
