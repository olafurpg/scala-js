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

import org.scalajs.testsuite.utils.AssertThrows._

import java.{util => ju}

import scala.reflect.ClassTag

trait ListTest extends CollectionTest {

  def factory: ListFactory

  @Test def shouldStoreStrings_List(): Unit = {
    val lst = factory.empty[String]

    assertEquals(0, lst.size())
    lst.add("one")
    assertEquals(1, lst.size())
    assertEquals(lst.get(0), "one")
    lst.add("two")
    assertEquals(2, lst.size())
    assertEquals(lst.get(0), "one")
    assertEquals(lst.get(1), "two")

    expectThrows(classOf[IndexOutOfBoundsException], lst.get(-1))
    expectThrows(classOf[IndexOutOfBoundsException], lst.get(lst.size))
  }

  @Test def shouldStoreIntegers_List(): Unit = {
    val lst = factory.empty[Int]

    lst.add(1)
    assertEquals(1, lst.size())
    assertEquals(1, lst.get(0))
    lst.add(2)
    assertEquals(2, lst.size())
    assertEquals(1, lst.get(0))
    assertEquals(2, lst.get(1))

    expectThrows(classOf[IndexOutOfBoundsException], lst.get(-1))
    expectThrows(classOf[IndexOutOfBoundsException], lst.get(lst.size))
  }

  @Test def shouldStoreDoubles_List(): Unit = {
    val lst = factory.empty[Double]

    lst.add(1.234)
    assertEquals(1, lst.size())
    assertEquals(1.234, lst.get(0), 0.0)
    lst.add(2.345)
    assertEquals(2, lst.size())
    assertEquals(1.234, lst.get(0), 0.0)
    assertEquals(2.345, lst.get(1), 0.0)
    lst.add(Double.NaN)
    lst.add(+0.0)
    lst.add(-0.0)
    assertEquals(5, lst.size())
    assertEquals(1.234, lst.get(0), 0.0)
    assertEquals(2.345, lst.get(1), 0.0)
    assertTrue(lst.get(2).isNaN)
    assertTrue(lst.get(3).equals(+0.0))
    assertTrue(lst.get(4).equals(-0.0))

    expectThrows(classOf[IndexOutOfBoundsException], lst.get(-1))
    expectThrows(classOf[IndexOutOfBoundsException], lst.get(lst.size))
  }

  @Test def shouldStoreCustomObjects_List(): Unit = {
    case class TestObj(num: Int)

    val lst = factory.empty[TestObj]

    lst.add(TestObj(100))
    assertEquals(1, lst.size())
    assertEquals(lst.get(0), TestObj(100))

    expectThrows(classOf[IndexOutOfBoundsException], lst.get(-1))
    expectThrows(classOf[IndexOutOfBoundsException], lst.get(lst.size))
  }

  @Test def shouldRemoveStoredElements_List(): Unit = {
    val lst = factory.empty[String]

    lst.add("one")
    lst.add("two")
    lst.add("three")

    assertFalse(lst.remove("four"))
    assertEquals(3, lst.size())
    assertTrue(lst.remove("two"))
    assertEquals(2, lst.size())
    assertEquals(lst.remove(0), "one")
    assertEquals(1, lst.size())
    assertEquals(lst.get(0), "three")

    expectThrows(classOf[IndexOutOfBoundsException], lst.remove(-1))
    expectThrows(classOf[IndexOutOfBoundsException], lst.remove(lst.size))
  }

  @Test def shouldRemoveStoredElementsOnDoubleCornerCases_List(): Unit = {
    val al = factory.empty[Double]

    al.add(1.234)
    al.add(2.345)
    al.add(Double.NaN)
    al.add(+0.0)
    al.add(-0.0)

    // al == ArrayList(1.234, 2.345, NaN, +0.0, -0.0)
    assertTrue(al.remove(Double.NaN))
    // al == ArrayList(1.234, 2.345, +0.0, -0.0)
    assertEquals(4, al.size())
    assertTrue(al.remove(2.345))
    // al == ArrayList(1.234, +0.0, -0.0)
    assertEquals(3, al.size())
    assertEquals(1.234, al.remove(0), 0.0)
    // al == ArrayList(+0.0, -0.0)
    assertEquals(2, al.size())
    assertTrue(al.remove(-0.0))
    // al == ArrayList(NaN, +0.0)
    assertEquals(1, al.size())

    al.clear()

    assertTrue(al.isEmpty)
  }

  @Test def shouldBeClearedWithOneOperation_List(): Unit = {
    val al = factory.empty[String]

    al.add("one")
    al.add("two")
    assertEquals(2, al.size)
    al.clear()
    assertEquals(0, al.size)
  }

  @Test def shouldCheckContainedPresence_List(): Unit = {
    val al = factory.empty[String]

    al.add("one")
    assertTrue(al.contains("one"))
    assertFalse(al.contains("two"))
    assertFalse(al.contains(null))
  }

  @Test def shouldCheckContainedPresenceForDoubleCornerCases_List(): Unit = {
    val al = factory.empty[Double]

    al.add(-0.0)
    assertTrue(al.contains(-0.0))
    assertFalse(al.contains(+0.0))

    al.clear()

    al.add(+0.0)
    assertFalse(al.contains(-0.0))
    assertTrue(al.contains(+0.0))
  }

  @Test def shouldGiveAProperSetOperation(): Unit = {
    val al = factory.empty[String]
    al.add("one")
    al.add("two")
    al.add("three")

    al.set(1, "four")
    assertEquals(al.get(0), "one")
    assertEquals(al.get(1), "four")
    assertEquals(al.get(2), "three")

    expectThrows(classOf[IndexOutOfBoundsException], al.set(-1, ""))
    expectThrows(classOf[IndexOutOfBoundsException], al.set(al.size, ""))
  }

  @Test def shouldGiveProperIteratorOverElements_List(): Unit = {
    val al = factory.empty[String]
    al.add("one")
    al.add("two")
    al.add("three")

    val elements = al.iterator
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "one")
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "two")
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "three")
    assertFalse(elements.hasNext)
  }

  @Test def shouldGiveProperListIteratorOverElements(): Unit = {
    val lst = factory.empty[String]
    lst.add("one")
    lst.add("two")
    lst.add("three")

    val elements = lst.listIterator
    assertFalse(elements.hasPrevious)
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "one")
    assertTrue(elements.hasPrevious)
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "two")
    assertTrue(elements.hasPrevious)
    assertTrue(elements.hasNext)
    assertEquals(elements.next(), "three")
    assertTrue(elements.hasPrevious)
    assertFalse(elements.hasNext)
    assertEquals(elements.previous(), "three")
    assertEquals(elements.previous(), "two")
    assertEquals(elements.previous(), "one")
  }

  @Test def shouldAddElementsAtAGivenIndex(): Unit = {
    val al = factory.empty[String]
    al.add(0, "one") // ["one"]
    al.add(0, "two") // ["two", "one"]
    al.add(1, "three") // ["two", "three", "one"]

    assertEquals(al.get(0), "two")
    assertEquals(al.get(1), "three")
    assertEquals(al.get(2), "one")

    expectThrows(classOf[IndexOutOfBoundsException], al.add(-1, ""))
    expectThrows(classOf[IndexOutOfBoundsException], al.add(al.size + 1, ""))
  }

  @Test def shouldGiveTheFirstIndexOfAnElement(): Unit = {
    val al = factory.empty[String]
    al.add("one")
    al.add("two")
    al.add("three")
    al.add("one")
    al.add("two")
    al.add("three")

    assertEquals(0, al.indexOf("one"))
    assertEquals(1, al.indexOf("two"))
    assertEquals(2, al.indexOf("three"))
    assertEquals(-1, al.indexOf("four"))
  }

  @Test def shouldGiveTheLastIndexOfAnElement(): Unit = {
    val al = factory.empty[String]
    al.add("one")
    al.add("two")
    al.add("three")
    al.add("one")
    al.add("two")
    al.add("three")

    assertEquals(3, al.lastIndexOf("one"))
    assertEquals(4, al.lastIndexOf("two"))
    assertEquals(5, al.lastIndexOf("three"))
    assertEquals(-1, al.lastIndexOf("four"))
  }

  @Test def shouldGiveTheFirstOrLastIndexOfAnElementForDoubleCornerCases(): Unit = {
    val al = factory.empty[Double]

    al.add(-0.0)
    al.add(+0.0)
    al.add(Double.NaN)
    al.add(+0.0)
    al.add(-0.0)
    al.add(Double.NaN)

    assertEquals(0, al.indexOf(-0.0))
    assertEquals(1, al.indexOf(+0.0))
    assertEquals(2, al.indexOf(Double.NaN))

    assertEquals(3, al.lastIndexOf(+0.0))
    assertEquals(4, al.lastIndexOf(-0.0))
    assertEquals(5, al.lastIndexOf(Double.NaN))
  }

  @Test def shouldGiveASublistBackedUpByTheOriginalList(): Unit = {
    def testListIterator(list: ju.List[String], expected: Seq[String]): Unit = {
      val iter = list.listIterator
      for (elem <- expected) {
        assertTrue(iter.hasNext)
        assertEquals(iter.next(), elem)
      }
      assertFalse(iter.hasNext)

      for (elem <- expected.reverse) {
        assertTrue(iter.hasPrevious)
        assertEquals(iter.previous(), elem)
      }
      assertFalse(iter.hasPrevious)
    }

    val al = factory.empty[String]

    al.add("one")
    al.add("two")
    al.add("three")
    al.add("four")
    al.add("five")
    al.add("six")

    testListIterator(al, Seq("one", "two", "three", "four", "five", "six"))

    val al0 = al.subList(0, al.size)
    assertEquals(6, al0.size)
    assertEquals(al.size, al0.size)
    for (i <- 0 until al.size)
      assertEquals(al0.get(i), al.get(i))
    al0.set(3, "zero")
    assertEquals(al0.get(3), "zero")
    for (i <- 0 until al.size)
      assertEquals(al0.get(i), al.get(i))
    testListIterator(al, Seq("one", "two", "three", "zero", "five", "six"))
    testListIterator(al0, Seq("one", "two", "three", "zero", "five", "six"))

    val al1 = al.subList(2, 5)
    assertEquals(3, al1.size)
    for (i <- 0 until 3)
      assertEquals(al1.get(i), al.get(2 + i))
    al1.set(0, "nine")
    assertEquals(al1.get(0), "nine")
    for (i <- 0 until 3) {
      assertEquals(al1.get(i), al.get(2 + i))
      if (!al.isInstanceOf[ju.concurrent.CopyOnWriteArrayList[_]]) {
        /* For CopyOnWriteArrayList, accessing al0 after al has been modified
         * through al1 (i.e., through anything bug al0 itself) is undefined
         * behavior.
         */
        assertEquals(al1.get(i), al0.get(2 + i))
      }
    }
    assertEquals(al1.get(0), "nine")
    assertEquals(al1.get(1), "zero")
    assertEquals(al1.get(2), "five")

    testListIterator(al, Seq("one", "two", "nine", "zero", "five", "six"))
    testListIterator(al1, Seq("nine", "zero", "five"))

    al1.clear()

    assertEquals(al.get(0), "one")
    assertEquals(al.get(1), "two")
    assertEquals(al.get(2), "six")
    assertEquals(3, al.size)
    assertEquals(0, al1.size)
    testListIterator(al, Seq("one", "two", "six"))
    testListIterator(al1, Seq.empty)

    assertTrue(al1.add("ten"))
    testListIterator(al, Seq("one", "two", "ten", "six"))
    testListIterator(al1, Seq("ten"))

    if (factory.allowsMutationThroughIterator) {
      val iter = al1.listIterator
      iter.add("three")
      iter.next()
      iter.add("zero")

      testListIterator(al, Seq("one", "two", "three", "ten", "zero", "six"))
      testListIterator(al1, Seq("three", "ten", "zero"))
    }
  }

  @Test def shouldIterateAndModifyElementsWithAListIteratorIfAllowed(): Unit = {
    if (factory.allowsMutationThroughIterator) {
      val s = Seq("one", "two", "three")
      val ll = factory.empty[String]

      for (e <- s)
        ll.add(e)

      val iter = ll.listIterator(1)

      assertTrue(iter.hasNext())
      assertTrue(iter.hasPrevious())

      assertEquals(iter.previous(), "one")

      assertTrue(iter.hasNext())
      assertFalse(iter.hasPrevious())

      assertEquals(iter.next(), "one")

      assertEquals(iter.next(), "two")
      assertEquals(iter.next(), "three")

      assertFalse(iter.hasNext())
      assertTrue(iter.hasPrevious())

      iter.add("four")

      assertFalse(iter.hasNext())
      assertTrue(iter.hasPrevious())

      assertEquals(iter.previous(), "four")

      iter.remove()

      assertFalse(iter.hasNext())
      assertTrue(iter.hasPrevious())
      assertEquals(iter.previous(), "three")
      iter.set("THREE")
      assertEquals(iter.previous(), "two")
      iter.set("TWO")
      assertEquals(iter.previous(), "one")
      iter.set("ONE")
      assertTrue(iter.hasNext())
      assertFalse(iter.hasPrevious())

      assertEquals(iter.next(), "ONE")
      iter.remove()
      assertEquals(iter.next(), "TWO")
      iter.remove()
      assertEquals(iter.next(), "THREE")
      iter.remove()

      assertFalse(iter.hasNext())
      assertFalse(iter.hasPrevious())

      assertTrue(ll.isEmpty())
    }
  }
}

trait ListFactory extends CollectionFactory {
  def empty[E: ClassTag]: ju.List[E]

  // Refines the result type of CollectionFactory.fromElements
  override def fromElements[E: ClassTag](elems: E*): ju.List[E] = {
    val coll = empty[E]
    coll.addAll(TrivialImmutableCollection(elems: _*))
    coll
  }

  /** Sortable using java.util.Collections.sort
   */
  def sortableUsingCollections: Boolean = true
}
