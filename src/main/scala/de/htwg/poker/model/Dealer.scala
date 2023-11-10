package de.htwg.poker.model

object Dealer {

  def createGame(playerNameList: List[String]): GameState = {
    val playerList =
      playerNameList.map(playerName => new Player(None, None, playerName))
    val gameState = GameState(playerList)
    gameState
  }
}
