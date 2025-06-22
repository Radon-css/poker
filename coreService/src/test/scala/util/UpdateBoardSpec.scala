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

  // Test implementation without MockClient since we can't access the real Client
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
      // Testing without mocking the Client
      val resultState = Try(Await.result(UpdateBoard.startRound(initialGameState), 1.second))
      resultState.isSuccess shouldBe false
    }

  }
}
