package de.htwg.poker.model

case class GameState(players: Option[List[Player]]) {
    
    def getPlayers: List[Player] = players.getOrElse(List.empty[Player])

    override def toString(): String = {
        val stringBuilder = new StringBuilder
        for(player <- getPlayers) {
            stringBuilder.append(player.toString())
        }
        stringBuilder.toString
    }
    }


