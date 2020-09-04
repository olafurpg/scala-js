/*
 * Scala.js (https://www.scala-js.org/)
 *
 * Copyright EPFL.
 *
 * Licensed under Apache License 2.0
 * (https://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package org.scalajs.testsuite.javalib.util

import scala.language.implicitConversions

import org.junit.Test
import org.junit.Assert._

import java.util.LinkedList

import scala.reflect.ClassTag

class LinkedListTest extends AbstractListTest {

  override def factory: LinkedListFactory = new LinkedListFactory

  @Test def add_and_remove_properly_in_head_and_last_positions(): Unit = {
    val ll = new LinkedList[Int]()

    ll.addLast(1)
    ll.removeFirst()
    ll.addLast(2)
    assertEquals(2, ll.peekFirst())

    ll.clear()

    ll.addFirst(1)
    ll.removeLast()
    ll.addFirst(2)
    assertEquals(2, ll.peekLast())
  }

  @Test def could_be_instantiated_with_a_prepopulated_Collection(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val ll = new LinkedList[Int](l)

    assertEquals(5, ll.size())

    for (i <- 0 until l.size())
      assertEquals(l(i), ll.poll())

    assertTrue(ll.isEmpty)
  }

  @Test def should_add_multiple_element_in_one_operation(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val ll = new LinkedList[Int]()

    assertEquals(0, ll.size())
    ll.addAll(l)
    assertEquals(5, ll.size())
    ll.add(6)
    assertEquals(6, ll.size())
  }

  @Test def `could_be_instantiated_with_a_prepopulated_Collection_-_LinkedListTest`(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val ll = new LinkedList[Int](l)

    assertEquals(5, ll.size())

    for (i <- 0 until l.size())
      assertEquals(l(i), ll.poll())

    assertTrue(ll.isEmpty)
  }

  @Test def should_retrieve_the_last_element(): Unit = {
    val llInt = new LinkedList[Int]()

    assertTrue(llInt.add(1000))
    assertTrue(llInt.add(10))
    assertEquals(10, llInt.pollLast())

    val llString = new LinkedList[String]()

    assertTrue(llString.add("pluto"))
    assertTrue(llString.add("pippo"))
    assertEquals(llString.pollLast(), "pippo")

    val llDouble = new LinkedList[Double]()

    assertTrue(llDouble.add(+10000.987))
    assertTrue(llDouble.add(-0.987))
    assertEquals(-0.987, llDouble.pollLast(), 0.0)
  }

  @Test def should_perform_as_a_stack_with_push_and_pop(): Unit = {
    val llInt = new LinkedList[Int]()

    llInt.push(1000)
    llInt.push(10)
    assertEquals(10, llInt.pop())
    assertEquals(1000, llInt.pop())
    assertTrue(llInt.isEmpty())

    val llString = new LinkedList[String]()

    llString.push("pluto")
    llString.push("pippo")
    assertEquals(llString.pop(), "pippo")
    assertEquals(llString.pop(), "pluto")
    assertTrue(llString.isEmpty())

    val llDouble = new LinkedList[Double]()

    llDouble.push(+10000.987)
    llDouble.push(-0.987)
    assertEquals(-0.987, llDouble.pop(), 0.0)
    assertEquals(+10000.987, llDouble.pop(), 0.0)
    assertTrue(llString.isEmpty())
  }

  @Test def should_poll_and_peek_elements(): Unit = {
    val pq = new LinkedList[String]()

    assertTrue(pq.add("one"))
    assertTrue(pq.add("two"))
    assertTrue(pq.add("three"))

    assertTrue(pq.peek.equals("one"))
    assertTrue(pq.poll.equals("one"))

    assertTrue(pq.peekFirst.equals("two"))
    assertTrue(pq.pollFirst.equals("two"))

    assertTrue(pq.peekLast.equals("three"))
    assertTrue(pq.pollLast.equals("three"))

    assertNull(pq.peekFirst)
    assertNull(pq.pollFirst)

    assertNull(pq.peekLast)
    assertNull(pq.pollLast)
  }

  @Test def should_remove_occurrences_of_provided_elements(): Unit = {
    val l = TrivialImmutableCollection("one", "two", "three", "two", "one")
    val ll = new LinkedList[String](l)

    assertTrue(ll.removeFirstOccurrence("one"))
    assertEquals(3, ll.indexOf("one"))
    assertTrue(ll.removeLastOccurrence("two"))
    assertEquals(0, ll.lastIndexOf("two"))
    assertTrue(ll.removeFirstOccurrence("one"))
    assertTrue(ll.removeLastOccurrence("two"))
    assertTrue(ll.removeFirstOccurrence("three"))
    assertFalse(ll.removeLastOccurrence("three"))
    assertTrue(ll.isEmpty)
  }

  @Test def should_iterate_over_elements_in_both_directions(): Unit = {
    val l = TrivialImmutableCollection("one", "two", "three")
    val ll = new LinkedList[String](l)

    val iter = ll.iterator()
    for (i <- 0 until l.size()) {
      assertTrue(iter.hasNext())
      assertEquals(iter.next(), l(i))
    }
    assertFalse(iter.hasNext())

    val diter = ll.descendingIterator()
    for (i <- (0 until l.size()).reverse) {
      assertTrue(diter.hasNext())
      assertEquals(diter.next(), l(i))
    }
    assertFalse(diter.hasNext())
  }
}

class LinkedListFactory extends AbstractListFactory {
  override def implementationName: String =
    "java.util.LinkedList"

  override def empty[E: ClassTag]: LinkedList[E] =
    new LinkedList[E]()
}
