package de.htwg.poker.model.PlayersComponent.playersBaseImpl
import de.htwg.poker.model.PlayersComponent.PlayersMockImpl.PlayerInterface
import de.htwg.poker.model.CardsComponent.CardInterface

case class Player(
    val card1: CardInterface,
    val card2: CardInterface,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0
) extends PlayerInterface {
  def balanceToString() = "(" + balance + "$)"

  def toHtml = {
    s"""<div class=\"flex flex-col items-center justify-center space-x-2\">
                <div class=\"rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white\">
                  <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-person-fill\" viewBox=\"0 0 16 16\">
                    <path d=\"M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6\"/>
                  </svg>
                </div>
                <div class=\"flex flex-col justify-center items-center text-slate-100\">
                    <p class=\"p-1\">${playername}</p>
                    <div class=\"rounded-full bg-slate-100 text-gray-400\">
                      <p class=\"p-1\">${balance}</p>
                    </div>
                    <div>${currentAmountBetted}</div>
                </div>
              </div>"""
  }
}
