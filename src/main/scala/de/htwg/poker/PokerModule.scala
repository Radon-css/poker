package de.htwg.poker

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import de.htwg.poker.controller.ControllerComponent._
import de.htwg.poker.model.PlayersComponent._
import de.htwg.poker.model.GameStateComponent._
import de.htwg.poker.model.CardsComponent._

class SudokuModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ControllerInterface].to[ControllerBaseImpl.Controller]
    bind[CardInterface].to[CardsBaseImpl.Card]
    bind[GameStateInterface].to[GameStateBaseImpl.GameState]
    bind[PlayerInterface].to[PlayersBaseImpl.Player]
    bind[DeckInterface].to[CardsBaseImpl.Deck]
  }

}
