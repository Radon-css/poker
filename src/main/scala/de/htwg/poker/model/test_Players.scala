package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" when {
    val card1 = Card(Suit.Hearts, Rank.Ace)
    val card2 = Card(Suit.Diamonds, Rank.King)
    val playername = "John Doe"
    val balance = 1000
    val currentAmountBetted = 0
    val player = Player(card1, card2, playername, balance, currentAmountBetted)

    "created" should {
      "have the correct card1" in {
        player.card1 should be(card1)
      }

      "have the correct card2" in {
        player.card2 should be(card2)
      }

      "have the correct playername" in {
        player.playername should be(playername)
      }

      "have the correct balance" in {
        player.balance should be(balance)
      }

      "have the correct currentAmountBetted" in {
        player.currentAmountBetted should be(currentAmountBetted)
      }
    }

    "balanceToString" should {
      "return the balance as a string" in {
        player.balanceToString() should be(s"($balance$$)")
      }
    }

    "toHtml" should {
      "return the player's HTML representation" in {
        val expectedHtml =
          s"""<div class=\"flex flex-col items-center justify-center space-x-2\">
                <div class=\"rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5\">
                  <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-person-fill\" viewBox=\"0 0 16 16\">
                    <path d=\"M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6\"/>
                  </svg>
                </div>
                    <p class=\"p-1 text-slate-100\">$playername</p>
                    <div class=\"flex items-center justify-center rounded-full bg-slate-100 text-gray-700 w-14\">
                      <p class=\"p-1\">$balance$$</p>
                    </div>
              </div>"""
        player.toHtml should be(expectedHtml)
      }
    }

    "betSizeToHtml" should {
      "return the bet size HTML representation" in {
        val expectedHtml =
          s""" <div class = \"rounded-full bg-gray-700/80 h-6 w-11\">
                      <div class = \"flex items-center justify-center py-0\">
                      <h1 class=\"text-gray-400\">$currentAmountBetted$$</h1>
                    </div>
                    </div>
                    """
        player.betSizeToHtml should be(expectedHtml)
      }
    }
  }
}