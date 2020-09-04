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

import org.junit.Test
import org.junit.Assert._

import java.{util => ju}

import org.scalajs.testsuite.javalib.util.concurrent.ConcurrentSkipListSetFactory

import scala.reflect.ClassTag

trait NavigableSetTest extends SetTest {

  def factory: NavigableSetFactory

  @Test def `should_retrieve_ceiling(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val nsInt = factory.empty[Int]

    nsInt.addAll(lInt)

    assertEquals(1, nsInt.ceiling(-10))
    assertEquals(1, nsInt.ceiling(0))
    assertEquals(1, nsInt.ceiling(1))
    assertEquals(5, nsInt.ceiling(5))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val nsString = factory.empty[String]

    nsString.addAll(lString)

    assertEquals(nsString.ceiling("00000"), "a")
    assertEquals(nsString.ceiling("0"), "a")
    assertEquals(nsString.ceiling("a"), "a")
    assertEquals(nsString.ceiling("d"), "d")
    assertNull(nsString.ceiling("z"))
  }

  @Test def `should_retrieve_floor(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val nsInt = factory.empty[Int]

    nsInt.addAll(lInt)

    assertEquals(5, nsInt.floor(10))
    assertEquals(5, nsInt.floor(5))
    assertEquals(3, nsInt.floor(3))
    assertEquals(1, nsInt.floor(1))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val nsString = factory.empty[String]

    nsString.addAll(lString)

    assertEquals(nsString.floor("zzzzz"), "e")
    assertEquals(nsString.floor("d"), "d")
    assertEquals(nsString.floor("b"), "b")
    assertEquals(nsString.floor("a"), "a")
    assertNull(nsString.floor("0"))
  }

  @Test def `should_retrieve_higher(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val nsInt = factory.empty[Int]

    nsInt.addAll(lInt)

    assertEquals(5, nsInt.higher(4))
    assertEquals(4, nsInt.higher(3))
    assertEquals(2, nsInt.higher(1))
    assertEquals(1, nsInt.higher(-10))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val nsString = factory.empty[String]

    nsString.addAll(lString)

    assertNull(nsString.higher("zzzzz"))
    assertEquals(nsString.higher("d"), "e")
    assertEquals(nsString.higher("b"), "c")
    assertEquals(nsString.higher("a"), "b")
    assertEquals(nsString.higher("0"), "a")
  }

  @Test def `should_retrieve_lower(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val nsInt = factory.empty[Int]

    nsInt.addAll(lInt)

    assertEquals(4, nsInt.lower(5))
    assertEquals(3, nsInt.lower(4))
    assertEquals(2, nsInt.lower(3))
    assertEquals(5, nsInt.lower(10))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val nsString = factory.empty[String]

    nsString.addAll(lString)

    assertEquals(nsString.lower("zzzzz"), "e")
    assertEquals(nsString.lower("d"), "c")
    assertEquals(nsString.lower("b"), "a")
    assertNull(nsString.lower("a"))
    assertNull(nsString.lower("0"))
  }

  @Test def should_poll_first_and_last_elements(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val ns = factory.empty[Int]

    ns.addAll(lInt)

    assertTrue(ns.contains(1))
    assertEquals(1, ns.pollFirst())
    assertFalse(ns.contains(1))
    assertEquals(5, ns.pollLast())
    assertEquals(2, ns.pollFirst())
    assertEquals(4, ns.pollLast())
    assertEquals(3, ns.pollFirst())
    assertTrue(ns.isEmpty())
  }
}

trait NavigableSetFactory extends SetFactory {
  def empty[E: ClassTag]: ju.NavigableSet[E]
}
