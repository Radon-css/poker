package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.immutable.ListMap
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class GameStateSpec extends AnyWordSpec with Matchers {
  val testCard1 = Card(Suit.Hearts, Rank.Ace)
  val testCard2 = Card(Suit.Diamonds, Rank.King)
  val testCard3 = Card(Suit.Clubs, Rank.Queen)
  val testCard4 = Card(Suit.Spades, Rank.Jack)

  val testPlayers = List(
    Player(testCard1, testCard2, "Player1", 1000, 20),
    Player(testCard3, testCard4, "Player2", 1000, 10)
  )

  val testDeck = shuffleDeck.drop(4) // Remove cards dealt to players

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

  "A GameState" should {
    "initialize correctly" in {
      initialGameState.players.get.size shouldBe 2
      initialGameState.deck.get.size shouldBe 48
      initialGameState.pot shouldBe 30
      initialGameState.currentHighestBetSize shouldBe 20
    }

    "handle betting correctly" in {
      val betAmount = 50
      val newState = initialGameState.bet(betAmount)

      newState.getCurrentPlayer.balance shouldBe 1000
      newState.pot shouldBe 30 + betAmount
      newState.currentHighestBetSize shouldBe 20 + betAmount
      newState.playerAtTurn shouldBe 1
    }

    "handle folding correctly" in {
      val newState = initialGameState.fold

      newState.players.get.head.folded shouldBe true
      newState.playerAtTurn shouldBe 1
    }

    "handle calling correctly" in {
      val newState = initialGameState.call

      newState.getCurrentPlayer.balance shouldBe 1000
      newState.pot shouldBe 30
      newState.playerAtTurn shouldBe 1
    }

    "handle checking correctly" in {
      val checkState = initialGameState.copy(currentHighestBetSize = 0)
      val newState = checkState.check

      newState.players.get.head.checkedThisRound shouldBe true
      newState.playerAtTurn shouldBe 1
    }

    "handle all-in correctly" in {
      val allInPlayer = initialGameState.players.get.head.copy(balance = 100)
      val allInState = initialGameState.copy(players = Some(List(allInPlayer, testPlayers(1))))
      val newState = allInState.allIn

      newState.getCurrentPlayer.balance shouldBe 1000
      newState.pot shouldBe 30 + 100
      newState.currentHighestBetSize shouldBe 100
    }

    "create a new game correctly" in {
      val playerNames = List("Alice", "Bob")
      val authIDs = ListMap("Alice" -> "auth1", "Bob" -> "auth2")
      val gameState = GameState(Nil, None, None, None).createGame(playerNames, Some(authIDs), 10, 20, 0)

      gameState.players.get.size shouldBe 2
      gameState.deck.get.size shouldBe 48
      gameState.pot shouldBe 30
      gameState.playerAtTurn shouldBe (if (playerNames.size < 3) 0 else 2)
    }

    "correctly determine next player" in {
      // Test with non-folded players
      initialGameState.getNextPlayer(0) shouldBe 1
      initialGameState.getNextPlayer(1) shouldBe 0

      // Test with folded players
      val foldedPlayers = List(
        testPlayers.head.copy(folded = true),
        testPlayers(1)
      )
      val foldedState = initialGameState.copy(players = Some(foldedPlayers))
      foldedState.getNextPlayer(0) shouldBe 1
      foldedState.getNextPlayer(1) shouldBe 1 // Should skip folded player 0
    }

    "correctly get current and previous players" in {
      initialGameState.getCurrentPlayer shouldBe testPlayers.head
      initialGameState.getPreviousPlayer shouldBe 1
    }

    "handle blind position calculations" in {
      initialGameState.getNextSmallBlindPlayer shouldBe 1
      initialGameState.getNextBigBlindPlayer shouldBe 0
    }

    "handle hand evaluation" in {
      val futureEval = initialGameState.getHandEval(0)
      val result = Try(Await.result(futureEval, 1.second))
      result.isSuccess shouldBe false
    }

  }
}
