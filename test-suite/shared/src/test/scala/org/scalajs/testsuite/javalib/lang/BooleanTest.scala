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

package org.scalajs.testsuite.javalib.lang

import java.lang.{Boolean => JBoolean}

import org.junit.Test
import org.junit.Assert._

import org.scalajs.testsuite.utils.AssertThrows._

/** Tests the implementation of the java standard library Boolean
 */
class BooleanTest {

  @Test def booleanValue(): Unit = {
    assertEquals(JBoolean.TRUE.booleanValue(), true)
    assertEquals(JBoolean.FALSE.booleanValue(), false)
    expectThrows(classOf[Exception], (null: JBoolean).booleanValue())
  }

  @Test def compareTo(): Unit = {
    def compare(x: Boolean, y: Boolean): Int =
      new JBoolean(x).compareTo(new JBoolean(y))

    assertEquals(0, compare(false, false))
    assertTrue(compare(false, true) < 0)
    assertTrue(compare(true, false) > 0)
    assertEquals(0, compare(true, true))
  }

  @Test def should_be_a_Comparable(): Unit = {
    def compare(x: Any, y: Any): Int =
      x.asInstanceOf[Comparable[Any]].compareTo(y)

    assertEquals(0, compare(false, false))
    assertTrue(compare(false, true) < 0)
    assertTrue(compare(true, false) > 0)
    assertEquals(0, compare(true, true))
  }

  @Test def should_parse_strings(): Unit = {
    def test(s: String, v: Boolean): Unit = {
      assertEquals(JBoolean.parseBoolean(s), v)
      assertEquals(JBoolean.valueOf(s).booleanValue(), v)
      assertEquals(new JBoolean(s).booleanValue(), v)
    }

    test("false", false)
    test("true", true)
    test("TrUe", true)
    test(null, false)
    test("truee", false)
  }
}
