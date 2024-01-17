package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A Player" when {
    "created" should {
      "have the correct initial values" in {
        val card1 = mock[Card]
        val card2 = mock[Card]
        val playername = "John Doe"
        val balance = 1000
        val currentAmountBetted = 0

        val player =
          Player(card1, card2, playername, balance, currentAmountBetted)

        player.card1 should be(card1)
        player.card2 should be(card2)
        player.playername should be(playername)
        player.balance should be(balance)
        player.currentAmountBetted should be(currentAmountBetted)
      }
    }

    "calling balanceToString" should {
      "return the balance as a string" in {
        val player = Player(mock[Card], mock[Card], "John Doe", 1000, 0)

        val balanceString = player.balanceToString

        balanceString should be("(1000$)")
      }
    }

    "calling toHtml" should {
      "return the player's HTML representation" in {
        val player = Player(
          mock[Card],
          mock[Card],
          "John Doe",
          1000,
          0
        )

        val html = player.toHtml

        html should include(
          "<p class=\"p-1 text-slate-100\">John Doe</p>"
        )
      }
    }

    "calling betSizeToHtml" should {
      "return the player's bet size HTML representation" in {
        val player = Player(
          mock[Card],
          mock[Card],
          "John Doe",
          1000,
          0
        )
        val html = player.betSizeToHtml

        html should include(
          "<h1 class=\"text-gray-400\">0$</h1>"
        )
      }
    }
  }
}
