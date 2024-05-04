package de.htwg.poker.model
import scala.math
import de.htwg.poker.util.Evaluator
import de.htwg.poker.util.TUIView
import de.htwg.poker.util.HandInfo

/* to depict the state of our game unambiguously, we need 10 different values.
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
allInFlag: tells if there was an all-In in the current Round, which changes the logic of the Call action
 */

case class GameState(
    playersAndBalances: List[(String, Int)],
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
  def getPlayersAndBalances: List[(String, Int)] = playersAndBalances
  def getSmallBlindPointer = smallBlindPointer

  // see TUIView for toString implementation
  override def toString(): String = TUIView.update(this)

  /* in these following methods, we have to update the gamestate according to the certain action that has been taken.
    For example, if there was a bet, the players balance has to be reduced by the amount that has been betted and this
    amount goes into the pot.
    If there was a fold, the player has to be removed from the playerList for the current round
    If there was a call, the players balance has to be reduced by the amount of the highest previous bet in that round
    and this amount goes into the pot...
     After we are done with this we also need to call the getNextPlayer method so that its the next players turn.

    To achieve this, we first assign the new information for the GameState to a val and then pass it into the
    copy() method which creates a new GameState from the old one, changing only the modified variables.*/

  def bet(amount: Int): GameState = {
    val updatedPlayer = getCurrentPlayer.copy(
      balance = getCurrentPlayer.balance - amount,
      currentAmountBetted = getCurrentPlayer.currentAmountBetted + amount
    )
    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)
    val updatePlayer = getPlayers(getPlayerAtTurn).playername
    val playerToUpdate = getCurrentPlayer.playername
    val newPlayerBalance = getCurrentPlayer.balance - amount
    val updatedPlayersAndBalances = getPlayersAndBalances.map { player =>
      if (player._1 == playerToUpdate) {
        player.copy(player._1, newPlayerBalance)
      } else {
        player
      }
    }
    println(updatedPlayersAndBalances)
    copy(
      playersAndBalances = updatedPlayersAndBalances,
      players = Some(newPlayerList),
      playerAtTurn = getNextPlayer(getPlayerAtTurn),
      currentHighestBetSize = math.max(
        getHighestBetSize,
        amount
      ),
      pot = getPot + amount
    )
  }

  def fold: GameState = {
    val newPlayerList = getPlayers.patch(playerAtTurn, Nil, 1)
    copy(
      players = Some(newPlayerList),
      playerAtTurn = getNextPlayerWhenFold
    )
  }

  def call: GameState = {
    val callSize =
      getHighestBetSize - getCurrentPlayer.currentAmountBetted

    val updatedPlayer = getCurrentPlayer.copy(
      balance = getCurrentPlayer.balance - callSize,
      currentAmountBetted = getCurrentPlayer.currentAmountBetted + callSize
    )

    val newPlayerList = getPlayers.updated(playerAtTurn, updatedPlayer)
    val playerToUpdate = getCurrentPlayer.playername
    val newPlayerBalance = getCurrentPlayer.balance - callSize
    val updatedPlayersAndBalances = getPlayersAndBalances.map { player =>
      if (player._1 == playerToUpdate) {
        player.copy(player._1, newPlayerBalance)
      } else {
        player
      }
    }

    copy(
      playersAndBalances = updatedPlayersAndBalances,
      players = Some(newPlayerList),
      playerAtTurn = getNextPlayer(getPlayerAtTurn),
      currentHighestBetSize = getHighestBetSize,
      pot = getPot + callSize
    )
  }

  def check: GameState = copy(playerAtTurn = getNextPlayer(getPlayerAtTurn))

  def allIn: GameState = {
    val allInSize = getCurrentPlayer.balance

    val updatedPlayer = getCurrentPlayer.copy(
      balance = 0,
      currentAmountBetted = getCurrentPlayer.currentAmountBetted + allInSize
    )

    val newPlayerList = getPlayers.updated(getPlayerAtTurn, updatedPlayer)

    val playerToUpdate = getCurrentPlayer.playername
    val updatedPlayersAndBalances = getPlayersAndBalances.map { player =>
      if (player._1 == playerToUpdate) {
        player.copy(player._1, 0)
      } else {
        player
      }
    }

    copy(
      playersAndBalances = updatedPlayersAndBalances,
      players = Some(newPlayerList),
      playerAtTurn = getNextPlayer(getPlayerAtTurn),
      currentHighestBetSize = math.max(
        getHighestBetSize,
        allInSize
      ),
      pot = getPot + allInSize
    )
  }

  // construct an initial gameState to start the Game
  def createGame(
      playerNameList: List[String],
      smallBlind: Int,
      bigBlind: Int,
      smallBlindPlayerIndex: Int
  ): GameState = {

    val InitialPlayersAndBalances =
      playerNameList.map(playerName => (playerName, 1000))

    val shuffledDeck = shuffleDeck

    val playerList = playerNameList.zipWithIndex.map {
      case (playerName, index) =>
        new Player(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          playerName
        )
    }

    val newShuffledDeck = shuffledDeck.drop(playerList.size * 2)

    val smallBlindPlayer = playerList.head.copy(
      balance = playerList.head.balance - smallBlind,
      currentAmountBetted = playerList.head.currentAmountBetted + smallBlind
    )

    val bigBlindPlayer = playerList(1).copy(
      balance = playerList(1).balance - bigBlind,
      currentAmountBetted = playerList(1).currentAmountBetted + bigBlind
    )

    val playerListWithBlinds =
      playerList.updated(0, smallBlindPlayer).updated(1, bigBlindPlayer)

    GameState(
      InitialPlayersAndBalances,
      Some(playerListWithBlinds),
      Some(newShuffledDeck),
      if (playerList.size < 3) 0 else 2,
      bigBlind,
      Nil,
      smallBlind + bigBlind,
      smallBlind,
      bigBlind
    )
  }

  /* here we used a strategy pattern to update the community cards. If a handout of community cards is required,
    we can simply call the strategy method which then decides how many cards have to be revealed.
    If there were no community cards revealed, you reveal the first three cards.
    If there are three or four community cards revealed, you reveal another card.
    If there are five community cards revealed, you can start the next round.*/

  def startRound: GameState = {

    val winners = Evaluator.calcWinner(getPlayers, getBoard)

    val winnerNames = winners.map(winner => winner.playername)

    val winningAmount = getPot / winners.size

    val shuffledDeck = shuffleDeck

    val newPlayerList = getPlayersAndBalances.zipWithIndex.map {
      case (player, index) =>
        Player(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          player._1,
          player._2
        )
    }

    val smallBlindPlayer = newPlayerList(getNextSmallBlindPlayer).copy(
      balance = newPlayerList(getNextSmallBlindPlayer).balance - smallBlind,
      currentAmountBetted = newPlayerList(
        getNextSmallBlindPlayer
      ).currentAmountBetted + smallBlind
    )

    val bigBlindPlayer = newPlayerList(getNextBigBlindPlayer).copy(
      balance = newPlayerList(getNextBigBlindPlayer).balance - bigBlind,
      currentAmountBetted =
        newPlayerList(getNextBigBlindPlayer).currentAmountBetted + bigBlind
    )

    val newShuffledDeck = shuffledDeck.drop(newPlayerList.size * 2)

    val playerListWithBlinds =
      newPlayerList
        .updated(getNextSmallBlindPlayer, smallBlindPlayer)
        .updated(getNextBigBlindPlayer, bigBlindPlayer)

    val finalPlayerList: List[Player] = playerListWithBlinds.map { player =>
      if (winnerNames.contains(player.playername)) {
        player.copy(balance = player.balance + winningAmount)
      } else {
        player
      }
    }

    val updatedPlayersAndBalances = getPlayersAndBalances.map { player =>
      if (winnerNames.contains(player._1)) {
        player.copy(player._1, player._2 + winningAmount)
      } else {
        player
      }
    }
    println(updatedPlayersAndBalances)

    copy(
      playersAndBalances = updatedPlayersAndBalances,
      players = Some(finalPlayerList),
      deck = Some(newShuffledDeck),
      playerAtTurn = getNewRoundPlayerAtTurn,
      currentHighestBetSize = getBigBlind,
      board = Nil,
      pot = getSmallBlind + getBigBlind,
      smallBlind = getSmallBlind,
      bigBlind = getBigBlind,
      smallBlindPointer = getNextSmallBlindPlayer
    )
  }

  def revealCard(): GameState = {
    val newBoard = getDeck.take(1)
    val newPlayerList = getPlayers.map(_.copy(currentAmountBetted = 0))
    copy(
      players = Some(newPlayerList),
      deck = Some(getDeck.drop(1)),
      playerAtTurn = getSmallBlindPointer,
      currentHighestBetSize = 0,
      board = getBoard ::: newBoard
    )
  }

  // helper methods
  def getNextPlayer(current: Int): Int =
    if (getPlayers.length - 1 == current) 0 else current + 1

  def getNextPlayerWhenFold: Int =
    if (getPlayers.length - 1 == getPlayerAtTurn) 0 else getPlayerAtTurn

  def getCurrentPlayer: Player = getPlayers(playerAtTurn)

  def getPreviousPlayer: Int =
    if (getPlayerAtTurn == 0) getPlayers.length - 1 else getPlayerAtTurn - 1

  def getNextSmallBlindPlayer: Int =
    if (getPlayersAndBalances.length - 1 == getSmallBlindPointer) 0
    else getSmallBlindPointer + 1

  def getNextBigBlindPlayer: Int =
    if (getPlayersAndBalances.length - 1 == getNextSmallBlindPlayer) 0
    else getNextSmallBlindPlayer + 1

  def getNewRoundPlayerAtTurn: Int =
    if (getPlayersAndBalances.length - 1 == getNextBigBlindPlayer) 0
    else getNextBigBlindPlayer + 1

  def getCurrentHand: String = {
    HandInfo
      .evaluate(
        List(
          getPlayers(getPlayerAtTurn).card1,
          getPlayers(getPlayerAtTurn).card2
        ),
        getBoard
      )
  }
}
