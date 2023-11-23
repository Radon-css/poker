package de.htwg.poker.model

abstract class Blind(blindAmount: Int) {
    val amount: Int = blindAmount
}

class smallBlind(smallBlindAmount: Int) extends Blind(smallBlindAmount)
class bigBlind(bigBlindAmount: Int) extends Blind(bigBlindAmount)
