package de.htwg.poker

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import de.htwg.poker.controller.ControllerComponent._
import de.htwg.poker.model.PlayersComponent._
import de.htwg.poker.model.PlayersComponent.PlayersBaseImpl.Player
import de.htwg.poker.model.GameStateComponent._
import de.htwg.poker.model.GameStateComponent.GameStateBaseImpl.GameState
import de.htwg.poker.model.CardsComponent.CardInterface
import de.htwg.poker.model.CardsComponent.Rank
import de.htwg.poker.model.CardsComponent.Suit
import de.htwg.poker.model.CardsComponent.CardsBaseImpl
import de.htwg.poker.model.CardsComponent.CardsAdvancedImpl
import de.htwg.poker.model.CardsComponent.CardsBaseImpl.Card
import de.htwg.poker.model.FileIOComponent.FileIOInterface
import de.htwg.poker.model.FileIOComponent.FileIOJsonImpl
import de.htwg.poker.model.FileIOComponent.FileIOXmlImpl


class PokerModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ControllerInterface].to[ControllerBaseImpl.Controller]
    bind[CardInterface].to[CardsAdvancedImpl.Card]
    bind[GameStateInterface].toInstance(
      new GameState(Nil, None, None, 0, 0, Nil, 0, 0, 0, 0)
    )
    bind[PlayerInterface].toInstance(
      new Player(
        new Card(Suit.Hearts, Rank.Ace),
        new Card(Suit.Hearts, Rank.Ace),
        " ",
        0,
        0
      )
    )
    bind[Rank].toInstance(Rank.Ace)
    bind[Suit].toInstance(Suit.Hearts)
    bind[FileIOInterface].to[FileIOXmlImpl.FileIO]
  }
}
