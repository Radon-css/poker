package de.htwg.poker.model

case class GameState(players: List[Player]) {
    
    def getPlayers: List[Player] = players

    override def toString(): String = getPlayers.toString()
    }


