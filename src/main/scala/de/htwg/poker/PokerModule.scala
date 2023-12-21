package de.htwg.poker

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import de.htwg.poker.controller.ControllerComponent._
import de.htwg.poker.model.PlayersComponent._
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.GameStateComponent._
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState
import de.htwg.poker.model.CardsComponent._

class PokerModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ControllerInterface].to[ControllerBaseImpl.Controller]
    bind[CardInterface].to[CardsBaseImpl.Card]
    bind[GameStateInterface]
      .annotatedWithName("default")
      .toInstance(new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0))
    bind[PlayerInterface].to[PlayersBaseImpl.Player]
  }
}
