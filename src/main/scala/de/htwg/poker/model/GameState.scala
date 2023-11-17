package de.htwg.poker.model

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    betSize: Int = 0
) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getBetSize: Int = betSize

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
      getPlayers(playerAtTurn).coins - amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, amount)
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
      GameState(Some(newPlayerList), Some(getDeck), getPlayerAtTurn, getBetSize)
  }

  def call(): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).coins - getBetSize
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val nextPlayer = getNextPlayer(playerAtTurn)
    GameState(Some(newPlayerList), Some(getDeck), nextPlayer, getBetSize)
  }
  // Hilfsfunktionen
  def getNextPlayer(currentPlayer : Int): Int = {
    if(getPlayers.length - 1 == currentPlayer) {
      return 0
    }
    return currentPlayer + 1
  }
}
