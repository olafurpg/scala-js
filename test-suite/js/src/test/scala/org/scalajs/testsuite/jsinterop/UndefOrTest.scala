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

import scala.scalajs.js

import org.junit.Assert._
import org.junit.Test

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.JSAssert._

class UndefOrTest {

  def some[A](v: A): js.UndefOr[A] = v
  def none[A]: js.UndefOr[A] = js.undefined

  // scala.scalajs.js.UndefOr[A]

  @Test def convert_A_to_js_UndefOr_A(): Unit = {
    val x: js.UndefOr[Int] = 42
    assertFalse(x.isEmpty)
    assertTrue(x.isDefined)
    assertTrue(x.nonEmpty)
    assertEquals(42, x.get)
  }

  @Test def convert_undefined_to_js_UndefOr_A(): Unit = {
    val x: js.UndefOr[Int] = js.undefined
    assertTrue(x.isEmpty)
    assertFalse(x.isDefined)
    assertFalse(x.nonEmpty)
    assertThrows(classOf[NoSuchElementException], x.get)
  }

  @Test def explicitly_convert_A_to_js_UndefOr_A(): Unit = {
    val x: js.UndefOr[Int] = js.defined(42)
    assertFalse(x.isEmpty)
    assertEquals(42, x.get)

    val f: js.UndefOr[js.Function1[Int, Int]] = js.defined((x: Int) => x + 1)
    assertFalse(f.isEmpty)
    assertEquals(6, f.get(5))
  }

  @Test def `convert_to_js_Any_when_A_<%_js_Any`(): Unit = {
    val x: js.UndefOr[Int] = 42
    assertEquals(x, 42)

    val y: js.UndefOr[String] = js.undefined
    assertJSUndefined(y)
  }

  @Test def getOrElse(): Unit = {
    assertEquals(some("hello").getOrElse("ko"), "hello")
    assertEquals(none[String].getOrElse("ok"), "ok")

    var defaultComputed = false
    assertEquals(some("test") getOrElse ({
  defaultComputed = true
  "ko"
}), "test")
    assertFalse(defaultComputed)
  }

  @Test def orNull(): Unit = {
    assertEquals(some("hello").orNull, "hello")
    assertNull(none[String].orNull)
  }

  @Test def map(): Unit = {
    assertEquals(some(62).map(_ / 3), 62 / 3)
    assertJSUndefined(none[Int].map(_ / 3))
  }

  @Test def fold(): Unit = {
    assertEquals(6, some(3).fold(10)(_ * 2))
    assertEquals(10, none[Int].fold(10)(_ * 2))
  }

  @Test def flatMap(): Unit = {
    def f(x: Int): js.UndefOr[Int] = if (x > 0) x+3 else js.undefined
    assertEquals(some(6).flatMap(f), 9)
    assertJSUndefined(some(-6).flatMap(f))
    assertJSUndefined(none[Int].flatMap(f))
  }

  @Test def flatten(): Unit = {
    assertTrue(some(some(7)).flatten.isDefined)
    assertEquals(7, some(some(7)).flatten.get)
    assertFalse(some(none[Int]).flatten.isDefined)
    assertFalse(none[js.UndefOr[Int]].flatten.isDefined)
  }

  @Test def filter(): Unit = {
    assertTrue(some(7).filter(_ > 0).isDefined)
    assertEquals(7, some(7).filter(_ > 0).get)
    assertFalse(some(7).filter(_ < 0).isDefined)
    assertFalse(none[Int].filter(_ < 0).isDefined)
  }

  @Test def filterNot(): Unit = {
    assertTrue(some(7).filterNot(_ < 0).isDefined)
    assertEquals(7, some(7).filterNot(_ < 0).get)
    assertFalse(some(7).filterNot(_ > 0).isDefined)
    assertFalse(none[Int].filterNot(_ > 0).isDefined)
  }

  @Test def contains(): Unit = {
    assertTrue(some(7).contains(7))
    assertFalse(some(7).contains(8))
    assertFalse(none[Int].contains(7))

    assertFalse(some(()).contains(()))
  }

  @Test def exists(): Unit = {
    assertTrue(some(7).exists(_ > 0))
    assertFalse(some(7).exists(_ < 0))
    assertFalse(none[Int].exists(_ > 0))
  }

  @Test def forall(): Unit = {
    assertTrue(some(7).forall(_ > 0))
    assertFalse(some(7).forall(_ < 0))
    assertTrue(none[Int].forall(_ > 0))
  }

  @Test def foreach(): Unit = {
    var witness1 = 3
    some(42).foreach(witness1 = _)
    assertEquals(42, witness1)

    var witness2 = 3
    none[Int].foreach(witness2 = _)
    assertEquals(3, witness2)
  }

  @Test def collect(): Unit = {
    assertEquals(some("hello") collect ({
  case "hello" => "ok"
}), "ok")
    assertTrue(js.isUndefined(some("hello") collect {
      case "notthis" => "ko"
    }))
    assertTrue(js.isUndefined(none[String] collect {
      case "hello" => "ko"
    }))
  }

  @Test def collect_should_call_guard_at_most_once(): Unit = {
    var witness = 0
    def guard(x: String): Boolean = {
      witness += 1
      true
    }
    assertEquals(some("hello") collect ({
  case x @ "hello" if guard(x) => "ok"
}), "ok")
    assertEquals(1, witness)
  }

  @Test def orElse(): Unit = {
    assertTrue((some(true) orElse some(false)).get)
    assertEquals(some("ok") orElse none, "ok")
    assertEquals(none orElse some("yes"), "yes")
    assertJSUndefined(none orElse none)

    // #2095
    assertEquals(some("ok") orElse "yes", "ok")
    assertEquals(none orElse "yes", "yes")
  }

  @Test def toList(): Unit = {
    assertEquals(some("hello").toList, List("hello"))
    assertEquals(none[String].toList, List.empty[String])
  }

  @Test def toLeft_and_toRight(): Unit = {
    assertTrue(some("left").toLeft("right").isInstanceOf[Left[_, _]])
    assertTrue(none[String].toLeft("right").isInstanceOf[Right[_, _]])
    assertTrue(some("right").toRight("left").isInstanceOf[Right[_, _]])
    assertTrue(none[String].toRight("left").isInstanceOf[Left[_, _]])
  }

  @Test def toOption(): Unit = {
    assertTrue(some("foo").toOption == Some("foo"))
    assertTrue(none.toOption == None)
  }

  // scala.scalajs.js.JSConverters.JSRichOption

  import js.JSConverters._

  @Test def should_provide_orUndefined(): Unit = {
    assertEquals(Some("asdf").orUndefined, "asdf")
    assertJSUndefined((None: Option[String]).orUndefined)
    assertJSUndefined(None.orUndefined)
  }

}
