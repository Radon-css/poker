package de.htwg.poker.model

case class GameState(players: Option[List[Player]], deck: Option[List[Card]]) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])

  override def toString(): String = {
    val stringBuilder = new StringBuilder
    for (player <- getPlayers) {
      stringBuilder.append(player.toString())
    }
    stringBuilder.toString
  }
}
