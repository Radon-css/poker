package de.htwg.poker.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model.{Card, Dealer, GameState, Player, Rank, Suit}

class ControllerSpec extends AnyWordSpec with Matchers {
  "A Controller" when {
    val card1 = Card(Suit.Hearts, Rank.Ace)
    val card2 = Card(Suit.Diamonds, Rank.King)
    val playername = "John Doe"
    val balance = 1000
    val currentAmountBetted = 0
    val player = Player(card1, card2, playername, balance, currentAmountBetted)

    val controller = new Controller(null)

    "createGame" should {
      "throw an exception if playerNameList is empty" in {
        val playerNameList = List.empty[String]
        val smallBlind = "10"
        val bigBlind = "20"

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if smallBlind or bigBlind is not an integer" in {
        val playerNameList = List("John Doe", "Jane Smith")
        val smallBlind = "10"
        val bigBlind = "abc"

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if smallBlind is greater than 100" in {
        val playerNameList = List("John Doe", "Jane Smith")
        val smallBlind = "101"
        val bigBlind = "20"

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if bigBlind is greater than 200" in {
        val playerNameList = List("John Doe", "Jane Smith")
        val smallBlind = "10"
        val bigBlind = "201"

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if smallBlind is greater than or equal to bigBlind" in {
        val playerNameList = List("John Doe", "Jane Smith")
        val smallBlind = "20"
        val bigBlind = "20"

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "create a new game state with the given parameters" in {
        val playerNameList = List("John Doe", "Jane Smith")
        val smallBlind = "10"
        val bigBlind = "20"

        controller.createGame(playerNameList, smallBlind, bigBlind) should be(
          true
        )
        controller.gameState.getPlayers should be(
          playerNameList.map(_ => player)
        )
        controller.gameState.getSmallBlind should be(10)
        controller.gameState.getBigBlind should be(20)
      }
    }

    // Add more test cases for other methods in the Controller class
  }
}
