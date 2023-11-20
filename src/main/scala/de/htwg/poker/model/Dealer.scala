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
    val newShuffledDeck = removeCards(shuffledDeck, playerList.size)
    val gameState = GameState(Some(playerList), Some(newShuffledDeck))
    gameState
  }
}

object context {
  var strategy =
    if (GameStage.gamestage == "preflop") flop
    else if (GameStage.gamestage == "flop") turn
    else if (GameStage.gamestage == "turn") river

    def flop = 
}
