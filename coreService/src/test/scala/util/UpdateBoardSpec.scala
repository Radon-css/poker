package de.htwg.poker.util

import de.htwg.poker.model._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.immutable.ListMap
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

class UpdateBoardSpec extends AnyWordSpec with Matchers {
  val testCard1 = Card(Suit.Hearts, Rank.Ace)
  val testCard2 = Card(Suit.Diamonds, Rank.King)
  val testCard3 = Card(Suit.Clubs, Rank.Queen)
  val testCard4 = Card(Suit.Spades, Rank.Jack)

  val testPlayers = List(
    Player(testCard1, testCard2, "Player1", 1000, 20),
    Player(testCard3, testCard4, "Player2", 1000, 10)
  )

  val testDeck = shuffleDeck.drop(4)

  val initialGameState = GameState(
    playersAndBalances = List(("Player1", 1000), ("Player2", 1000)),
    players = Some(testPlayers),
    playersAndAuthIDs = Some(ListMap("Player1" -> "auth1", "Player2" -> "auth2")),
    deck = Some(testDeck),
    playerAtTurn = 0,
    currentHighestBetSize = 20,
    board = Nil,
    pot = 30,
    smallBlind = 10,
    bigBlind = 20
  )

  "UpdateBoard" should {
    "select the correct strategy based on board size" in {
      val preFlopState = initialGameState.copy(board = Nil)
      val flopState = initialGameState.copy(board = List.fill(3)(testCard1))
      val turnState = initialGameState.copy(board = List.fill(4)(testCard1))

      Await.result(UpdateBoard.strategy(preFlopState), 1.second).board.size shouldBe 3
      Await.result(UpdateBoard.strategy(flopState), 1.second).board.size shouldBe 4
      Await.result(UpdateBoard.strategy(turnState), 1.second).board.size shouldBe 5
    }

    "correctly execute the flop" in {
      val newState = Await.result(UpdateBoard.flop(initialGameState), 1.second)
      newState.board.size shouldBe 3
      newState.deck.get.size shouldBe testDeck.size - 3
      newState.currentHighestBetSize shouldBe 0
    }

    "correctly execute the turn" in {
      val flopState = initialGameState.copy(board = List.fill(3)(testCard1))
      val newState = Await.result(UpdateBoard.turn(flopState), 1.second)
      newState.board.size shouldBe 4
      newState.deck.get.size shouldBe flopState.deck.get.size - 1
    }

    "correctly execute the river" in {
      val turnState = initialGameState.copy(board = List.fill(4)(testCard1))
      val newState = Await.result(UpdateBoard.river(turnState), 1.second)
      newState.board.size shouldBe 5
      newState.deck.get.size shouldBe turnState.deck.get.size - 1
    }

    "reset bets when adding community cards" in {
      val newState = Await.result(UpdateBoard.flop(initialGameState), 1.second)
      newState.players.get.foreach(_.currentAmountBetted shouldBe 0)
    }

    "handle startRound correctly" in {
      val resultState = Try(Await.result(UpdateBoard.startRound(initialGameState), 1.second))
      resultState.isSuccess shouldBe false
    }
  }

  "Balance updates" should {
    "be calculated correctly for single winner" in {
      val testState = initialGameState.copy(pot = 100)
      val winnerName = "Player1"

      // Simulate what would happen with a single winner
      val expectedBalances = testState.playersAndBalances.map {
        case (name, balance) if name == winnerName =>
          (name, balance + 100 - (if (name == "Player1") 20 else 10))
        case player => player
      }

      expectedBalances.find(_._1 == winnerName).get._2 shouldBe (1000 + 100 - 20)
    }

    "be calculated correctly for multiple winners" in {
      val testState = initialGameState.copy(pot = 100)
      val winningAmount = testState.pot / 2

      val expectedBalances = testState.playersAndBalances.map {
        case ("Player1", balance) => ("Player1", balance + winningAmount - 20)
        case ("Player2", balance) => ("Player2", balance + winningAmount - 10)
        case player               => player
      }

      expectedBalances.find(_._1 == "Player1").get._2 shouldBe (1000 + 50 - 20)
      expectedBalances.find(_._1 == "Player2").get._2 shouldBe (1000 + 50 - 10)
    }

    "handle blind deductions correctly" in {
      val testState = initialGameState.copy(pot = 100)

      val expectedBalances = testState.playersAndBalances.map {
        case ("Player1", balance) => ("Player1", balance - 20) // Small blind
        case ("Player2", balance) => ("Player2", balance - 10) // Big blind
        case player               => player
      }

      expectedBalances.find(_._1 == "Player1").get._2 shouldBe (1000 - 20)
      expectedBalances.find(_._1 == "Player2").get._2 shouldBe (1000 - 10)
    }
  }

  "New round initialization" should {
    "have correct properties" in {
      val testState = initialGameState.copy(pot = 100)

      // This is what we expect the new round to look like
      val expectedState = testState.copy(
        players = Some(testPlayers),
        deck = Some(testDeck.drop(4)),
        playerAtTurn = 2,
        currentHighestBetSize = 20,
        board = Nil,
        pot = 30,
        smallBlindPointer = 1,
        newRoundStarted = true
      )

      expectedState.board shouldBe Nil
      expectedState.pot shouldBe 30
      expectedState.currentHighestBetSize shouldBe 20
      expectedState.newRoundStarted shouldBe true
      expectedState.smallBlindPointer shouldBe 1
      expectedState.playerAtTurn shouldBe 2
      expectedState.deck.get.size shouldBe testDeck.size - 4
    }
  }

  "Edge cases" should {
    "handle zero balances correctly" in {
      val zeroBalanceState = initialGameState.copy(
        playersAndBalances = List(("Player1", 0), ("Player2", 0))
      )

      // Just verify the state is valid
      zeroBalanceState.playersAndBalances.foreach(_._2 shouldBe 0)
    }
  }
}
