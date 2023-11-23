package de.htwg.poker.model

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[Card] = Nil,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
  ) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getHighestBetSize: Int = currentHighestBetSize
  def getBoard: List[Card] = board
  def getSmallBlind: Int = smallBlind
  def getBigBlind: Int = bigBlind

  override def toString(): String = {
    val stringBuilder = new StringBuilder
    for (player <- getPlayers) {
      stringBuilder.append(player.toString())
    }
    stringBuilder.append("\n")
    for (card <- getBoard) {
      stringBuilder.append(card.toString() + " ")
    }
    stringBuilder.toString()
  }

  def bet(amount: Int): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).coins - amount,
      getPlayers(playerAtTurn).currentAmountBetted + amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, amount, getBoard)
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, getHighestBetSize, getBoard)
  }

  def call(): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).coins - (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted),
      getPlayers(
        playerAtTurn
      ).currentAmountBetted + (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted)
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, getHighestBetSize, getBoard)
  }

  def check(): GameState = {
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(getPlayers), Some(getDeck), nextPlayer, getHighestBetSize, getBoard)
  }

  object updateBoard {
  var strategy: GameState = if (boardState.state == "preflop") flop else if (boardState.state == "flop") turn else river

    def flop: GameState = {
      val newBoard = getDeck.take(3)
      print(newBoard)
      print(getBoard)
      GameState(Some(getPlayers), Some(getDeck.drop(3)), 0, getHighestBetSize, getBoard ::: newBoard)
    }
    def turn: GameState = {
      val newBoard = getDeck.take(1)
      GameState(Some(getPlayers), Some(getDeck.drop(1)), 0, getHighestBetSize, getBoard ::: newBoard)
    }
    def river: GameState = {
      val newBoard = getDeck.take(1)
      GameState(Some(getPlayers), Some(getDeck.drop(1)), 0, getHighestBetSize, getBoard ::: newBoard)
    }
}


  // Hilfsfunktionen
  def getNextPlayer(currentPlayer: Int): Int = {
    if (getPlayers.length - 1 == currentPlayer) {
      return 0
    }
    return currentPlayer + 1
  }
}

object boardState {
  var state = "preflop"
  def continue(): Unit = {
    if (state == "preflop") {
      state = "flop"
    } else if (state == "flop") {
      state = "turn"
    } else if (state == "turn") {
      state = "river"
    }
  }
}

