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

import org.junit.Assert._
import org.junit.Test

import java.util.Optional

import org.scalajs.testsuite.utils.AssertThrows._

class OptionalTest {

  @Test def testCreation(): Unit = {
    Optional.empty[String]()
    Optional.of[String]("")
    assertThrows(classOf[NullPointerException], Optional.of[String](null))
    Optional.ofNullable[String]("")
    Optional.ofNullable[String](null)
  }

  @Test def testEquals(): Unit = {
    assertEquals(Optional.ofNullable[String](null), Optional.empty[String]())
    assertEquals(Optional.ofNullable[String](""), Optional.of[String](""))
    assertNotEquals(Optional.of[String]("1"), Optional.ofNullable[String]("2"))
    assertEquals(Optional.ofNullable[Int](1), Optional.of[Int](1))
    assertNotEquals(Optional.of[Int](1), Optional.ofNullable[Int](2))
    case class Test(value: Long)
    assertEquals(Optional.ofNullable(Test(1L)), Optional.of(Test(1L)))
    assertNotEquals(Optional.of(Test(1L)), Optional.ofNullable(Test(2L)))
  }

  @Test def testIsPresent(): Unit = {
    val emp = Optional.empty[String]()
    assertFalse(emp.isPresent())
    val fullInt = Optional.of[Int](1)
    assertTrue(fullInt.isPresent())
    val fullString = Optional.of[String]("")
    assertTrue(fullString.isPresent())
  }

  @Test def testGet(): Unit = {
    val emp = Optional.empty[String]()
    assertThrows(classOf[NoSuchElementException], emp.get())
    val fullInt = Optional.of[Int](1)
    assertEquals(1, fullInt.get())
    val fullString = Optional.of[String]("")
    assertEquals(fullString.get(), "")
    class Test()
    val t = new Test()
    assertEquals(Optional.of(t).get(), t)
  }

  @Test def testOrElse(): Unit = {
    val emp = Optional.empty[String]()
    assertEquals(emp.orElse("123"), "123")
    val emptyInt = Optional.empty[Int]()
    assertEquals(2, emptyInt.orElse(2))
  }

  @Test def testHashCode(): Unit = {
    val emp = Optional.empty[String]()
    assertEquals(emp.hashCode(), 0)
    val fullString = Optional.of[String]("123")
    assertEquals(fullString.hashCode(), "123".hashCode())
  }

}
