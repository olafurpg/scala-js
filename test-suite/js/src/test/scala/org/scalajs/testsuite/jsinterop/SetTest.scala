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

package org.scalajs.testsuite.jsinterop

import org.junit.Assert._
import org.junit.Assume.assumeTrue
import org.junit.{BeforeClass, Test}

import scala.scalajs.{LinkingInfo, js}

object SetTest {
  @BeforeClass
  def assumeRuntimeSupportsSet(): Unit = {
    assumeTrue("Assume ES6", LinkingInfo.assumingES6)
  }
}

class SetTest {

  // scala.scalajs.js.Set

  @Test def testClear(): Unit = {
    val obj = js.Set("foo", "bar")
    assertTrue(obj.size == 2)
    obj.clear()
    assertTrue(obj.size == 0)
  }

  @Test def testIterator(): Unit = {
    val obj = js.Set("foo", "bar", "babar")
    val elems: List[String] = obj.iterator.toList
    assertEquals(elems, List("foo", "bar", "babar"))
  }

  @Test def testToJSSet(): Unit = {
    // scala.scalajs.js.JSConverters.JSRichGenSet

    import js.JSConverters._
    val obj = Set(1, 2).toJSSet
    assertTrue(obj(1))
    assertTrue(obj(2))
    assertFalse(obj(3))
  }

  @Test def testAdd(): Unit = {
    val obj = js.Set[String]()
    assertTrue(obj.size == 0)
    assertTrue(obj.add("foo"))
    assertTrue(obj.add("bar"))
    assertTrue(obj.size == 2)
  }

  @Test def testContains(): Unit = {
    val obj = js.Set("foo")
    assertTrue(obj.contains("foo"))
    assertFalse(obj.contains("bar"))
  }

  @Test def testRemove(): Unit = {
    val obj = js.Set("foo")
    assertTrue(obj.remove("foo"))
    assertFalse(obj.contains("foo"))
  }
}
