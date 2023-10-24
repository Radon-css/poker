package src;
import scala.util.Random

val Card = Vector ("Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ass")

case class Hand (){
    val random = new Random()
    val Card1 = Card(random.nextInt(Card.length))
    val Card2 = Card(random.nextInt(Card.length))
    override def toString(): String = {
        Card1 + ", " + Card2
    }
}

case class Dealer () {
    val Hand = new Hand
}

case class Player (val playername: String) {
    val Hand = new Hand
    val Name = playername
}

@main
def start = {
    val s = new Player ("Julian")
    val d = new Dealer
    println("Der Dealer zieht: " + d.Hand.toString)
    println(s.Name + " zieht: " + s.Hand.toString)
}

