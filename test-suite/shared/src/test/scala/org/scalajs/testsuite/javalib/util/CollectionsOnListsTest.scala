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

import java.{lang => jl, util => ju}

import org.junit.Assert._
import org.junit.Test

import org.scalajs.testsuite.javalib.util.concurrent.CopyOnWriteArrayListFactory
import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.CollectionsTestBase

import scala.reflect.ClassTag

object CollectionsOnListTest extends CollectionsTestBase {

  // Test: sort[T<:Comparable[T]](List[T])
  def sort_on_comparables(factory: ListFactory): Unit = {
    if (factory.sortableUsingCollections) {
      test_sort_on_comparables[CustomComparable](factory,
        new CustomComparable(_), false)
      test_sort_on_comparables[jl.Integer](factory, jl.Integer.valueOf)
      test_sort_on_comparables[jl.Long](factory, _.toLong)
      test_sort_on_comparables[jl.Double](factory, _.toDouble)
    }
  }

  // Test: sort[T](List[T], Comparator[T])
  def sort_with_comparator(factory: ListFactory): Unit = {
    if (factory.sortableUsingCollections) {
      test_sort_with_comparator[CustomComparable](factory,
        new CustomComparable(_), (x, y) => x.compareTo(y), false)
      test_sort_with_comparator[jl.Integer](factory, _.toInt, (x, y) => x.compareTo(y))
      test_sort_with_comparator[jl.Long](factory, _.toLong,
        (x, y) => x.compareTo(y))
      test_sort_with_comparator[jl.Double](factory, _.toDouble,
        (x, y) => x.compareTo(y))
    }
  }

  private def test_sort_on_comparables[T <: AnyRef with Comparable[T]: ClassTag](
      factory: ListFactory, toElem: Int => T,
      absoluteOrder: Boolean = true): Unit = {

    val list = factory.empty[T]

    def testIfSorted(rangeValues: Boolean): Unit = {
      for (i <- range.init)
        assertTrue(list.get(i).compareTo(list.get(i + 1)) <= 0)
      if (absoluteOrder && rangeValues) {
        for (i <- range)
          assertEquals(0, list.get(i).compareTo(toElem(i)))
      }
    }

    list.addAll(rangeOfElems(toElem))
    ju.Collections.sort(list)
    testIfSorted(true)

    list.clear()
    list.addAll(TrivialImmutableCollection(range.reverse.map(toElem): _*))
    ju.Collections.sort(list)
    testIfSorted(true)

    for (seed <- List(0, 1, 42, -5432, 2341242)) {
      val rnd = new scala.util.Random(seed)
      list.clear()
      list.addAll(
          TrivialImmutableCollection(range.map(_ => toElem(rnd.nextInt())): _*))
      ju.Collections.sort(list)
      testIfSorted(false)
    }
  }

  private def test_sort_with_comparator[T: ClassTag](factory: ListFactory, toElem: Int => T,
      cmpFun: (T, T) => Int, absoluteOrder: Boolean = true): Unit = {

    val list = factory.empty[T]

    def testIfSorted(rangeValues: Boolean): Unit = {
      for (i <- range.init)
        assertTrue(cmpFun(list.get(i), list.get(i + 1)) <= 0)
      if (absoluteOrder && rangeValues) {
        for (i <- range)
          assertEquals(0, cmpFun(list.get(i), toElem(i)))
      }
    }

    val cmp = new ju.Comparator[T] {
      override def compare(o1: T, o2: T): Int = cmpFun(o1, o2)
    }

    list.addAll(rangeOfElems(toElem))
    ju.Collections.sort(list, cmp)
    testIfSorted(true)

    list.clear()
    list.addAll(TrivialImmutableCollection(range.reverse.map(toElem): _*))
    ju.Collections.sort(list, cmp)
    testIfSorted(true)

    for (seed <- List(0, 1, 42, -5432, 2341242)) {
      val rnd = new scala.util.Random(seed)
      list.clear()
      list.addAll(
          TrivialImmutableCollection(range.map(_ => toElem(rnd.nextInt())): _*))
      ju.Collections.sort(list, cmp)
      testIfSorted(false)
    }
  }
}

trait CollectionsOnListTest extends CollectionsOnCollectionsTest {

  def factory: ListFactory

  @Test def sort_on_comparables(): Unit =
    CollectionsOnListTest.sort_on_comparables(factory)

  @Test def sort_with_comparator(): Unit =
    CollectionsOnListTest.sort_with_comparator(factory)

  @Test def binarySearch_on_comparables(): Unit = {
    // Test: binarySearch[T](list: List[Comparable[T]], T)
    def test[T <: AnyRef with Comparable[T]: ClassTag](toElem: Int => T): Unit = {
      val list = factory.fromElements[T](range.map(toElem).sorted: _*)

      for (i <- Seq(range.head, range.last, range(range.size/3),
        range(range.size/2), range(3*range.size/5))) {
        assertEquals(i, ju.Collections.binarySearch(list, toElem(i)))
      }

      // If not found it should return: -(insertion point) - 1
      assertEquals(-1, ju.Collections.binarySearch(list, toElem(-1)))
      assertEquals(-1, ju.Collections.binarySearch(list, toElem(-42)))
      assertEquals(-range.size - 1,
        ju.Collections.binarySearch(list, toElem(range.last + 1)))
      assertEquals(-range.size - 1,
        ju.Collections.binarySearch(list, toElem(range.last + 42)))
      list.remove(range.last / 2)
      assertEquals(-(range.last / 2) - 1,
        ju.Collections.binarySearch(list, toElem(range.last / 2)))
    }

    test[jl.Integer](jl.Integer.valueOf)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
  }

  @Test def binarySearch_with_comparator(): Unit = {
    // Test: binarySearch[T](List[T], key: T, Comparator[T]))
    def test[T: ClassTag](toElem: Int => T, cmpFun: (T, T) => Int): Unit = {
      val cmp = new ju.Comparator[T] {
        override def compare(o1: T, o2: T): Int = cmpFun(o1, o2)
      }

      val list = factory.fromElements[T](
          range.map(toElem).sortWith(cmpFun(_, _) < 0): _*)

      for (i <- Seq(range.head, range.last, range(range.size/3),
        range(range.size/2), range(3*range.size/5))) {
        assertEquals(i, ju.Collections.binarySearch(list, toElem(i), cmp))
      }

      // If not found it should return: -(insertion point) - 1
      assertEquals(-1, ju.Collections.binarySearch(list, toElem(-1), cmp))
      assertEquals(-1, ju.Collections.binarySearch(list, toElem(-42), cmp))
      assertEquals(-range.size - 1,
          ju.Collections.binarySearch(list, toElem(range.last + 1), cmp))
      assertEquals(-range.size - 1,
          ju.Collections.binarySearch(list, toElem(range.last + 42), cmp))
      list.remove(range.last / 2)
      assertEquals(-(range.last / 2) - 1,
          ju.Collections.binarySearch(list, toElem(range.last / 2), cmp))
    }

    test[jl.Integer](_.toInt, (x, y) => x.compareTo(y))
    test[jl.Long](_.toLong, (x, y) => x.compareTo(y))
    test[jl.Double](_.toDouble, (x, y) => x.compareTo(y))
  }

  @Test def reverse(): Unit = {
    // Test: reverse(list: List[_])
    def test[T: ClassTag](toElem: Int => T): Unit = {
      val list = factory.fromElements[T](range.map(toElem): _*)

      def testIfInOrder(reversed: Boolean): Unit = {
        for (i <- range) {
          val expected =
            if (reversed) range.last - i
            else i
          assertEquals(list.get(i), toElem(expected))
        }
      }

      ju.Collections.reverse(list)
      testIfInOrder(true)

      ju.Collections.reverse(list)
      testIfInOrder(false)
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def shuffle(): Unit = {
    def testShuffle(shuffle: ju.List[_] => Unit): Unit = {
      def test[E: ClassTag](toElem: Int => E): Unit = {
        val list = factory.empty[E]
        ju.Collections.shuffle(list)
        assertEquals(0, list.size)
        list.addAll(rangeOfElems(toElem))
        shuffle(list)
        assertEquals(range.size, list.size)
        assertTrue(list.containsAll(rangeOfElems(toElem)))
      }
      test[jl.Integer](_.toInt)
      test[jl.Long](_.toLong)
      test[jl.Double](_.toDouble)
      test[String](_.toString)
    }

    // Test: shuffle(list: List[_])
    // Relies on the correctness of shuffle(list: List[_], rnd: Random)
    // Tests for this version are omitted because they are not reproducible

    // Test: shuffle(list: List[_], rnd: Random)
    testShuffle(ju.Collections.shuffle(_, new ju.Random(0)))
    testShuffle(ju.Collections.shuffle(_, new ju.Random(42)))
    testShuffle(ju.Collections.shuffle(_, new ju.Random(-1243)))
    testShuffle(ju.Collections.shuffle(_, new ju.Random(94325)))
  }

  @Test def swap(): Unit = {
    // Test: swap(List[_], Int, Int)
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val list = factory.fromElements[E](range.map(toElem): _*)

      ju.Collections.swap(list, 0, 1)
      assertEquals(list.get(0), toElem(1))
      assertEquals(list.get(1), toElem(0))
      for (i <- range.drop(2))
        assertEquals(list.get(i), toElem(i))

      ju.Collections.swap(list, 0, range.last)
      assertEquals(list.get(0), toElem(range.last))
      assertEquals(list.get(1), toElem(0))
      for (i <- range.drop(2).init)
        assertEquals(list.get(i), toElem(i))
      assertEquals(list.get(range.last), toElem(1))

      ju.Collections.swap(list, 0, range.last)
      assertEquals(list.get(0), toElem(1))
      assertEquals(list.get(1), toElem(0))
      for (i <- range.drop(2))
        assertEquals(list.get(i), toElem(i))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def fill(): Unit = {
    // Test: fill[E](List[E], E)
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val list = factory.fromElements[E](range.map(toElem): _*)

      ju.Collections.fill(list, toElem(0))
      for (i <- range)
        assertEquals(list.get(i), toElem(0))

      ju.Collections.fill(list, toElem(42))
      for (i <- range)
        assertEquals(list.get(i), toElem(42))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def copy(): Unit = {
    // Test: copy[E](List[E], List[E])
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val source = factory.empty[E]
      val dest = factory.empty[E]

      // Lists of same size
      range.foreach(i => source.add(toElem(i)))
      range.foreach(i => dest.add(toElem(-i)))
      ju.Collections.copy(dest, source)
      for (i <- range)
        assertEquals(dest.get(i), toElem(i))

      // source.size < dest.size
      source.clear()
      dest.clear()
      range.take(range.size / 2).foreach(i => source.add(toElem(i)))
      range.foreach(i => dest.add(toElem(-i)))
      ju.Collections.copy(dest, source)
      for (i <- range.take(range.size / 2))
        assertEquals(dest.get(i), toElem(i))
      for (i <- range.drop(range.size / 2))
        assertEquals(dest.get(i), toElem(-i))

      // source.size > dest.size
      source.clear()
      dest.clear()
      range.foreach(i => source.add(toElem(i)))
      range.take(range.size / 2).foreach(i => dest.add(toElem(-i)))
      expectThrows(classOf[IndexOutOfBoundsException], ju.Collections.copy(dest, source))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def rotate(): Unit = {
    def modulo(a: Int, b: Int): Int = ((a % b) + b) % b
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val list = factory.fromElements[E](range.map(toElem): _*)

      ju.Collections.rotate(list, 0)
      for (i <- range)
        assertEquals(list.get(i), toElem(i))

      ju.Collections.rotate(list, list.size)
      for (i <- range)
        assertEquals(list.get(i), toElem(i))

      ju.Collections.rotate(list, 1)
      for (i <- range)
        assertEquals(list.get(i), toElem(modulo(i - 1, range.size)))

      ju.Collections.rotate(list, 1)
      for (i <- range)
        assertEquals(list.get(i), toElem(modulo(i - 2, range.size)))

      ju.Collections.rotate(list, -5)
      for (i <- range)
        assertEquals(list.get(i), toElem(modulo(i + 3, range.size)))

      list.clear()
      list.addAll(TrivialImmutableCollection((0 until 6).map(toElem): _*))
      ju.Collections.rotate(list, 2)
      for (i <- 0 until 6)
        assertEquals(list.get(i), toElem(modulo(i - 2, 6)))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def replaceAll(): Unit = {
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val list = factory.fromElements[E](range.map(toElem): _*)

      ju.Collections.replaceAll(list, toElem(range.last), toElem(0))
      for (i <- range.init)
        assertEquals(list.get(i), toElem(i))
      assertEquals(list.get(list.size() - 1), toElem(0))

      ju.Collections.replaceAll(list, toElem(range(range.size - 2)), toElem(0))
      for (i <- range.dropRight(2))
        assertEquals(list.get(i), toElem(i))
      assertEquals(list.get(list.size() - 2), toElem(0))
      assertEquals(list.get(list.size() - 1), toElem(0))

      ju.Collections.replaceAll(list, toElem(0), toElem(-1))
      for (i <- range.tail.dropRight(2))
        assertEquals(list.get(i), toElem(i))
      assertEquals(list.get(0), toElem(-1))
      assertEquals(list.get(list.size() - 2), toElem(-1))
      assertEquals(list.get(list.size() - 1), toElem(-1))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def indexOfSubList(): Unit = {
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val source = factory.empty[E]
      val target = factory.empty[E]

      assertEquals(0, ju.Collections.indexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(0, ju.Collections.indexOfSubList(source, target))

      target.addAll(rangeOfElems(toElem))
      assertEquals(0, ju.Collections.indexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(0, ju.Collections.indexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(0, ju.Collections.indexOfSubList(source, target))

      source.remove(0)
      assertEquals(range.size - 1, ju.Collections.indexOfSubList(source, target))

      target.add(0, toElem(-5))
      assertEquals(-1, ju.Collections.indexOfSubList(source, target))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def lastIndexOfSubList(): Unit = {
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val source = factory.empty[E]
      val target = factory.empty[E]

      assertEquals(0, ju.Collections.lastIndexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(range.size, ju.Collections.lastIndexOfSubList(source, target))

      target.addAll(rangeOfElems(toElem))
      assertEquals(0, ju.Collections.lastIndexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(range.size, ju.Collections.lastIndexOfSubList(source, target))

      source.addAll(rangeOfElems(toElem))
      assertEquals(2 * range.size, ju.Collections.lastIndexOfSubList(source, target))

      source.remove(source.size - 1)
      assertEquals(range.size, ju.Collections.lastIndexOfSubList(source, target))

      target.add(0, toElem(-5))
      assertEquals(-1, ju.Collections.lastIndexOfSubList(source, target))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }

  @Test def unmodifiableList(): Unit = {
    def test[E: ClassTag](toElem: Int => E): Unit = {
      val immuList = ju.Collections.unmodifiableList(factory.empty[E])
      testListUnmodifiability(immuList, toElem(0))
    }

    test[jl.Integer](_.toInt)
    test[jl.Long](_.toLong)
    test[jl.Double](_.toDouble)
    test[String](_.toString)
  }
}

class CollectionsOnAbstractListTest extends CollectionsOnListTest {
  def factory: ListFactory = new AbstractListFactory
}

class CollectionsOnArrayListTest extends CollectionsOnListTest {
  def factory: ListFactory = new ArrayListFactory
}

class CollectionsOnLinkedListTest extends CollectionsOnListTest {
  def factory: ListFactory = new LinkedListFactory
}

class CollectionsOnCopyOnWriteArrayListTest extends CollectionsOnListTest {
  def factory: ListFactory = new CopyOnWriteArrayListFactory
}
