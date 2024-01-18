package de.htwg.poker.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class ObserverSpec extends AnyWordSpec with Matchers with MockitoSugar {

  class TestObserver extends Observer {
    var updated: Boolean = false
    override def update: Unit = updated = true
  }

  "An Observable" when {
    "subscribing an observer" should {
      "add the observer to the subscribers list" in {
        val observable = new Observable
        val observer = new TestObserver

        observable.add(observer)

        observable.subscribers should contain(observer)
      }
    }

    "unsubscribing an observer" should {
      "remove the observer from the subscribers list" in {
        val observable = new Observable
        val observer = new TestObserver
        observable.add(observer)

        observable.remove(observer)

        observable.subscribers should not contain (observer)
      }
    }

    "notifying observers" should {
      "call the update method on all subscribed observers" in {
        val observable = new Observable
        val observer1 = mock[TestObserver]
        val observer2 = mock[TestObserver]
        observable.add(observer1)
        observable.add(observer2)

        observable.notifyObservers

        verify(observer1, times(1)).update
        verify(observer2, times(1)).update
      }
    }
  }
}
