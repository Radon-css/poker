package de.htwg.poker.util

import scala.io.Source
import java.io.PrintWriter

object PokerHashMultiplier {
  // Funktion zum Berechnen der Primzahlen für die Charaktere
  def charToPrime(char: Char): Int = char match {
    case '2' => 2
    case '3' => 3
    case '4' => 5
    case '5' => 7
    case '6' => 11
    case '7' => 13
    case '8' => 17
    case '9' => 19
    case 'T' => 23
    case 'J' => 29
    case 'Q' => 31
    case 'K' => 37
    case 'A' => 41
    case _ =>
      throw new IllegalArgumentException("Ungültiger Charakter: " + char)
  }

  // Funktion zum Multiplizieren der Primzahlen für jeden Eintrag
  def calculateProduct(entry: String): Int = {
    entry.map(charToPrime).product
  }

  def main(args: Array[String]): Unit = {
    // Pfad zur "PokerHash"-Textdatei
    val filePath = "src/main/scala/de/htwg/poker/util/notflush.txt"
    val fileOutPath = "src/main/scala/de/htwg/poker/util/results.txt"

    val writer = new PrintWriter(fileOutPath)

    // Einlesen der zweiten Spalte der Textdatei und Berechnung der Produkte
    val result = Source.fromFile(filePath).getLines().foreach { line =>
      val entry =
        line.split(",")(1) // Zweite Spalte extrahieren
      writer.println(calculateProduct(entry))
    }

    writer.close()
  }
}
