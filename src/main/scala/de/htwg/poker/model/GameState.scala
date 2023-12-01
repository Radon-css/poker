package de.htwg.poker.model

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
    // print balance
    for (player <- getPlayers) {
      stringBuilder.append("(" + player.balance + "$)     ")
    }
    stringBuilder.append("\n")
    // print playerNames
    for (playerWithIndex <- indexedPlayerList) {
      if (playerWithIndex._2 == getPlayerAtTurn) {
        val boldPlayer = playerWithIndex._1.playername
        stringBuilder.append(
          s"$ANSI_COLORED$boldPlayer$ANSI_RESET" + "     "
        )
      } else {
        stringBuilder.append(playerWithIndex._1.playername + "     ")
      }
    }
    // print playerCards
      stringBuilder.append("\n")
      for(player <- getPlayers) {
        stringBuilder.append(player.card1.toString + player.card2.toString + "     ")
      }
      stringBuilder.append("\n")
    // print playerBet
      for(player <- getPlayers) {
        stringBuilder.append(player.currentAmountBetted + "    ")
      }
    // print boardCards
    stringBuilder.append("\n\n")
    for (card <- getBoard) {
      stringBuilder.append(card.toString() + " ")
    }
    stringBuilder.append("\n")
    stringBuilder.toString()
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
}
