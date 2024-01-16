package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.controller.Controller
import de.htwg.poker.model._

class GameStateSpec extends AnyWordSpec with Matchers {
  "A GameState" when {
    val card1 = Card(Suit.Hearts, Rank.Ace)
    val card2 = Card(Suit.Diamonds, Rank.King)
    val player1 = Player(card1, card2, "John Doe", 1000, 0)
    val player2 = Player(card1, card2, "Jane Smith", 2000, 0)
    val players = List(player1, player2)
    val deck = List(card1, card2)
    val gameState = GameState(players, Some(players), Some(deck))
    val controller = new Controller(gameState)

    "created" should {
      "have the correct players" in {
        gameState.getPlayers should be(players)
      }

      "have the correct deck" in {
        gameState.getDeck should be(deck)
      }

      "have the correct player at turn" in {
        gameState.getPlayerAtTurn should be(0)
      }

      "have the correct highest bet size" in {
        gameState.getHighestBetSize should be(0)
      }

      "have the correct board" in {
        gameState.getBoard should be(Nil)
      }

      "have the correct small blind" in {
        gameState.getSmallBlind should be(10)
      }

      "have the correct big blind" in {
        gameState.getBigBlind should be(20)
      }

      "have the correct pot" in {
        gameState.getPot should be(30)
      }

      "have the correct original players" in {
        gameState.getOriginalPlayers should be(players)
      }

      "have the correct small blind pointer" in {
        gameState.getSmallBlindPointer should be(0)
      }
    }

    "bet" should {
      "update the player's balance and current amount betted" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        val updatedPlayer = updatedGameState.getPlayers(0)
        updatedPlayer.balance should be(player1.balance - amount)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + amount
        )
      }

      "update the pot and highest bet size" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.getPot should be(gameState.getPot + amount)
        updatedGameState.getHighestBetSize should be(amount)
      }

      "update the player at turn" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

    "call" should {
      "update the player's balance and current amount betted" in {
        val updatedGameState = gameState.call
        val updatedPlayer = updatedGameState.getPlayers(0)
        updatedPlayer.balance should be(player1.balance - 20)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + 20
        )
      }

      "update the pot and highest bet size" in {
        val updatedGameState = gameState.call
        updatedGameState.getPot should be(gameState.getHighestBetSize + 20)
        updatedGameState.getHighestBetSize should be(20)
      }

      "update the player at turn" in {
        val updatedGameState = gameState.call
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

    "fold" should {
      "remove the player from the game" in {
        val updatedGameState = gameState.fold
        updatedGameState.getPlayers should be(List(player2))
      }

      "update the player at turn" in {
        val updatedGameState = gameState.fold
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

    "allin" should {
      "update the player's balance and current amount betted" in {
        val updatedGameState = gameState.allIn
        val updatedPlayer = updatedGameState.getPlayers(0)
        updatedPlayer.balance should be(0)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + 1000
        )
      }

      "update the pot and highest bet size" in {
        val updatedGameState = gameState.allIn
        updatedGameState.getPot should be(gameState.getPot + 1000)
        updatedGameState.getHighestBetSize should be(1000)
      }

      "update the player at turn" in {
        val updatedGameState = gameState.allIn
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

    "check" should {
      "update the player at turn" in {
        val updatedGameState = gameState.check
        updatedGameState.getPlayerAtTurn should be(1)
      }
    }

  }

  "UpdateBoard" should {
    "return the correct GameState when starting a new round" in {
      val player1 =
        Player(
          new Card(Suit.Clubs, Rank.Ace),
          new Card(Suit.Clubs, Rank.Eight),
          "John Doe",
          1000,
          0
        )
      val player2 = Player(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Eight),
        "Jane Smith",
        2000,
        0
      )
      val gameState = GameState(
        List(player1, player2),
        Some(List(player1, player2)),
        None,
        0,
        20,
        Nil,
        30,
        10,
        20,
        0
      )

      val updatedGameState = gameState.UpdateBoard.startRound

      updatedGameState.getOriginalPlayers should be(
        gameState.getOriginalPlayers
      )
      updatedGameState.getPlayers should not be gameState.getPlayers
      updatedGameState.getDeck should not be gameState.getDeck
      updatedGameState.getPlayerAtTurn should be(2)
      updatedGameState.getBigBlind should be(20)
      updatedGameState.getBoard should be(Nil)
      updatedGameState.getPot should be(30)
      updatedGameState.getSmallBlind should be(10)
      updatedGameState.getSmallBlindPointer should be(0)
    }

    "return the correct GameState when revealing the flop" in {
      val player1 =
        Player(
          new Card(Suit.Clubs, Rank.Ace),
          new Card(Suit.Clubs, Rank.Eight),
          "John Doe",
          1000,
          0
        )
      val player2 = Player(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Eight),
        "Jane Smith",
        2000,
        0
      )
      val gameState = GameState(
        List(player1, player2),
        Some(List(player1, player2)),
        None,
        0,
        20,
        Nil,
        30,
        10,
        20,
        0
      )

      val updatedGameState = gameState.UpdateBoard.flop

      updatedGameState.getOriginalPlayers should be(
        gameState.getOriginalPlayers
      )
      updatedGameState.getPlayers should not be gameState.getPlayers
      updatedGameState.getDeck should not be gameState.getDeck
      updatedGameState.getPlayerAtTurn should be(0)
      updatedGameState.getBigBlind should be(0)
      updatedGameState.getPot should be(30)
      updatedGameState.getSmallBlind should be(10)
      updatedGameState.getSmallBlindPointer should be(0)
    }

    "return the correct GameState when revealing the turn" in {
      val player1 =
        Player(
          new Card(Suit.Clubs, Rank.Ace),
          new Card(Suit.Clubs, Rank.Eight),
          "John Doe",
          1000,
          0
        )
      val player2 = Player(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Eight),
        "Jane Smith",
        2000,
        0
      )
      val gameState = GameState(
        List(player1, player2),
        Some(List(player1, player2)),
        None,
        0,
        20,
        Nil,
        30,
        10,
        20,
        0
      )

      val updatedGameState = gameState.UpdateBoard.turn

      updatedGameState.getOriginalPlayers should be(
        gameState.getOriginalPlayers
      )
      updatedGameState.getPlayers should not be gameState.getPlayers
      updatedGameState.getDeck should not be gameState.getDeck
      updatedGameState.getPlayerAtTurn should be(0)
      updatedGameState.getBigBlind should be(0)
      updatedGameState.getPot should be(30)
      updatedGameState.getSmallBlind should be(10)
      updatedGameState.getSmallBlindPointer should be(0)
    }

    "return the correct GameState when revealing the river" in {
      val player1 =
        Player(
          new Card(Suit.Clubs, Rank.Ace),
          new Card(Suit.Clubs, Rank.Eight),
          "John Doe",
          1000,
          0
        )
      val player2 = Player(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Eight),
        "Jane Smith",
        2000,
        0
      )
      val gameState = GameState(
        List(player1, player2),
        Some(List(player1, player2)),
        None,
        0,
        20,
        Nil,
        30,
        10,
        20,
        0
      )

      val updatedGameState = gameState.UpdateBoard.river

      updatedGameState.getOriginalPlayers should be(
        gameState.getOriginalPlayers
      )
      updatedGameState.getPlayers should not be gameState.getPlayers
      updatedGameState.getDeck should not be gameState.getDeck
      updatedGameState.getPlayerAtTurn should be(0)
      updatedGameState.getBigBlind should be(0)
      updatedGameState.getPot should be(30)
      updatedGameState.getSmallBlind should be(10)
      updatedGameState.getSmallBlindPointer should be(0)
    }
  }
}
