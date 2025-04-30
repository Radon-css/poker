package de.htwg.poker.util

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

      val tupleToAppend: Option[(String, String)] = coinValue match {
        case 1000 => Some(("#FFFFFF", "#5F5F5F"))
        case 500  => Some(("#FFFFFF", "#763968"))
        case 100  => Some(("#FFFFFF", "#242424"))
        case 50   => Some(("#FFFFFF", "#286343"))
        case 10   => Some(("#FFFFFF", "#1E5FBF"))
        case _    => None
      }

      val tuplesToAppend =
        tupleToAppend.map(Seq.fill(amountOfCoins)(_)).getOrElse(Seq.empty)
      returnValue = returnValue ++ tuplesToAppend
    }
    returnValue
  }

  def getSuitStyle(id: Int): (String, String) = {
    val (suit, color) = id match {
      case 1 => ("bi-suit-club-fill", "black-text")
      case 2 => ("bi-suit-spade-fill", "black-text")
      case 3 => ("bi-suit-diamond-fill", "red-text")
      case 4 => ("bi-suit-heart-fill", "red-text")
      case _ => ("unknown-suit", "unknown-color")
    }
    (suit, color)
  }
}
