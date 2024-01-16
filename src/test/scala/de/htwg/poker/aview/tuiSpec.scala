import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.poker.controller.Controller
import de.htwg.poker.aview.TUI
import de.htwg.poker.model._

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" when {
    "created" should {
      "register itself as an observer" in {
        val controller = new Controller(
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        )
        val tui = new TUI(controller)
        tui.update
      }
    }

    "processing input" should {
      "return true for valid 'start' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("start player1 player2 10")
        result shouldBe true
      }

      "return true for valid 'x' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("x")
        result shouldBe true
      }

      "return true for valid 'bet' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("bet 50")
        result shouldBe true
      }

      "return true for valid 'allin' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("allin")
        result shouldBe true
      }

      "return true for valid 'fold' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("fold")
        result shouldBe true
      }

      "return true for valid 'call' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("call")
        result shouldBe true
      }

      "return true for valid 'check' command" in {
        val controller = new Controller(
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        )
        val tui = new TUI(controller)
        val result = tui.processInput("check")
        result shouldBe true
      }

      "return true for 'q' command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("q")
        result shouldBe true
      }

      "return true for 'u' command" in {
        val players = List(
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Ten),
            "Frank",
            1000
          ),
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Nine),
            "Tom",
            1000
          )
        )
        val gameState =
          new GameState(
            players,
            Some(players),
            None,
            0,
            0,
            List.empty,
            0,
            0,
            0
          )
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        gameState.call
        val result = tui.processInput("u")
        result shouldBe true
      }

      "return true for 'r' command" in {
        val players = List(
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Ten),
            "Frank",
            1000
          ),
          new Player(
            new Card(Suit.Hearts, Rank.Ace),
            new Card(Suit.Hearts, Rank.Nine),
            "Tom",
            1000
          )
        )
        val gameState =
          new GameState(
            players,
            Some(players),
            None,
            0,
            0,
            List.empty,
            0,
            0,
            0
          )
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        gameState.call
        tui.processInput("u")
        val result = tui.processInput("r")
        result shouldBe true
      }

      "return false for invalid command" in {
        val gameState =
          new GameState(List.empty, None, None, 0, 0, List.empty, 0, 0, 0)
        val controller = new Controller(gameState)
        val tui = new TUI(controller)
        val result = tui.processInput("invalid")
        result shouldBe false
      }
    }
  }
}
