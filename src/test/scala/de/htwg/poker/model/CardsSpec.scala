import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.model._

class CardsSpec extends AnyWordSpec with Matchers {
  val ANSI_BLACK = "\u001b[30m"
  val ANSI_RED = "\u001b[31m"
  val ANSI_RESET = "\u001b[0m"

  "A Card" when {
    "created" should {
      "have the correct suit and rank" in {
        val card = new Card(Suit.Clubs, Rank.Ace)
        card.suit should be(Suit.Clubs)
        card.rank should be(Rank.Ace)
      }
    }

    "converted to string" should {
      "return the correct string representation" in {
        val card = new Card(Suit.Spades, Rank.King)
        card.toString should be(s"[K$ANSI_BLACK♠$ANSI_RESET]")
      }
    }

    "converted to HTML" should {
      "return the correct HTML representation" in {
        val card = new Card(Suit.Diamonds, Rank.Two)
        card.toHtml should be(
          "<div class=\"rounded-lg bg-slate-100 w-6 h-9 hover:scale-125 flex flex-col justify-center items-center shadow-xl shadow-black/50\">&#x2666;<h1 class=\"font-bold \">2</h1></div>"
        )
      }
    }
  }

  "A Rank" should {
    "have the correct string representation" in {
      Rank.Two.toString should be("2")
      Rank.Three.toString should be("3")
      Rank.Four.toString should be("4")
      Rank.Five.toString should be("5")
      Rank.Six.toString should be("6")
      Rank.Seven.toString should be("7")
      Rank.Eight.toString should be("8")
      Rank.Nine.toString should be("9")
      Rank.Ten.toString should be("10")
      Rank.Jack.toString should be("J")
      Rank.Queen.toString should be("Q")
      Rank.King.toString should be("K")
      Rank.Ace.toString should be("A")
    }

    "have the correct strength" in {
      Rank.Two.strength should be(1)
      Rank.Three.strength should be(2)
      Rank.Four.strength should be(3)
      Rank.Five.strength should be(4)
      Rank.Six.strength should be(5)
      Rank.Seven.strength should be(6)
      Rank.Eight.strength should be(7)
      Rank.Nine.strength should be(8)
      Rank.Ten.strength should be(9)
      Rank.Jack.strength should be(10)
      Rank.Queen.strength should be(11)
      Rank.King.strength should be(12)
      Rank.Ace.strength should be(13)
    }
  }

  "A Suit" should {
    "have the correct string representation" in {
      Suit.Clubs.toString should be(s"$ANSI_BLACK♣$ANSI_RESET")
      Suit.Spades.toString should be(s"$ANSI_BLACK♠$ANSI_RESET")
      Suit.Diamonds.toString should be(s"$ANSI_RED♢$ANSI_RESET")
      Suit.Hearts.toString should be(s"$ANSI_RED♡$ANSI_RESET")
    }

    "have the correct id" in {
      Suit.Clubs.id should be(1)
      Suit.Spades.id should be(2)
      Suit.Diamonds.id should be(3)
      Suit.Hearts.id should be(4)
    }

    "have the correct HTML representation" in {
      Suit.Clubs.toHtml should be(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-club-fill\" viewBox=\"0 0 16 16\"><path d=\"M11.5 12.5a3.493 3.493 0 0 1-2.684-1.254 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907 3.5 3.5 0 1 1-2.538-5.743 3.5 3.5 0 1 1 6.708 0A3.5 3.5 0 1 1 11.5 12.5\"/></svg>"
      )
      Suit.Spades.toHtml should be(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-spade-fill\" viewBox=\"0 0 16 16\"><path d=\"M7.184 11.246A3.5 3.5 0 0 1 1 9c0-1.602 1.14-2.633 2.66-4.008C4.986 3.792 6.602 2.33 8 0c1.398 2.33 3.014 3.792 4.34 4.992C13.86 6.367 15 7.398 15 9a3.5 3.5 0 0 1-6.184 2.246 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907\"/></svg>"
      )
      Suit.Diamonds.toHtml should be(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-diamond-fill\" viewBox=\"0 0 16 16\"><path fill-rule=\"evenodd\" d=\"M6.95.435c.58-.58 1.52-.58 2.1 0l6.515 6.516c.58.58.58 1.519 0 2.098L9.05 15.565c-.58.58-1.519.58-2.098 0L.435 9.05a1.482 1.482 0 0 1 0-2.098L6.95.435z\"/></svg>"
      )
      Suit.Hearts.toHtml should be(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-suit-heart-fill\" viewBox=\"0 0 16 16\"><path d=\"M4 1c2.21 0 4 1.755 4 3.92C8 2.755 9.79 1 12 1s4 1.755 4 3.92c0 3.263-3.234 4.414-7.608 9.608a.513.513 0 0 1-.784 0C3.234 9.334 0 8.183 0 4.92 0 2.755 1.79 1 4 1\"/></svg>"
      )
    }
  }
}
