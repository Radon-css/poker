package de.htwg.poker.model

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._

class DealerSpec extends AnyWordSpec with Matchers {
  "Dealer" should {
    "create a game with the correct number of players and cards" in {
      val playerNameList = List("Julian", "Henrik", "Urs")
      val gameState = Dealer.createGame(playerNameList)
      gameState.getPlayers.length shouldEqual 3
      gameState.getDeck.length shouldEqual 49
    }
  }
}
