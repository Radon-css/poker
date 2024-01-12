package de.htwg.poker.model.GameStateComponent

import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.PlayersComponent.PlayerInterface
import de.htwg.poker.model.CardsComponent.CardInterface as Card

trait GameStateInterface {
  def getPlayers: List[PlayerInterface]
  def getDeck: List[Card]
  def getPlayerAtTurn: Int
  def getHighestBetSize: Int
  def getBoard: List[Card]
  def getSmallBlind: Int
  def getBigBlind: Int
  def getPot: Int
  def getOriginalPlayers: List[PlayerInterface]
  def getSmallBlindPointer: Int

  def toString(): String

  def createGame(
      playerNameList: List[String],
      smallBlind: Int,
      bigBlind: Int
  ): GameStateInterface
  def bet(amount: Int): GameStateInterface
  def allIn(): GameStateInterface
  def fold(): GameStateInterface
  def call(): GameStateInterface
  def check(): GameStateInterface
  def updateBoard(): GameStateInterface

  def getNextPlayer: Int
  def getNextPlayerWhenFold: Int
  def getPreviousPlayer: Int
}