import de.htwg.poker.eval.HandInfo
import de.htwg.poker.eval.types.EvalCard
import de.htwg.poker.eval.types.EvalRank._
import de.htwg.poker.eval.types.EvalSuit._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HandInfoSpec extends AnyWordSpec with Matchers {

  "HandInfo" should {

    "evaluate hands correctly" when {

      "evaluating High Card" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, King))
        val boardCards = List(
          EvalCard(Clubs, Queen),
          EvalCard(Spades, Jack),
          EvalCard(Hearts, Nine),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "High"
      }

      "evaluating Pair" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Queen),
          EvalCard(Spades, Jack),
          EvalCard(Hearts, Nine),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Pair"
      }

      "evaluating Two Pair" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Queen),
          EvalCard(Spades, Queen),
          EvalCard(Hearts, Nine),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "TwoPair"
      }

      "evaluating Three of a Kind" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Ace),
          EvalCard(Spades, Queen),
          EvalCard(Hearts, Nine),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Triples"
      }

      "evaluating Straight" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, King))
        val boardCards = List(
          EvalCard(Clubs, Queen),
          EvalCard(Spades, Jack),
          EvalCard(Hearts, Ten),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Straight"
      }

      "evaluating Wheel Straight (A-2-3-4-5)" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Two))
        val boardCards = List(
          EvalCard(Clubs, Three),
          EvalCard(Spades, Four),
          EvalCard(Hearts, Five),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Nine)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Straight"
      }

      "evaluating Flush" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Hearts, King))
        val boardCards = List(
          EvalCard(Hearts, Queen),
          EvalCard(Hearts, Jack),
          EvalCard(Diamonds, Nine),
          EvalCard(Hearts, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Flush"
      }

      "evaluating Full House" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Ace),
          EvalCard(Spades, Queen),
          EvalCard(Hearts, Queen),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "FullHouse"
      }

      "evaluating Four of a Kind" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Ace),
          EvalCard(Spades, Ace),
          EvalCard(Hearts, Queen),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Quads"
      }

      "evaluating Straight Flush" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Hearts, King))
        val boardCards = List(
          EvalCard(Hearts, Queen),
          EvalCard(Hearts, Jack),
          EvalCard(Hearts, Ten),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "StraightFlush"
      }

      "evaluating Royal Flush (as Straight Flush)" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Hearts, King))
        val boardCards = List(
          EvalCard(Hearts, Queen),
          EvalCard(Hearts, Jack),
          EvalCard(Hearts, Ten),
          EvalCard(Diamonds, Seven),
          EvalCard(Clubs, Five)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "StraightFlush"
      }
    }

    "find the best possible hand" when {
      "there are multiple combinations" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, Ace))
        val boardCards = List(
          EvalCard(Clubs, Ace),
          EvalCard(Spades, Ace),
          EvalCard(Hearts, King),
          EvalCard(Diamonds, King),
          EvalCard(Clubs, King)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Quads"
      }

      "there are multiple possible straights" in {
        val playerCards = List(EvalCard(Hearts, Ten), EvalCard(Diamonds, Nine))
        val boardCards = List(
          EvalCard(Clubs, Eight),
          EvalCard(Spades, Seven),
          EvalCard(Hearts, Six),
          EvalCard(Diamonds, Five),
          EvalCard(Clubs, Four)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Straight"
      }
    }

    "handle edge cases" when {
      "there are exactly 5 cards" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Diamonds, King))
        val boardCards = List(
          EvalCard(Clubs, Queen),
          EvalCard(Spades, Jack),
          EvalCard(Hearts, Ten)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "Straight"
      }

      "all cards are of the same suit" in {
        val playerCards = List(EvalCard(Hearts, Ace), EvalCard(Hearts, King))
        val boardCards = List(
          EvalCard(Hearts, Queen),
          EvalCard(Hearts, Jack),
          EvalCard(Hearts, Ten)
        )
        HandInfo.evalHand(playerCards, boardCards) shouldBe "StraightFlush"
      }
    }
  }

  "combinations function" should {
    "generate all possible combinations" in {
      val list = List(1, 2, 3)
      val result = HandInfo.combinations(2, list)
      result should contain allOf (List(1, 2), List(1, 3), List(2, 3))
      result should have size 3
    }

    "return empty list when n is 0" in {
      HandInfo.combinations(0, List(1, 2, 3)) shouldBe List(Nil)
    }

    "return empty list when input list is empty" in {
      HandInfo.combinations(2, List.empty) shouldBe Nil
    }
  }

  "getStrength function" should {
    "return correct strength values" in {
      HandInfo.getStrength(Two) shouldBe 1
      HandInfo.getStrength(Three) shouldBe 2
      HandInfo.getStrength(Ace) shouldBe 13
    }
  }

  "getId function" should {
    "return correct suit IDs" in {
      HandInfo.getId(Clubs) shouldBe 1
      HandInfo.getId(Spades) shouldBe 2
      HandInfo.getId(Diamonds) shouldBe 3
      HandInfo.getId(Hearts) shouldBe 4
    }
  }
}
