package de.htwg.poker.model

case class Player(
    val card1: Card,
    val card2: Card,
    val playername: String,
    val balance: Int = 1000,
    val currentAmountBetted: Int = 0,
    val folded: Boolean = false,
    val checkedThisRound: Boolean = false
) {
  def balanceToString = "(" + balance + "$)"

  def toHtml = {
    val opacityClass = if (folded) "opacity-50" else ""
    s"""<div class=\"flex flex-col items-center justify-center space-x-2\">
                <div class=\"rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5 ${opacityClass}\">
                  <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-person-fill\" viewBox=\"0 0 16 16\">
                    <path d=\"M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6\"/>
                  </svg>
                </div>
                    <p class=\"p-1 text-slate-100\">${playername}</p>
                    <div class=\"flex items-center justify-center rounded-full bg-slate-100 text-gray-700 w-14\">
                      <p class=\"p-1\">${balance}$$</p>
                    </div>
              </div>"""
  }

  def betSizeToHtml = {
    s""" <div class = \"rounded-full bg-gray-700/80 h-6 w-11\">
                      <div class = \"flex items-center justify-center py-0\">
                      <h1 class=\"text-gray-400\">${currentAmountBetted}$$</h1>
                    </div>
                    </div>
                    """
  }
}
