package de.htwg.poker.util

trait Observer {
  def update: Unit
}

class Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) =
     println(s"[Observable] Adding observer: ${s.getClass.getSimpleName} (${System.identityHashCode(s)})")
     subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers = subscribers.foreach(o => o.update)
}
