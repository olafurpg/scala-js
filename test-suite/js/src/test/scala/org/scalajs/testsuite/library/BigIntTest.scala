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

package org.scalajs.testsuite.library

import org.junit.Assert._
import org.junit.Assume._
import org.junit.{BeforeClass, Test}
import org.scalajs.testsuite.utils.Platform

import scala.scalajs.js

class BigIntTest {

  @Test def apply(): Unit = {
    val fromString = js.BigInt("9007199254740992")
    assertEquals("9007199254740992", fromString.toString())

    val fromInt = js.BigInt(2147483647)
    assertEquals("2147483647", fromInt.toString())

    val fromDouble = js.BigInt(4294967295d)
    assertEquals("4294967295", fromDouble.toString())
  }

  @Test def asIntN(): Unit = {
    val x = js.BigInt.asIntN(8, js.BigInt("256"))
    assertEquals(js.BigInt("0"), x)
  }

  @Test def asUintN(): Unit = {
    val x = js.BigInt(-123)
    assertEquals(js.BigInt("1125899906842501"), js.BigInt.asUintN(50, x))
  }

  @Test def toLocaleString(): Unit = {
    val bi = js.BigInt("42123456789123456789")
    assertEquals("42123456789123456789", bi.toString())

    val result = bi
      .toLocaleString("de-DE", new js.BigInt.ToLocaleStringOptions {
        style = "currency"
        currency = "EUR"
      })

    // The exact return value is not specified. Just check the type.
    assertTrue(js.typeOf(result) == "string")
  }

  @Test def valueOf(): Unit = {
    val bi = js.BigInt("42123456789123456789")
    assertEquals(bi.valueOf(), bi)
  }

  @Test def operators(): Unit = {
    val previousMaxSafe = js.BigInt("9007199254740991")
    assertEquals(js.BigInt("9007199254740991"), previousMaxSafe)

    val maxPlusOne = previousMaxSafe + js.BigInt(1)
    assertEquals(js.BigInt("9007199254740992"), maxPlusOne)

    val theFuture = previousMaxSafe + js.BigInt(2)
    assertEquals(js.BigInt("9007199254740993"), theFuture)

    val multi = previousMaxSafe * js.BigInt(2)
    assertEquals(js.BigInt("18014398509481982"), multi)

    val subtr = multi - js.BigInt(10)
    assertEquals(js.BigInt("18014398509481972"), subtr)

    val mod = multi % js.BigInt(10)
    assertEquals(js.BigInt("2"), mod)

    // TODO: Scala.js does not recongnize ** as operator
    //    val bigN = js.BigInt(2) ** js.BigInt(54)
    //    assertEquals(bigN, js.BigInt("18014398509481984"))

    val negative = -js.BigInt("18014398509481984")
    assertEquals(js.BigInt("-18014398509481984"), negative)

    val bitAnd = js.BigInt(123) & js.BigInt(31)
    assertEquals(js.BigInt("27"), bitAnd)

    val bitOr = js.BigInt(123) | js.BigInt(31)
    assertEquals(js.BigInt("127"), bitOr)

    val bitXor = js.BigInt(123) ^ js.BigInt(31)
    assertEquals(js.BigInt("100"), bitXor)

    val bitLeftShift = js.BigInt(123) << js.BigInt(31)
    assertEquals(js.BigInt("264140488704"), bitLeftShift)

    val bitRightShift = js.BigInt(12345678) >> js.BigInt(9)
    assertEquals(js.BigInt("24112"), bitRightShift)

    val bitNot = ~js.BigInt("42")
    assertEquals(js.BigInt("-43"), bitNot)
  }

  @Test def compare_with_bigint(): Unit = {
    val n = js.BigInt("42")
    assertTrue(n == js.BigInt("42"))
    assertTrue(n != js.BigInt("43"))
    assertTrue(n > js.BigInt("41"))
    assertTrue(n >= js.BigInt("41"))
    assertTrue(n < js.BigInt("43"))
    assertTrue(n <= js.BigInt("43"))
  }

}
