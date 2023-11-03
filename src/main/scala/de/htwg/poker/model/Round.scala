package de.htwg.poker.model

class Round {
  def handout(playerList: List[Player]): List[Player] = {

    print(
      playerName + " " + player.card1.toString + " " + player.card2.toString + "     "
    )
  }

  def flop(numPlayers: Int): Unit = {
    println("")
    println("")
    println(
      shuffledDeck(numPlayers * 2 - 1).toString + " " + shuffledDeck(
        numPlayers * 2
      ).toString + " " + shuffledDeck(numPlayers * 2 + 1).toString
    )
  }

  def createPlayerList(playerNameList: List[String]): List[Player] = {
    val playerList =
      playerNameList.map(playerName => new Player(None, None, playerName))
    playerList
  }
  def createPlayerNameList(playerNames: String) = {
    val playerNamesList = playerNames.split(" ").toList
    playerNamesList
  }
}
