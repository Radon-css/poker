package de.htwg.poker.model

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0
) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getHighestBetSize: Int = currentHighestBetSize

  override def toString(): String = {
    val stringBuilder = new StringBuilder
    for (player <- getPlayers) {
      stringBuilder.append(player.toString())
    }
    stringBuilder.toString
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
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, amount)
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, getHighestBetSize)
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
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, getHighestBetSize)
  }

  def check(): GameState = {
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(getPlayers), Some(getDeck), nextPlayer, getHighestBetSize)
  }

  // Hilfsfunktionen
  def getNextPlayer(currentPlayer: Int): Int = {
    if (getPlayers.length - 1 == currentPlayer) {
      return 0
    }
    return currentPlayer + 1
  }
}

object GameStage {
  var gamestage = "preflop"
  def continue(): Unit = {
    if (gamestage == "preflop") {
      gamestage = "flop"
    } else if (gamestage == "flop") {
      gamestage = "turn"
    } else if (gamestage == "turn") {
      gamestage = "river"
    }
  }
}
