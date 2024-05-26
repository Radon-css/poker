package de.htwg.poker.util

@main
def run: Unit = {
  print(WappFunctions.calculateCoins(10))
  print(WappFunctions.calculateCoins(20))
}
object WappFunctions {
  def calculateCoins(amount: Int): Seq[(String, String)] = {
    var amountLeft = amount
    var returnValue: Seq[(String, String)] = Nil
    val coinValues: List[Int] = List(1000, 500, 100, 50, 10)

    for (coinValue <- coinValues) {
      val amountOfCoins = math.floor(amountLeft / coinValue).toInt
      amountLeft = amountLeft - amountOfCoins * coinValue

      var tupleToAppend: (String, String) = null

      coinValue match {
        case 1000 => tupleToAppend = ("#FFFFFF", "#5F5F5F")
        case 500  => tupleToAppend = ("#FFFFFF", "#763968")
        case 100  => tupleToAppend = ("#FFFFFF", "#242424")
        case 50   => tupleToAppend = ("#FFFFFF", "#286343")
        case 10   => tupleToAppend = ("#FFFFFF", "#1E5FBF")
      }

      val tuplesToAppend = Seq.fill(amountOfCoins)(tupleToAppend)
      returnValue = returnValue ++ tuplesToAppend
    }
    returnValue
  }

  def getSuitStyle(id: Int): (String, String) = {
    var suit: String = null
    var color: String = null

    id match {
      case 1 =>
        suit = "bi-suit-club-fill"
        color = "black-text"
      case 2 =>
        suit = "bi-suit-spade-fill"
        color = "black-text"
      case 3 =>
        suit = "bi-suit-diamond-fill"
        color = "red-text"
      case 4 =>
        suit = "bi-suit-heart-fill"
        color = "red-text"
    }
    (suit, color)
  }
}
