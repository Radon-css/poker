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

    val smallBlindPlayer = new Player(
      playerList(0).card1,
      playerList(0).card2,
      playerList(0).playername,
      playerList(0).coins - 10,
      playerList(0).currentAmountBetted + 10
    )

    val bigBlindPlayer = new Player(
      playerList(1).card1,
      playerList(1).card2,
      playerList(1).playername,
      playerList(1).coins - 20,
      playerList(1).currentAmountBetted + 20
    )

    val playerListWithBlinds0 = playerList.updated(0, smallBlindPlayer)
    val playerListWithBlinds = playerListWithBlinds0.updated(1, bigBlindPlayer)

    val gameState = GameState(
      Some(playerListWithBlinds),
      Some(newShuffledDeck),
      2,
      20,
      Nil,
      10,
      20
    )
    gameState
  }
}
