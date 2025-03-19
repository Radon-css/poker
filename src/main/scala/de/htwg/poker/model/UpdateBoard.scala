package de.htwg.poker.model
import de.htwg.poker.model.GameState
import de.htwg.poker.util.Evaluator

/* here we used a strategy pattern to update the community cards. If a handout of community cards is required,
    we can simply call the strategy method which then decides how many cards have to be revealed.
    If there were no community cards revealed, you reveal the first three cards.
    If there are three or four community cards revealed, you reveal another card.
    If there are five community cards revealed, you can start the next round.*/

object UpdateBoard {

  def strategy(gameState: GameState): GameState = {
    if (gameState.getBoard.size == 0) flop(gameState)
    else if (gameState.getBoard.size == 3) turn(gameState)
    else if (gameState.getBoard.size == 4) river(gameState)
    else startRound(gameState)
  }

  def startRound(gameState: GameState): GameState = {

    val winners = Evaluator.calcWinner(
      gameState.getPlayers.filter(player => !player.folded),
      gameState.getBoard
    )

    val winnerNames = winners.map(winner => winner.playername)

    val winningAmount = gameState.getPot / winners.size

    val shuffledDeck = shuffleDeck

    val newPlayerList = gameState.getPlayersAndBalances.zipWithIndex.map {
      case (player, index) =>
        Player(
          shuffledDeck(index * 2),
          shuffledDeck(index * 2 + 1),
          player._1,
          player._2
        )
    }

    val smallBlindPlayer =
      newPlayerList(gameState.getNextSmallBlindPlayer).copy(
        balance = newPlayerList(
          gameState.getNextSmallBlindPlayer
        ).balance - gameState.getSmallBlind,
        currentAmountBetted = newPlayerList(
          gameState.getNextSmallBlindPlayer
        ).currentAmountBetted + gameState.getSmallBlind
      )

    val bigBlindPlayer = newPlayerList(gameState.getNextBigBlindPlayer).copy(
      balance = newPlayerList(
        gameState.getNextBigBlindPlayer
      ).balance - gameState.getBigBlind,
      currentAmountBetted = newPlayerList(
        gameState.getNextBigBlindPlayer
      ).currentAmountBetted + gameState.getBigBlind
    )

    val newShuffledDeck = shuffledDeck.drop(newPlayerList.size * 2)

    val playerListWithBlinds =
      newPlayerList
        .updated(gameState.getNextSmallBlindPlayer, smallBlindPlayer)
        .updated(gameState.getNextBigBlindPlayer, bigBlindPlayer)

    val finalPlayerList: List[Player] = playerListWithBlinds.map { player =>
      if (winnerNames.contains(player.playername)) {
        player.copy(balance = player.balance + winningAmount)
      } else {
        player
      }
    }

    val updatedPlayersAndBalances = gameState.getPlayersAndBalances.map {
      player =>
        if (winnerNames.contains(player._1)) {
          if (player._1 == smallBlindPlayer.playername) {
            player.copy(
              player._1,
              player._2 - gameState.getSmallBlind + winningAmount
            )
          } else if (player._1 == bigBlindPlayer.playername) {
            player.copy(
              player._1,
              player._2 - gameState.getBigBlind + winningAmount
            )
          } else {
            player.copy(player._1, player._2 + winningAmount)
          }
        } else if (player._1 == smallBlindPlayer.playername) {
          player.copy(player._1, player._2 - gameState.getSmallBlind)
        } else if (player._1 == bigBlindPlayer.playername) {
          player.copy(player._1, player._2 - gameState.getBigBlind)
        } else {
          player
        }
    }

    gameState.copy(
      playersAndBalances = updatedPlayersAndBalances,
      players = Some(finalPlayerList),
      deck = Some(newShuffledDeck),
      playerAtTurn = gameState.getNewRoundPlayerAtTurn,
      currentHighestBetSize = gameState.getBigBlind,
      board = Nil,
      pot = gameState.getSmallBlind + gameState.getBigBlind,
      smallBlind = gameState.getSmallBlind,
      bigBlind = gameState.getBigBlind,
      smallBlindPointer = gameState.getNextSmallBlindPlayer,
      newRoundStarted = true
    )
  }

  // Currying
  private def addCardsToBoard(
      cardsToAdd: Int
  )(gameState: GameState): GameState = {
    val newBoard = gameState.getDeck.take(cardsToAdd)
    val newPlayerList =
      gameState.getPlayers.map(_.copy(currentAmountBetted = 0))

    gameState.copy(
      players = Some(newPlayerList),
      deck = Some(gameState.getDeck.drop(cardsToAdd)),
      playerAtTurn = gameState.getNextPlayer(gameState.getSmallBlindPointer),
      currentHighestBetSize = 0,
      board = gameState.getBoard ::: newBoard
    )
  }

  // Partially Applied Function
  val flopFunction: GameState => GameState = addCardsToBoard(3)(_)
  val turnFunction: GameState => GameState = addCardsToBoard(1)(_)
  val riverFunction: GameState => GameState = addCardsToBoard(1)(_)

  def flop(gameState: GameState): GameState = flopFunction(gameState)
  def turn(gameState: GameState): GameState = turnFunction(gameState)
  def river(gameState: GameState): GameState = riverFunction(gameState)

// Closure
  def flopWithClosure(gameState: GameState): GameState = {

    def addCards(cardsToAdd: Int): GameState =
      addCardsToBoard(cardsToAdd)(gameState)
    addCards(3)
  }
}
