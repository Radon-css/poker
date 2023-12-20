package de.htwg.poker.model.GameStateComponent.GameStateMockImpl

import de.htwg.poker.model.PlayersComponent.playersBaseImpl.Player
import de.htwg.poker.model.PlayersComponent.PlayerInterface
import de.htwg.poker.model.GameStateComponent.GameStateInterface
import de.htwg.poker.model.CardsComponent.CardInterface as Card

trait GameStateMockInterface extends GameStateInterface {
  override def getPlayers: List[PlayerInterface] = Nil
  override def getDeck: List[Card] = Nil
  override def getPlayerAtTurn: Int = 0
  override def getHighestBetSize: Int = 0
  override def getBoard: List[Card] = Nil
  override def getSmallBlind: Int = 0
  override def getBigBlind: Int = 0
  override def getPot: Int = 0
  override def getOriginalPlayers: List[PlayerInterface] = Nil
  override def getSmallBlindPointer: Int = 0
  override def createGame(
      playerNameList: List[String],
      smallBlind: Int,
      bigBlind: Int
  ): GameStateInterface = this
  override def bet(amount: Int): GameStateInterface =
    this
  override def allIn(): GameStateInterface =
    this
  override def fold(): GameStateInterface =
    this
  override def call(): GameStateInterface =
    this
  override def check(): GameStateInterface =
    this

}
