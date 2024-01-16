package de.htwg.poker.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model.GameState

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" when {
    "created" should {
      "throw an exception if playerNameList has less than 2 players" in {
        val playerNameList = List("Player1")
        val smallBlind = "10"
        val bigBlind = "20"
        val controller = new Controller(
          new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
        )
        controller.createGame(playerNameList, smallBlind, bigBlind)

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if smallBlind is greater than 100" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "101"
        val bigBlind = "20"
        val controller = new Controller(
          new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
        )
        controller.createGame(playerNameList, smallBlind, bigBlind)

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if bigBlind is greater than 200" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "10"
        val bigBlind = "201"
        val controller = new Controller(
          new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
        )
        controller.createGame(playerNameList, smallBlind, bigBlind)

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "throw an exception if bigBlind is less than or equal to smallBlind" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "20"
        val bigBlind = "20"
        val controller = new Controller(
          new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
        )
        controller.createGame(playerNameList, smallBlind, bigBlind)

        an[Exception] should be thrownBy {
          controller.createGame(playerNameList, smallBlind, bigBlind)
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val playerNameList = List("Player1", "Player2")
        val smallBlind = "10"
        val bigBlind = "20"
        val controller = new Controller(
          new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
        )
        controller.createGame(playerNameList, smallBlind, bigBlind)

        controller.createGame(
          playerNameList,
          smallBlind,
          bigBlind
        ) shouldBe true
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.undo
        }
      }

      "undo the last step and notify observers" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // perform some actions to modify the gameState

        controller.undo
        // assert gameState and notify observers
      }

      "redo the last undone step and notify observers" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // perform some actions and undo them

        controller.redo
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.bet(100)
        }
      }

      "throw an exception if player has insufficient balance" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with a player having insufficient balance

        an[Exception] should be thrownBy {
          controller.bet(100)
        }
      }

      "throw an exception if bet size is too low" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with a bet size that is too low

        an[Exception] should be thrownBy {
          controller.bet(50)
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with valid conditions

        controller.bet(100) shouldBe true
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.allin
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with valid conditions

        controller.allin shouldBe true
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.fold
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with valid conditions

        controller.fold shouldBe true
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.call
        }
      }

      "throw an exception if no bet has been made" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with no bet made

        an[Exception] should be thrownBy {
          controller.call
        }
      }

      "throw an exception if player has already called" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with player already called

        an[Exception] should be thrownBy {
          controller.call
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with valid conditions

        controller.call shouldBe true
        // assert gameState and notify observers
      }

      "throw an exception if no game has been started" in {
        val gameState = ???
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.check
        }
      }

      "throw an exception if player has not betted the highest amount" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with player not betted the highest amount

        an[Exception] should be thrownBy {
          controller.check
        }
      }

      "update the gameState and notify observers if all conditions are met" in {
        val gameState = ???
        val controller = new Controller(gameState)
        // set up the gameState with valid conditions

        controller.check shouldBe true
        // assert gameState and notify observers
      }
    }
  }
}
