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

package org.scalajs.testsuite.scalalib

import org.junit.Test
import org.junit.Assert._
import org.junit.Assume._

import scala.collection.immutable.NumericRange
import scala.math.BigDecimal

import org.scalajs.testsuite.utils.Platform._

class RangesTest {

  @Test def Iterable_range_should_not_emit_dce_warnings_issue_650(): Unit = {
    Iterable.range(1, 10)
  }

  @Test def Iterable_range_and_simple_range_should_be_equal(): Unit = {
    // Mostly to exercise more methods of ranges for dce warnings
    assertEquals(Iterable.range(0, 10).toList, (0 until 10).toList)
  }

  @Test def NumericRange_overflow_issue_2407(): Unit = {
    val nr = NumericRange(Int.MinValue, Int.MaxValue, 1 << 23)
    assertEquals(Int.MinValue, nr.sum)
  }

  @Test def Range_foreach_issue_2409(): Unit = {
    val r = Int.MinValue to Int.MaxValue by (1 << 23)
    var i = 0
    r.foreach(_ => i += 1)
    assertEquals(512, i)
    assertEquals(512, r.length)
    assertEquals(Int.MinValue, r.sum)
  }

  @Test def Range_toString_issue_2412(): Unit = {
    if (scalaVersion.startsWith("2.11.")) {
      assertEquals((1 to 10 by 2).toString, "Range(1, 3, 5, 7, 9)")
      assertEquals((1 until 1 by 2).toString, "Range()")
      assertTrue(
          (BigDecimal(0.0) to BigDecimal(1.0)).toString.startsWith("scala.collection.immutable.Range$Partial"))
      assertEquals((0 to 1).toString, "Range(0, 1)")
    } else {
      assertEquals((1 to 10 by 2).toString, "inexact Range 1 to 10 by 2")
      assertEquals((1 until 1 by 2).toString, "empty Range 1 until 1 by 2")
      assertEquals((BigDecimal(0.0d) to BigDecimal(1.0d)).toString, "Range requires step")
      assertEquals((0 to 1).toString, "Range 0 to 1")
    }
  }

  @Test def NumericRange_toString_issue_2412(): Unit = {
    if (scalaVersion.startsWith("2.11.")) {
      assertEquals(NumericRange.inclusive(0, 10, 2).toString(),
          "NumericRange(0, 2, 4, 6, 8, 10)")
      assertEquals(NumericRange(0, 10, 2).toString,
          "NumericRange(0, 2, 4, 6, 8)")
    } else {
      assertEquals(NumericRange.inclusive(0, 10, 2).toString(),
          "NumericRange 0 to 10 by 2")
      assertEquals(NumericRange(0, 10, 2).toString,
          "NumericRange 0 until 10 by 2")
    }
  }

  @Test def NumericRange_with_arbitrary_integral(): Unit = {
    // This is broken in Scala JVM up to (including) 2.11.8, 2.12.1 (SI-10086).
    assumeFalse("Assumed not on JVM for 2.12.1",
        executingInJVM && scalaVersion == "2.12.1")

    // Our custom integral type.
    case class A(v: Int)

    implicit object aIsIntegral extends scala.math.Integral[A] {
      def compare(x: A, y: A): Int = x.v.compare(y.v)
      def fromInt(x: Int): A = A(x)
      def minus(x: A, y: A): A = A(x.v - y.v)
      def negate(x: A): A = A(-x.v)
      def plus(x: A, y: A): A = A(x.v + y.v)
      def times(x: A, y: A): A = A(x.v * y.v)
      def quot(x: A, y: A): A = A(x.v / y.v)
      def rem(x: A, y: A): A = A(x.v % y.v)
      def toDouble(x: A): Double = x.v.toDouble
      def toFloat(x: A): Float = x.v.toFloat
      def toInt(x: A): Int = x.v
      def toLong(x: A): Long = x.v.toLong
      def parseString(str: String): Option[A] = Some(A(str.toInt))
    }

    val r = NumericRange(A(1), A(10), A(1))
    assertEquals(r.min, A(1))
    assertEquals(r.max, A(9))

    // Also test with custom ordering.
    assertEquals(r.min(aIsIntegral.reverse), A(9))
    assertEquals(r.max(aIsIntegral.reverse), A(1))
  }
}
