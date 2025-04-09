package de.htwg.poker.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model.GameState
import de.htwg.poker.model.{Card, GameState, Player, Rank, Suit}
import de.htwg.poker.aview.TUI

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
      "throw an exception if the last two inputs are not integers" in {
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
      }
      "check if an handout is required" in {
        val players = List(
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Ten),
            "Frank",
            1000
          ),
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Nine),
            "Tom",
            1000
          )
        )
        val gameState =
          new GameState(
            players,
            Some(players),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        gameState.call
        gameState.check
        controller.handout_required shouldBe true
      }
    }
    "bet" should {
      "update the player's balance and current amount betted" in {
        val amount = 100
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        controller.bet(amount)
        gameState.players.getOrElse(List.empty[Player])(0).balance should be(900)
        gameState.players.getOrElse(List.empty[Player])(0).currentAmountBetted should be(100)
      }
      "throw an exception if the amount is greater than the player's balance" in {
        val amount = 1001
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        controller.bet(amount)

        an[Exception] should be thrownBy {
          controller.bet(amount)
        }
      }
      "throw an exception if the amount is less than the current highest bet size" in {
        val amount = 9
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        controller.bet(amount)

        an[Exception] should be thrownBy {
          controller.bet(amount)
        }
      }
    }
    "call" should {
      "update the player's balance and current amount betted" in {
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        controller.call
        gameState.players.getOrElse(List.empty[Player])(0).balance should be(1000)
        gameState.players.getOrElse(List.empty[Player])(0).currentAmountBetted should be(20)
      }
      "throw an exception if a players call before any bet was made" in {
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.call
        }
      }
      "throw an exception if a player tries to call when he has already betted enough" in {
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                1000
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                1000
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  1000
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  1000
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)
        controller.call

        an[Exception] should be thrownBy {
          controller.call
        }
      }
      "throw an exception if a players balance is less then the current highest bet size" in {
        val gameState =
          new GameState(
            List(
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Ten),
                "Frank",
                19
              ),
              new Player(
                new Card(Suit.Hearts, Rank.Ace),
                new Card(Suit.Hearts, Rank.Nine),
                "Tom",
                20
              )
            ),
            Some(
              List(
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Ten),
                  "Frank",
                  19
                ),
                new Player(
                  new Card(Suit.Hearts, Rank.Ace),
                  new Card(Suit.Hearts, Rank.Nine),
                  "Tom",
                  20
                )
              )
            ),
            None,
            0,
            0,
            Nil,
            0,
            10,
            20,
            0
          )
        val controller = new Controller(gameState)

        an[Exception] should be thrownBy {
          controller.call
        }
      }
    }
  }
}
