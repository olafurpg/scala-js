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

package org.scalajs.testsuite.javalib.util.concurrent

import java.util.concurrent.ConcurrentSkipListSet
import java.{util => ju}

import org.junit.Assert._
import org.junit.Assume._
import org.junit.Test

import org.scalajs.testsuite.javalib.util.NavigableSetFactory
import org.scalajs.testsuite.javalib.util.TrivialImmutableCollection

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.Platform._

import scala.reflect.ClassTag

// TODO extends AbstractSetTest with NavigableSetTest
class ConcurrentSkipListSetTest {

  def factory: ConcurrentSkipListSetFactory = new ConcurrentSkipListSetFactory

  @Test def should_store_and_remove_ordered_integers(): Unit = {
    val csls = factory.empty[Int]

    assertEquals(0, csls.size())
    assertTrue(csls.add(222))
    assertEquals(1, csls.size())
    assertTrue(csls.add(111))
    assertEquals(2, csls.size())
    assertEquals(111, csls.first)
    assertTrue(csls.remove(111))

    assertEquals(1, csls.size())
    assertEquals(222, csls.first)

    assertTrue(csls.remove(222))
    assertEquals(0, csls.size())
    assertTrue(csls.isEmpty)
    assertFalse(csls.remove(333))
    expectThrows(classOf[NoSuchElementException], csls.first)
  }

  @Test def should_store_and_remove_ordered_strings(): Unit = {
    val csls = factory.empty[String]

    assertEquals(0, csls.size())
    assertTrue(csls.add("222"))
    assertEquals(1, csls.size())
    assertTrue(csls.add("111"))
    assertEquals(2, csls.size())
    assertEquals(csls.first, "111")
    assertTrue(csls.remove("111"))

    assertEquals(1, csls.size())
    assertEquals(csls.first, "222")

    assertTrue(csls.remove("222"))
    assertEquals(0, csls.size())
    assertFalse(csls.remove("333"))
    assertTrue(csls.isEmpty)
  }

  @Test def should_store_objects_with_custom_comparables(): Unit = {
    case class Rect(x: Int, y: Int)

    val areaComp = new ju.Comparator[Rect] {
      def compare(a: Rect, b: Rect): Int = (a.x*a.y) - (b.x*b.y)
    }

    val csls = new ConcurrentSkipListSet[Rect](areaComp)

    assertTrue(csls.add(Rect(1,2)))
    assertTrue(csls.add(Rect(2,3)))
    assertTrue(csls.add(Rect(1,3)))

    val first = csls.first()
    assertEquals(1, first.x)
    assertEquals(2, first.y)

    assertTrue(csls.remove(first))
    assertFalse(csls.remove(first))

    val second = csls.first()
    assertEquals(1, second.x)
    assertEquals(3, second.y)

    assertTrue(csls.remove(second))

    val third = csls.first()
    assertEquals(2, third.x)
    assertEquals(3, third.y)

    assertTrue(csls.remove(third))

    assertTrue(csls.isEmpty)
  }

  @Test def should_store_ordered_Double_even_in_corner_cases(): Unit = {
    val csls = factory.empty[Double]

    assertTrue(csls.add(1.0))
    assertTrue(csls.add(+0.0))
    assertTrue(csls.add(-0.0))
    assertTrue(csls.add(Double.NaN))

    assertTrue(csls.first.equals(-0.0))

    assertTrue(csls.remove(-0.0))

    assertTrue(csls.first.equals(+0.0))

    assertTrue(csls.remove(+0.0))

    assertTrue(csls.first.equals(1.0))

    assertTrue(csls.remove(1.0))

    assertTrue(csls.first.isNaN)

    assertTrue(csls.remove(Double.NaN))

    assertTrue(csls.isEmpty)
  }

  @Test def could_be_instantiated_with_a_prepopulated_Collection(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val csls = factory.newFrom(l)

    assertEquals(5, csls.size())
    for (i <- 1 to 5) {
      assertEquals(i, csls.first)
      assertTrue(csls.remove(i))
    }
    assertTrue(csls.isEmpty)
  }

  @Test def should_be_cleared_in_a_single_operation(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val csls = factory.newFrom(l)

    assertEquals(5, csls.size())
    csls.clear()
    assertEquals(0, csls.size())
  }

  @Test def should_add_multiple_elemnt_in_one_operation(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val csls = factory.empty[Int]

    assertEquals(0, csls.size())
    csls.addAll(l)
    assertEquals(5, csls.size())
    csls.add(6)
    assertEquals(6, csls.size())
  }

  @Test def should_check_contained_values_even_in_double_corner_cases(): Unit = {
    val csls = factory.empty[Double]

    assertTrue(csls.add(11111.0))
    assertEquals(1, csls.size())
    assertTrue(csls.contains(11111.0))
    assertEquals(11111.0, csls.iterator.next(), 0.0)

    assertTrue(csls.add(Double.NaN))
    assertEquals(2, csls.size())
    assertTrue(csls.contains(Double.NaN))
    assertFalse(csls.contains(+0.0))
    assertFalse(csls.contains(-0.0))

    assertTrue(csls.remove(Double.NaN))
    assertTrue(csls.add(+0.0))
    assertEquals(2, csls.size())
    assertFalse(csls.contains(Double.NaN))
    assertTrue(csls.contains(+0.0))
    assertFalse(csls.contains(-0.0))

    assertTrue(csls.remove(+0.0))
    assertTrue(csls.add(-0.0))
    assertEquals(2, csls.size())
    assertFalse(csls.contains(Double.NaN))
    assertFalse(csls.contains(+0.0))
    assertTrue(csls.contains(-0.0))

    assertTrue(csls.add(+0.0))
    assertTrue(csls.add(Double.NaN))
    assertTrue(csls.contains(Double.NaN))
    assertTrue(csls.contains(+0.0))
    assertTrue(csls.contains(-0.0))
  }

  @Test def `should_retrieve_the_first(ordered)_element`(): Unit = {
    val cslsInt = factory.empty[Int]

    assertTrue(cslsInt.add(1000))
    assertTrue(cslsInt.add(10))
    assertEquals(10, cslsInt.first)

    val cslsString = factory.empty[String]

    assertTrue(cslsString.add("pluto"))
    assertTrue(cslsString.add("pippo"))
    assertEquals(cslsString.first, "pippo")

    val cslsDouble = factory.empty[Double]

    assertTrue(cslsDouble.add(+10000.987))
    assertTrue(cslsDouble.add(-0.987))
    assertEquals(-0.987, cslsDouble.first, 0.0)
  }

  @Test def `should_retrieve_the_last(ordered)_element`(): Unit = {
    val cslsInt = factory.empty[Int]

    assertTrue(cslsInt.add(1000))
    assertTrue(cslsInt.add(10))
    assertEquals(1000, cslsInt.last)

    val cslsString = factory.empty[String]

    assertTrue(cslsString.add("pluto"))
    assertTrue(cslsString.add("pippo"))
    assertEquals(cslsString.last, "pluto")

    val cslsDouble = factory.empty[Double]

    assertTrue(cslsDouble.add(+10000.987))
    assertTrue(cslsDouble.add(-0.987))
    assertEquals(10000.987, cslsDouble.last, 0.0)
  }

  @Test def `should_retrieve_ceiling(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val cslsInt = new ConcurrentSkipListSet[Int](lInt)

    assertEquals(1, cslsInt.ceiling(-10))
    assertEquals(1, cslsInt.ceiling(0))
    assertEquals(1, cslsInt.ceiling(1))
    assertEquals(5, cslsInt.ceiling(5))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val cslsString = new ConcurrentSkipListSet[String](lString)

    assertEquals(cslsString.ceiling("00000"), "a")
    assertEquals(cslsString.ceiling("0"), "a")
    assertEquals(cslsString.ceiling("a"), "a")
    assertEquals(cslsString.ceiling("d"), "d")
    assertNull(cslsString.ceiling("z"))
  }

  @Test def `should_retrieve_floor(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val cslsInt = new ConcurrentSkipListSet[Int](lInt)

    assertEquals(5, cslsInt.floor(10))
    assertEquals(5, cslsInt.floor(5))
    assertEquals(3, cslsInt.floor(3))
    assertEquals(1, cslsInt.floor(1))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val cslsString = new ConcurrentSkipListSet[String](lString)

    assertEquals(cslsString.floor("zzzzz"), "e")
    assertEquals(cslsString.floor("d"), "d")
    assertEquals(cslsString.floor("b"), "b")
    assertEquals(cslsString.floor("a"), "a")
    assertNull(cslsString.floor("0"))
  }

  @Test def `should_retrieve_higher(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val cslsInt = new ConcurrentSkipListSet[Int](lInt)

    assertEquals(5, cslsInt.higher(4))
    assertEquals(4, cslsInt.higher(3))
    assertEquals(2, cslsInt.higher(1))
    assertEquals(1, cslsInt.higher(-10))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val cslsString = new ConcurrentSkipListSet[String](lString)

    assertNull(cslsString.higher("zzzzz"))
    assertEquals(cslsString.higher("d"), "e")
    assertEquals(cslsString.higher("b"), "c")
    assertEquals(cslsString.higher("a"), "b")
    assertEquals(cslsString.higher("0"), "a")
  }

  @Test def `should_retrieve_lower(ordered)_elements`(): Unit = {
    val lInt = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val cslsInt = new ConcurrentSkipListSet[Int](lInt)

    assertEquals(4, cslsInt.lower(5))
    assertEquals(3, cslsInt.lower(4))
    assertEquals(2, cslsInt.lower(3))
    assertEquals(5, cslsInt.lower(10))

    val lString = TrivialImmutableCollection("a", "e", "b", "c", "d")
    val cslsString = new ConcurrentSkipListSet[String](lString)

    assertEquals(cslsString.lower("zzzzz"), "e")
    assertEquals(cslsString.lower("d"), "c")
    assertEquals(cslsString.lower("b"), "a")
    assertNull(cslsString.lower("a"))
    assertNull(cslsString.lower("0"))
  }

  @Test def should_poll_first_and_last_elements(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val csls = new ConcurrentSkipListSet[Int](l)

    assertTrue(csls.contains(1))
    assertEquals(1, csls.pollFirst())
    assertFalse(csls.contains(1))
    assertEquals(5, csls.pollLast())
    assertEquals(2, csls.pollFirst())
    assertEquals(4, csls.pollLast())
    assertEquals(3, csls.pollFirst())
    assertTrue(csls.isEmpty())
  }

  @Test def should_get_partial_views_that_are_backed_on_the_original_list(): Unit = {
    val l = TrivialImmutableCollection(1, 5, 2, 3, 4)
    val csls = new ConcurrentSkipListSet[Int](l)

    val hs1 = csls.headSet(3)
    val l1 = TrivialImmutableCollection(1, 2)
    assertTrue(hs1.containsAll(l1))
    assertTrue(hs1.removeAll(l1))
    assertTrue(hs1.isEmpty)
    assertEquals(3, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(3, 4, 5)))

    csls.addAll(l)

    val hs2 = csls.headSet(3, true)
    val l2 = TrivialImmutableCollection(1, 2, 3)
    assertTrue(hs2.containsAll(l2))
    assertTrue(hs2.removeAll(l2))
    assertTrue(hs2.isEmpty)
    assertEquals(2, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(4, 5)))

    csls.addAll(l)

    val ts1 = csls.tailSet(3)
    val l3 = TrivialImmutableCollection(3, 4, 5)
    assertTrue(ts1.containsAll(l3))
    assertTrue(ts1.removeAll(l3))
    assertTrue(ts1.isEmpty)
    assertEquals(2, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(1, 2)))

    csls.addAll(l)

    val ts2 = csls.tailSet(3, false)
    val l4 = TrivialImmutableCollection(4, 5)
    assertTrue(ts2.containsAll(l4))
    assertTrue(ts2.removeAll(l4))
    assertTrue(ts2.isEmpty)
    assertEquals(3, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(1, 2, 3)))

    csls.addAll(l)

    val ss1 = csls.subSet(2, true, 3, true)
    val l5 = TrivialImmutableCollection(2, 3)
    assertTrue(ss1.containsAll(l5))
    assertTrue(ss1.removeAll(l5))
    assertTrue(ss1.isEmpty)
    assertEquals(3, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(1, 4, 5)))

    csls.addAll(l)

    val ss2 = csls.subSet(1, false, 4, false)
    assertTrue(ss2.containsAll(l5))
    assertTrue(ss2.removeAll(l5))
    assertTrue(ss2.isEmpty)
    assertEquals(3, csls.size)
    assertTrue(csls.containsAll(TrivialImmutableCollection(1, 4, 5)))
  }

  @Test def should_throw_exception_on_non_comparable_objects(): Unit = {
    assumeTrue("Assumed compliant asInstanceOf", hasCompliantAsInstanceOfs)
    assumeFalse("Ignored on JVM due to possible race condition", executingInJVM)
    // Behaviour based on JDK8 modulo (improbable) race conditions.

    class TestObj(num: Int)

    val csls = new ConcurrentSkipListSet[TestObj]()

    assertEquals(0, csls.size())
    expectThrows(classOf[ClassCastException], {
      // Throw either when the first or second element is added
      csls.add(new TestObj(111))
      csls.add(new TestObj(222))
    })
    assertNull(csls.comparator)
  }
}

class ConcurrentSkipListSetFactory extends NavigableSetFactory {
  def implementationName: String =
    "java.util.concurrent.ConcurrentSkipListSet"

  def empty[E: ClassTag]: ju.concurrent.ConcurrentSkipListSet[E] =
    new ConcurrentSkipListSet[E]

  def newFrom[E](coll: ju.Collection[E]): ju.concurrent.ConcurrentSkipListSet[E] =
    new ConcurrentSkipListSet[E](coll)

  override def allowsNullElement: Boolean = false
}
