package de.htwg.poker.util

import de.htwg.poker.Client
import de.htwg.poker.model.Card
import de.htwg.poker.model.GameState
import de.htwg.poker.model.Player
import de.htwg.poker.model.shuffleDeck
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UpdateBoard {

  def strategy(gameState: GameState): Future[GameState] = {
    if (gameState.board.size == 0) flop(gameState)
    else if (gameState.board.size == 3) turn(gameState)
    else if (gameState.board.size == 4) river(gameState)
    else startRound(gameState)
  }

  def startRound(gameState: GameState): Future[GameState] = {
    Client
      .calcWinner(
        gameState.players.getOrElse(List.empty[Player]).filter(player => !player.folded),
        gameState.board
      )
      .flatMap { winners =>
        val winnerNames = winners.map(_.playername)
        val winningAmount = gameState.pot / winners.size
        val shuffledDeck = shuffleDeck

        val newPlayerList = gameState.playersAndBalances.zipWithIndex.map { case (player, index) =>
          Player(
            shuffledDeck(index * 2),
            shuffledDeck(index * 2 + 1),
            player._1,
            player._2
          )
        }

        val smallBlindPlayer = newPlayerList(gameState.getNextSmallBlindPlayer).copy(
          balance = newPlayerList(gameState.getNextSmallBlindPlayer).balance - gameState.smallBlind,
          currentAmountBetted = newPlayerList(
            gameState.getNextSmallBlindPlayer
          ).currentAmountBetted + gameState.smallBlind
        )

        val bigBlindPlayer = newPlayerList(gameState.getNextBigBlindPlayer).copy(
          balance = newPlayerList(gameState.getNextBigBlindPlayer).balance - gameState.bigBlind,
          currentAmountBetted = newPlayerList(gameState.getNextBigBlindPlayer).currentAmountBetted + gameState.bigBlind
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

        val updatedPlayersAndBalances = gameState.playersAndBalances.map { player =>
          if (winnerNames.contains(player._1)) {
            if (player._1 == smallBlindPlayer.playername) {
              player.copy(player._1, player._2 - gameState.smallBlind + winningAmount)
            } else if (player._1 == bigBlindPlayer.playername) {
              player.copy(player._1, player._2 - gameState.bigBlind + winningAmount)
            } else {
              player.copy(player._1, player._2 + winningAmount)
            }
          } else if (player._1 == smallBlindPlayer.playername) {
            player.copy(player._1, player._2 - gameState.smallBlind)
          } else if (player._1 == bigBlindPlayer.playername) {
            player.copy(player._1, player._2 - gameState.bigBlind)
          } else {
            player
          }
        }

        Future.successful(
          gameState.copy(
            playersAndBalances = updatedPlayersAndBalances,
            players = Some(finalPlayerList),
            deck = Some(newShuffledDeck),
            playerAtTurn = gameState.getNewRoundPlayerAtTurn,
            currentHighestBetSize = gameState.bigBlind,
            board = Nil,
            pot = gameState.smallBlind + gameState.bigBlind,
            smallBlind = gameState.smallBlind,
            bigBlind = gameState.bigBlind,
            smallBlindPointer = gameState.getNextSmallBlindPlayer,
            newRoundStarted = true
          )
        )
      }
  }

  // Currying
  private def addCardsToBoard(
      cardsToAdd: Int
  )(gameState: GameState): GameState = {
    val newBoard = gameState.deck.getOrElse(List.empty).take(cardsToAdd)
    val newPlayerList =
      gameState.players.getOrElse(List.empty[Player]).map(_.copy(currentAmountBetted = 0))

    gameState.copy(
      players = Some(newPlayerList),
      deck = Some(gameState.deck.getOrElse(List.empty).drop(cardsToAdd)),
      playerAtTurn = gameState.getNextPlayer(gameState.smallBlindPointer),
      currentHighestBetSize = 0,
      board = gameState.board ::: newBoard
    )
  }

  // Partially Applied Function
  val flopFunction: GameState => GameState = addCardsToBoard(3)(_)
  val turnFunction: GameState => GameState = addCardsToBoard(1)(_)
  val riverFunction: GameState => GameState = addCardsToBoard(1)(_)

  // These methods now return Future[GameState] to be consistent with strategy()
  def flop(gameState: GameState): Future[GameState] = Future.successful(flopFunction(gameState))
  def turn(gameState: GameState): Future[GameState] = Future.successful(turnFunction(gameState))
  def river(gameState: GameState): Future[GameState] = Future.successful(riverFunction(gameState))

  // Closure
  def flopWithClosure(gameState: GameState): Future[GameState] = {
    def addCards(cardsToAdd: Int): GameState =
      addCardsToBoard(cardsToAdd)(gameState)
    Future.successful(addCards(3))
  }
}
