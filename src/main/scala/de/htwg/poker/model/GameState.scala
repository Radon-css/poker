package de.htwg.poker.model
import scala.math
import de.htwg.poker.util.Evaluator
import de.htwg.poker.util.TUIView

/* to depict the state of our game unambiguously, we need 10 different variables.
original Players: players participating in the game
players: players participating in the current round
deck: full card deck that has been shuffled for the current round
playerAtTurn: index of the player that is at turn
currentHighestBetSize: highest amount that has been betted by any player in the current state of the game
board: community cards that every player can see
pot: current size of the pot that you can win
smallBlind: amount you have to pay in order to participate in the next round
bigBlind: amount you have to pay in order to participate in the next round
smallBlindPointer: index of the player who has to pay the smallBlind in the current round.
 */

case class GameState(
    originalPlayers: List[Player],
    players: Option[List[Player]],
    deck: Option[List[Card]],
    playerAtTurn: Int = 0,
    currentHighestBetSize: Int = 0,
    board: List[Card] = Nil,
    pot: Int = 30,
    smallBlind: Int = 10,
    bigBlind: Int = 20,
    smallBlindPointer: Int = 0
) {

  def getPlayers: List[Player] = players.getOrElse(List.empty[Player])
  def getDeck: List[Card] = deck.getOrElse(List.empty[Card])
  def getPlayerAtTurn: Int = playerAtTurn
  def getHighestBetSize: Int = currentHighestBetSize
  def getBoard: List[Card] = board
  def getSmallBlind: Int = smallBlind
  def getBigBlind: Int = bigBlind
  def getPot: Int = pot
  def getOriginalPlayers: List[Player] = originalPlayers
  def getSmallBlindPointer = smallBlindPointer

  // see TUIView for toString implementation
  override def toString(): String = TUIView.update(this)

  /* in these following methods, we have to update the gamestate according to the certain action that has been taken.
    For example, if there was a bet, the players balance has to be reduced by the amount that has been betted and this
    amount goes into the pot.
    If there was a fold, the player has to be removed from the playerList for the current round
    If there was a call, the players balance has to be reduced by the amount of the highest previous bet in that round
    and this amount goes into the pot...

    After we are done with this we also need to call the getNextPlayer method so that its the next players turn.*/

  def bet(amount: Int): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).balance - amount,
      getPlayers(playerAtTurn).currentAmountBetted + amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      amount,
      getBoard,
      getPot + amount,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def allIn(): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      0,
      getPlayers(playerAtTurn).currentAmountBetted + getPlayers(
        playerAtTurn
      ).balance
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      getPlayers(playerAtTurn).balance,
      getBoard,
      getPot + getPlayers(playerAtTurn).balance,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def fold(): GameState = {
    val newPlayerList = getPlayers.patch(getPlayerAtTurn, Nil, 1)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayerWhenFold,
      getHighestBetSize,
      getBoard,
      getPot,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def call(): GameState = {
    val updatedPlayer = new Player(
      getPlayers(playerAtTurn).card1,
      getPlayers(playerAtTurn).card2,
      getPlayers(playerAtTurn).playername,
      getPlayers(playerAtTurn).balance - (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted),
      getPlayers(
        playerAtTurn
      ).currentAmountBetted + (getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted)
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    GameState(
      getOriginalPlayers,
      Some(newPlayerList),
      Some(getDeck),
      getNextPlayer,
      getHighestBetSize,
      getBoard,
      getPot + getHighestBetSize - getPlayers(
        playerAtTurn
      ).currentAmountBetted,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  def check(): GameState = {
    GameState(
      getOriginalPlayers,
      Some(getPlayers),
      Some(getDeck),
      getNextPlayer,
      getHighestBetSize,
      getBoard,
      getPot,
      getSmallBlind,
      getBigBlind,
      getSmallBlindPointer
    )
  }

  /* here we used a strategy pattern to update the community cards. If a handout of community cards is required,
    we can simply call the strategy method which then decides how many cards have to be revealed.
    If there were no community cards revealed, you reveal the first three cards.
    If there are three or four community cards revealed, you reveal another card.
    If there are five community cards revealed, you can start the next round.*/

  object updateBoard {
    var strategy: GameState =
      if (getBoard.size == 0) flop
      else if (getBoard.size == 3) turn
      else if (getBoard.size == 4) river
      else startRound

    def startRound: GameState = {
      val shuffledDeck = shuffleDeck

      val newPlayerList = getOriginalPlayers.zipWithIndex.map {
        case (playerName, index) =>
          new Player(
            shuffledDeck(index * 2),
            shuffledDeck(index * 2 + 1),
            getOriginalPlayers(index).playername,
            getOriginalPlayers(index).balance
          )
      }

      val smallBlindPlayer = new Player(
        newPlayerList(0).card1,
        newPlayerList(0).card2,
        newPlayerList(0).playername,
        newPlayerList(0).balance - smallBlind,
        newPlayerList(0).currentAmountBetted + smallBlind
      )

      val bigBlindPlayer = new Player(
        newPlayerList(1).card1,
        newPlayerList(1).card2,
        newPlayerList(1).playername,
        newPlayerList(1).balance - bigBlind,
        newPlayerList(1).currentAmountBetted + bigBlind
      )

      val newShuffledDeck = shuffledDeck.drop(newPlayerList.size * 2)

      val playerListWithBlinds0 =
        newPlayerList.updated(0, smallBlindPlayer)
      val playerListWithBlinds =
        playerListWithBlinds0.updated(1, bigBlindPlayer)

      GameState(
        getOriginalPlayers,
        Some(playerListWithBlinds),
        Some(newShuffledDeck),
        if (getOriginalPlayers.size < 3) 0 else 2,
        getBigBlind,
        Nil,
        getSmallBlind + getBigBlind,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
    def flop: GameState = {
      val newBoard = getDeck.take(3)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(3)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
    def turn: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
    def river: GameState = {
      val newBoard = getDeck.take(1)
      val newPlayerList =
        getPlayers.map(player => player.copy(currentAmountBetted = 0))
      GameState(
        getOriginalPlayers,
        Some(newPlayerList),
        Some(getDeck.drop(1)),
        0,
        0,
        getBoard ::: newBoard,
        getPot,
        getSmallBlind,
        getBigBlind,
        getSmallBlindPointer
      )
    }
  }

  // helper methods
  def getNextPlayer: Int = {
    if (getPlayers.length - 1 == getPlayerAtTurn) {
      return 0
    }
    return getPlayerAtTurn + 1
  }

  def getNextPlayerWhenFold: Int = {
    if (getPlayers.length - 1 == getPlayerAtTurn) {
      return 0
    }
    return getPlayerAtTurn
  }

  def getPreviousPlayer: Int = {
    if (getPlayerAtTurn == 0) {
      return getPlayers.length - 1
    }
    return getPlayerAtTurn - 1
  }

  def getCurrentHand: String = {
    val currHand = new Evaluator()
      .evaluate(
        List(
          getPlayers(getPlayerAtTurn).card1,
          getPlayers(getPlayerAtTurn).card2
        ),
        getBoard
      )
    currHand
  }
}
