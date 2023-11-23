package de.htwg.poker.model

object Dealer {

  def createGame(playerNameList: List[String]): GameState = {
    val playerList =
      playerNameList.map(playerName =>
        new Player(
          shuffledDeck(playerNameList.indexOf(playerName) * 2),
          shuffledDeck(playerNameList.indexOf(playerName) * 2 + 1),
          playerName
        )
      )
    val newShuffledDeck = shuffledDeck.drop(playerList.size * 2)
    val gameState = GameState(Some(playerList), Some(newShuffledDeck))
    gameState
  }
}

