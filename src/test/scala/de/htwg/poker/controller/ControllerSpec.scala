package de.htwg.poker.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.poker.model.GameState

class ControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A Controller" when {

    "createGame" should {
      "return true and update the game state when valid inputs are provided" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "10"
        val bigBlind = "20"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)

        val result = controller.createGame(playerNameList, smallBlind, bigBlind)
        result shouldBe true

      }

      "throw an exception when less than two players are provided" in {
        val playerNameList = List("Player1")
        val smallBlind = "10"
        val bigBlind = "20"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }
      "throw an exception when the small blind is greater than 100" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "101"
        val bigBlind = "20"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }
      "throw an exception when the big blind is greater than 200" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "10"
        val bigBlind = "201"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }
      "throw an exception when the big blind is smaller than the small blind" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "20"
        val bigBlind = "10"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }
      "throw an exception when the last two inputs are not integers" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "abc"
        val bigBlind = "def"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }
    }
    "bet" should {
      "return true and update the game state when valid inputs are provided" in {

        import org.mockito.Mockito.when

        val playerNameList = List("Player1", "Player2")
        val smallBlind = "10"
        val bigBlind = "20"
        val gameState = mock[GameState]
        val controller = new Controller(gameState)

        when(controller.gameState) thenReturn gameState

        controller.createGame(playerNameList, smallBlind, bigBlind)

        val result = controller.bet(100)
        result shouldBe true
      }
      "throw an exception when the game has not been started" in {
        val amount = 100
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        an[Exception] should be thrownBy {
          controller.bet(amount)
        }
      }
      "throw an exception when the player does not have enough balance" in {
        val amount = 100
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        an[Exception] should be thrownBy {
          controller.bet(amount)
        }
      }
      "throw an exception when the bet size is too low" in {
        val amount = 100
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        an[Exception] should be thrownBy {
          controller.bet(amount)
        }
      }
    }
    "allin" should {
      "return true and update the game state when valid inputs are provided" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        val result = controller.allin
        result shouldBe true
      }
      "throw an exception when the game has not been started" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.allin
        }
      }
    }
    "fold" should {
      "return true and update the game state when valid inputs are provided" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        val result = controller.fold
        result shouldBe true
      }
      "throw an exception when the game has not been started" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.fold
        }
      }
    }
    "call" should {
      "return true and update the game state when valid inputs are provided" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")

        val result = controller.call
        result shouldBe true
      }
      "throw an exception when the game has not been started" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.call
        }
      }
      "throw an exception when the player does not have enough balance" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.call
        }
      }
      "throw an exception when the player has already called" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.call
        }
      }
    }
    "check" should {
      "return true and update the game state when valid inputs are provided" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        controller.createGame(List("Player1", "Player2"), "10", "20")
        controller.call

        val result = controller.check
        result shouldBe true
      }
      "throw an exception when the game has not been started" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.check
        }
      }
      "throw an exception when the player has already checked" in {
        val gameState = mock[GameState]
        val controller = new Controller(gameState)
        an[Exception] should be thrownBy {
          controller.check
        }
      }
    }
  }
}
