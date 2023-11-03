package de.htwg.poker
package controller
import model.shuffledDeck
import model.Player

class Dealer {

  def handout(playerNames: String): List[Player] = {
    val playerNamesList = playerNames.split(" ")
    val players = playerNamesList.foldLeft(List.empty[Player]) {
      (list, playerName) =>
        val deckIndex = playerNamesList.indexOf(playerName) * 2
        val player = new Player(
          shuffledDeck(deckIndex),
          shuffledDeck(deckIndex + 1),
          playerName
        )
        print(
          playerName + " " + player.card1.toString + " " + player.card2.toString + "     "
        )
        list :+ player

    }
    players
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
}
