package de.htwg.poker.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardsSpec extends AnyWordSpec with Matchers {

  "A Rank" should {
    "have correct string representations" in {
      Rank.Two.toString shouldBe "2"
      Rank.Ace.toString shouldBe "A"
      Rank.Jack.toString shouldBe "J"
    }

    "have correct strength values" in {
      Rank.Two.strength shouldBe 1
      Rank.Ace.strength shouldBe 13
      Rank.Seven.strength shouldBe 6
    }

    "have correct prime values" in {
      Rank.Two.prime shouldBe 2
      Rank.Three.prime shouldBe 3
      Rank.Five.prime shouldBe 7
      Rank.Ace.prime shouldBe 41
    }

    "have all 13 values" in {
      Rank.values.size shouldBe 13
    }
  }

  "A Suit" should {
    "have correct string representations" in {
      Suit.Clubs.toString shouldBe "♣"
      Suit.Hearts.toString shouldBe "♡"
    }

    "have correct ids" in {
      Suit.Clubs.id shouldBe 1
      Suit.Spades.id shouldBe 2
      Suit.Diamonds.id shouldBe 3
      Suit.Hearts.id shouldBe 4
    }

    "have valid HTML representations" in {
      Suit.Clubs.toHtml should include("bi-suit-club-fill")
      Suit.Diamonds.toHtml should include("fill=\"red\"")
      Suit.Hearts.toHtml should include("bi-suit-heart-fill")
    }

    "have all 4 values" in {
      Suit.values.size shouldBe 4
    }
  }

  "A Card" should {
    val testCard = Card(Suit.Hearts, Rank.Ace)

    "have a string representation" in {
      testCard.toString shouldBe "[A♡]"
      Card(Suit.Clubs, Rank.Two).toString shouldBe "[2♣]"
    }

    "have an HTML representation" in {
      testCard.toHtml should include("A")
      testCard.toHtml should include("bi-suit-heart-fill")
      testCard.toHtml should include("bg-slate-100")
    }

    "be constructed with suit and rank" in {
      testCard.suit shouldBe Suit.Hearts
      testCard.rank shouldBe Rank.Ace
    }
  }

  "The Deck" should {
    "contain 52 cards" in {
      deck.size shouldBe 52
    }

    "contain all combinations of ranks and suits" in {
      val allCombinations = for {
        rank <- Rank.values
        suit <- Suit.values
      } yield Card(suit, rank)

      allCombinations.foreach { card =>
        deck should contain(card)
      }
    }

    "shuffle to a different order" in {
      val shuffled1 = shuffleDeck
      val shuffled2 = shuffleDeck

      // There's a tiny chance this could fail even with correct implementation
      shuffled1 should not equal shuffled2
      shuffled1.toSet shouldEqual shuffled2.toSet
      shuffled1.size shouldBe 52
    }

    "maintain all cards after shuffling" in {
      val shuffled = shuffleDeck
      shuffled.toSet shouldEqual deck.toSet
    }
  }
}
