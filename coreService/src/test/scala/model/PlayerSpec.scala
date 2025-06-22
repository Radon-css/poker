package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayersSpec extends AnyWordSpec with Matchers {
  val testCard1 = Card(Suit.Hearts, Rank.Ace)
  val testCard2 = Card(Suit.Diamonds, Rank.King)
  val testPlayer = Player(testCard1, testCard2, "TestPlayer", 1000, 200)

  "A Player" should {
    "be initialized with correct values" in {
      testPlayer.playername shouldBe "TestPlayer"
      testPlayer.balance shouldBe 1000
      testPlayer.currentAmountBetted shouldBe 200
      testPlayer.folded shouldBe false
      testPlayer.checkedThisRound shouldBe false
    }

    "have proper string representation for balance" in {
      testPlayer.balanceToString shouldBe "(1000$)"
      Player(testCard1, testCard2, "LowBalance", 50).balanceToString shouldBe "(50$)"
    }

    "correctly represent folded state in HTML" in {
      val foldedPlayer = testPlayer.copy(folded = true)
      foldedPlayer.toHtml should include("opacity-50")
      testPlayer.toHtml should not include "opacity-50"
    }

    "display player information correctly in HTML" in {
      val html = testPlayer.toHtml
      html should include("TestPlayer")
      html should include("1000$")
      html should include("bi-person-fill")
      html should include("bg-gray-600")
    }

    "display bet size correctly in HTML" in {
      testPlayer.betSizeToHtml should include("200$")
      val noBetPlayer = testPlayer.copy(currentAmountBetted = 0)
      noBetPlayer.betSizeToHtml should include("0$")
    }

    "maintain immutability" in {
      // Verify that modifications create new instances
      val newPlayer = testPlayer.copy(balance = 900)
      newPlayer.balance shouldBe 900
      testPlayer.balance shouldBe 1000 // Original unchanged
    }

    "handle edge cases for balance" in {
      val richPlayer = Player(testCard1, testCard2, "Rich", Int.MaxValue)
      richPlayer.balanceToString shouldBe s"(${Int.MaxValue}$$)"

      val brokePlayer = Player(testCard1, testCard2, "Broke", 0)
      brokePlayer.balanceToString shouldBe "(0$)"

      val debtPlayer = Player(testCard1, testCard2, "Debt", -100)
      debtPlayer.balanceToString shouldBe "(-100$)"
    }

    "handle special characters in player name" in {
      val specialNamePlayer = Player(testCard1, testCard2, "Jöhn_D'œ", 500)
      specialNamePlayer.toHtml should include("Jöhn_D'œ")
    }
  }

  "Player's betting status" should {
    "be reflected in HTML representation" in {
      val bigBetter = testPlayer.copy(currentAmountBetted = 500)
      bigBetter.betSizeToHtml should include("500$")

      val allInPlayer = testPlayer.copy(currentAmountBetted = 1000)
      allInPlayer.betSizeToHtml should include("1000$")
    }
  }

  "Player's folded state" should {
    "affect HTML rendering" in {
      val activePlayer = testPlayer.copy(folded = false)
      activePlayer.toHtml should not include "opacity-50"

      val foldedPlayer = testPlayer.copy(folded = true)
      foldedPlayer.toHtml should include("opacity-50")
    }
  }

  "Player's checked state" should {
    "not affect HTML rendering by default" in {
      val checkedPlayer = testPlayer.copy(checkedThisRound = true)
      checkedPlayer.toHtml shouldEqual testPlayer.toHtml
    }
  }
}
