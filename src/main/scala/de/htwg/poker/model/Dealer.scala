package de.htwg.poker.model

object Dealer {

  def createGame(
      playerNameList: List[String],
      smallBlind: Int,
      bigBlind: Int
  ): GameState = {

    val shuffledDeck = shuffleDeck

    val playerList = playerNameList.zipWithIndex.map {
      case (playerName, index) =>
        new Player(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          playerName
        )
    }
    val newShuffledDeck = shuffledDeck.drop(playerList.size * 2)

    val smallBlindPlayer = new Player(
      playerList(0).card1,
      playerList(0).card2,
      playerList(0).playername,
      playerList(0).balance - smallBlind,
      playerList(0).currentAmountBetted + smallBlind
    )

    val bigBlindPlayer = new Player(
      playerList(1).card1,
      playerList(1).card2,
      playerList(1).playername,
      playerList(1).balance - bigBlind,
      playerList(1).currentAmountBetted + bigBlind
    )

    val playerListWithBlinds0 = playerList.updated(0, smallBlindPlayer)
    val playerListWithBlinds = playerListWithBlinds0.updated(1, bigBlindPlayer)

    val gameState = GameState(
      playerList,
      Some(playerListWithBlinds),
      Some(newShuffledDeck),
      if (playerList.size < 3) 0 else 2,
      bigBlind,
      Nil,
      smallBlind + bigBlind,
      smallBlind,
      bigBlind
    )
    gameState
  }
}
