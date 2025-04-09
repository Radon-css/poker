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
        gameState.players.getOrElse(List.empty[Player]) should be(players)
      }

      "have the correct deck" in {
        gameState.deck.getOrElse(List.empty[Card]) should be(deck)
      }

      "have the correct player at turn" in {
        gameState.playerAtTurn should be(0)
      }

      "have the correct highest bet size" in {
        gameState.currentHighestBetSize should be(0)
      }

      "have the correct board" in {
        gameState.board should be(Nil)
      }

      "have the correct small blind" in {
        gameState.smallBlind should be(10)
      }

      "have the correct big blind" in {
        gameState.bigBlind should be(20)
      }

      "have the correct pot" in {
        gameState.pot should be(30)
      }

      "have the correct original players" in {
        gameState.getOriginalPlayers should be(players)
      }

      "have the correct small blind pointer" in {
        gameState.smallBlindPointer should be(0)
      }
    }

    "bet" should {
      "update the player's balance and current amount betted" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        val updatedPlayer = updatedGameState.players.getOrElse(List.empty[Player])(0)
        updatedPlayer.balance should be(player1.balance - amount)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + amount
        )
      }

      "update the pot and highest bet size" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.pot should be(gameState.pot + amount)
        updatedGameState.currentHighestBetSize should be(amount)
      }

      "update the player at turn" in {
        val amount = 100
        val updatedGameState = gameState.bet(amount)
        updatedGameState.playerAtTurn should be(1)
      }
    }

    "call" should {
      "update the player's balance and current amount betted" in {
        val updatedGameState = gameState.call
        val updatedPlayer = updatedGameState.players.getOrElse(List.empty[Player])(0)
        updatedPlayer.balance should be(player1.balance - 20)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + 20
        )
      }

      "update the pot and highest bet size" in {
        val updatedGameState = gameState.call
        updatedGameState.pot should be(gameState.currentHighestBetSize + 20)
        updatedGameState.currentHighestBetSize should be(20)
      }

      "update the player at turn" in {
        val updatedGameState = gameState.call
        updatedGameState.playerAtTurn should be(1)
      }
    }

    "fold" should {
      "remove the player from the game" in {
        val updatedGameState = gameState.fold
        updatedGameState.players.getOrElse(List.empty[Player]) should be(List(player2))
      }

      "update the player at turn" in {
        val updatedGameState = gameState.fold
        updatedGameState.playerAtTurn should be(1)
      }
    }

    "allin" should {
      "update the player's balance and current amount betted" in {
        val updatedGameState = gameState.allIn
        val updatedPlayer = updatedGameState.players.getOrElse(List.empty[Player])(0)
        updatedPlayer.balance should be(0)
        updatedPlayer.currentAmountBetted should be(
          player1.currentAmountBetted + 1000
        )
      }

      "update the pot and highest bet size" in {
        val updatedGameState = gameState.allIn
        updatedGameState.pot should be(gameState.pot + 1000)
        updatedGameState.currentHighestBetSize should be(1000)
      }

      "update the player at turn" in {
        val updatedGameState = gameState.allIn
        updatedGameState.playerAtTurn should be(1)
      }
    }

    "check" should {
      "update the player at turn" in {
        val updatedGameState = gameState.check
        updatedGameState.playerAtTurn should be(1)
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
      updatedGameState.players.getOrElse(List.empty[Player]) should not be gameState.players.getOrElse(List.empty[Player])
      updatedGameState.deck.getOrElse(List.empty[Card]) should not be gameState.deck.getOrElse(List.empty[Card])
      updatedGameState.playerAtTurn should be(2)
      updatedGameState.bigBlind should be(20)
      updatedGameState.board should be(Nil)
      updatedGameState.pot should be(30)
      updatedGameState.smallBlind should be(10)
      updatedGameState.smallBlindPointer should be(0)
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
      updatedGameState.players.getOrElse(List.empty[Player]) should not be gameState.players.getOrElse(List.empty[Player])
      updatedGameState.deck.getOrElse(List.empty[Card]) should not be gameState.deck.getOrElse(List.empty[Card])
      updatedGameState.playerAtTurn should be(0)
      updatedGameState.bigBlind should be(0)
      updatedGameState.pot should be(30)
      updatedGameState.smallBlind should be(10)
      updatedGameState.smallBlindPointer should be(0)
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
      updatedGameState.players.getOrElse(List.empty[Player]) should not be gameState.players.getOrElse(List.empty[Player])
      updatedGameState.deck.getOrElse(List.empty[Card]) should not be gameState.deck.getOrElse(List.empty[Card])
      updatedGameState.playerAtTurn should be(0)
      updatedGameState.bigBlind should be(0)
      updatedGameState.pot should be(30)
      updatedGameState.smallBlind should be(10)
      updatedGameState.smallBlindPointer should be(0)
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
      updatedGameState.players.getOrElse(List.empty[Player]) should not be gameState.players.getOrElse(List.empty[Player])
      updatedGameState.deck.getOrElse(List.empty[Card]) should not be gameState.deck.getOrElse(List.empty[Card])
      updatedGameState.playerAtTurn should be(0)
      updatedGameState.bigBlind should be(0)
      updatedGameState.pot should be(30)
      updatedGameState.smallBlind should be(10)
      updatedGameState.smallBlindPointer should be(0)
      updatedGameState.getPreviousPlayer should be(1)
    }
  }
}
