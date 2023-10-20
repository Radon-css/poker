import scala.util.Random

val Karten = Vector ("zwei", "Drei", "Vier", "Fuenf", "Sechs", "Sieben", "Acht", "Neun", "Zehn", "Bube", "Dame", "Koenig", "Ass")

case class Hand (){
    val random = new Random()
    val Karte1 = Karten(random.nextInt(Karten.length))
    val Karte2 = Karten(random.nextInt(Karten.length))
    override def toString(): String = {
        Karte1 + ", " + Karte2
    }
}

case class Dealer () {
    val Hand = new Hand
}

case class Spieler (val Spielername: String) {
    val Hand = new Hand
    val Name = Spielername
}

@main
def start = {
    val s = new Spieler ("Julian")
    val d = new Dealer
    println("Der Dealer zieht: " + d.Hand.toString)
    println(s.Name + " zieht: " + s.Hand.toString)
}

