package de.htwg.poker.controllers

import de.htwg.poker.model._
import de.htwg.poker.util.UpdateBoard
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.immutable.ListMap
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class ControllerSpec extends AnyWordSpec with Matchers {

  val emptyGameState = GameState(
    playersAndBalances = Nil,
    players = None,
    playersAndAuthIDs = None,
    deck = None
  )

  "A Controller" should {

    val controller = new Controller(emptyGameState)

    "create a game successfully with valid parameters" in {
      val playerNames = List("Alice", "Bob")
      val authIDs = ListMap("Alice" -> "auth1", "Bob" -> "auth2")
      val result = controller.createGame(playerNames, Some(authIDs), "10", "20")
      result shouldBe true
      controller.gameState.players.get.size shouldBe 2
    }

    "reject game creation with duplicate player names" in {
      val playerNames = List("Alice", "Alice")
      intercept[Exception] {
        controller.createGame(playerNames, None, "10", "20")
      }.getMessage shouldBe "All player names must be unique"
    }

    "reject game creation with invalid blind sizes" in {
      val playerNames = List("Alice", "Bob")
      intercept[Exception] {
        controller.createGame(playerNames, None, "20", "10")
      }.getMessage shouldBe "Small blind must be smaller than big blind"
    }

    "handle betting correctly" which {
      val testState = emptyGameState.copy(
        players = Some(
          List(
            Player(Card(Suit.Hearts, Rank.Ace), Card(Suit.Diamonds, Rank.King), "Alice", 1000, 0),
            Player(Card(Suit.Clubs, Rank.Queen), Card(Suit.Spades, Rank.Jack), "Bob", 1000, 0)
          )
        ),
        currentHighestBetSize = 0,
        bigBlind = 20
      )
      val testController = new Controller(testState)

      "accept valid bets" in {
        val result = testController.bet(50)
        result shouldBe true
        testController.gameState.currentHighestBetSize shouldBe 50
      }

      "reject bets smaller than big blind" in {
        intercept[Exception] {
          testController.bet(10)
        }.getMessage shouldBe "Bet size is too low"
      }

      "handle all-in bets" in {
        val allInAmount = testController.gameState.players.get.head.balance
        val result = testController.bet(allInAmount)
        result shouldBe true
        testController.gameState.players.get.head.balance shouldBe 950
      }
    }

    "handle checking correctly" which {
      val testState = emptyGameState.copy(
        players = Some(
          List(
            Player(Card(Suit.Hearts, Rank.Ace), Card(Suit.Diamonds, Rank.King), "Alice", 1000, 20),
            Player(Card(Suit.Clubs, Rank.Queen), Card(Suit.Spades, Rank.Jack), "Bob", 1000, 20)
          )
        ),
        currentHighestBetSize = 20
      )
      val testController = new Controller(testState)

      "accept valid checks" in {
        val result = testController.check
        result shouldBe true
        testController.gameState.players.get.head.checkedThisRound shouldBe true
      }

      "reject invalid checks" in {
        val invalidState = testState.copy(
          players = Some(testState.players.get.map(_.copy(currentAmountBetted = 0)))
        )
        val invalidController = new Controller(invalidState)
        intercept[Exception] {
          invalidController.check
        }.getMessage shouldBe "Cannot check"
      }
    }

    "determine when handout is required" which {
      "for preflop when all called" in {
        val testState = emptyGameState.copy(
          players = Some(
            List(
              Player(Card(Suit.Hearts, Rank.Ace), Card(Suit.Diamonds, Rank.King), "Alice", 1000, 10),
              Player(Card(Suit.Clubs, Rank.Queen), Card(Suit.Spades, Rank.Jack), "Bob", 1000, 20)
            )
          ),
          board = Nil,
          smallBlind = 10,
          bigBlind = 20,
          currentHighestBetSize = 20,
          playerAtTurn = 0,
          smallBlindPointer = 0
        )
        val testController = new Controller(testState)
        testController.handout_required shouldBe false
      }

      "for postflop when all checked" in {
        val testState = emptyGameState.copy(
          players = Some(
            List(
              Player(Card(Suit.Hearts, Rank.Ace), Card(Suit.Diamonds, Rank.King), "Alice", 1000, 0, folded = false, checkedThisRound = true),
              Player(Card(Suit.Clubs, Rank.Queen), Card(Suit.Spades, Rank.Jack), "Bob", 1000, 0, folded = false, checkedThisRound = true)
            )
          ),
          board = List(Card(Suit.Hearts, Rank.Two)),
          currentHighestBetSize = 0
        )
        val testController = new Controller(testState)
        testController.handout_required shouldBe true
      }
    }

    "detect when a player wins before showdown" in {
      val testState = emptyGameState.copy(
        players = Some(
          List(
            Player(Card(Suit.Hearts, Rank.Ace), Card(Suit.Diamonds, Rank.King), "Alice", 1000, 0),
            Player(Card(Suit.Clubs, Rank.Queen), Card(Suit.Spades, Rank.Jack), "Bob", 1000, 0, folded = true)
          )
        )
      )
      val testController = new Controller(testState)
      testController.playerWonBeforeShowdown shouldBe true
    }

  }
}
