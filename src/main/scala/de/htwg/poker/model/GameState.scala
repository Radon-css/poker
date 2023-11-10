package de.htwg.poker.model

case class GameState(
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0
) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn

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
    val newPlayerList = getPlayers.updated(playerAtTurn, updatedPlayer)
    val nextPlayer = (playerAtTurn + 1)
    val gameState = GameState(Some(newPlayerList), Some(getDeck), nextPlayer)
    gameState
  }
}
