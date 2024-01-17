package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model.{GameState, Player, Card, Suit, Rank}

class TUIViewSpec extends AnyWordSpec with Matchers {
  "TUIView" should {
    "update the game state and return a string representation" in {
      val card1 = Card(Suit.Hearts, Rank.Ace)
      val card2 = Card(Suit.Diamonds, Rank.King)
      val player1 = Player(card1, card2, "John Doe", 1000, 0)
      val player2 = Player(card1, card2, "Jane Smith", 500, 0)
      val player3 = Player(card1, card2, "Bob Johnson", 750, 0)
      val players = List(player1, player2, player3)
      val board = List(card1, card2)
      val gameState = GameState(players, None, Some(board))

      val expectedOutput =
        """[...] [...] [...]
          |John Doe     Jane Smith   Bob Johnson 
          |AH KD        AH KD        AH KD       
          |0$           0$           0$          
          |
          |[*] [*] [*] [*] [*] 
          |
          |""".stripMargin

      val result = TUIView.update(gameState)
      result should be(expectedOutput)
    }
  }
}
