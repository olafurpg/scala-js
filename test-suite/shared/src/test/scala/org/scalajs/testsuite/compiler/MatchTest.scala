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

package org.scalajs.testsuite.compiler

import scala.annotation.switch

import org.junit.Test
import org.junit.Assert._

class MatchTest {
  import MatchTest._

  @Test def switchWithGuardsStat(): Unit = {
    def test(x: Int, y: Int): String = {
      var result = ""
      (x: @switch) match {
        case 1            => result = "one"
        case 2 if y < 10  => result = "two special"
        case 2            => result = "two"
        case 3 if y < 10  => result = "three special"
        case 3 if y > 100 => result = "three big special"
        case z if y > 100 => result = "big " + z
        case _            => result = "None of those"
      }
      result
    }

    assertEquals(test(1, 0), "one")
    assertEquals(test(2, 0), "two special")
    assertEquals(test(2, 50), "two")
    assertEquals(test(3, 5), "three special")
    assertEquals(test(3, 200), "three big special")
    assertEquals(test(3, 50), "None of those")
    assertEquals(test(5, 300), "big 5")
    assertEquals(test(5, 20), "None of those")
  }

  @Test def switchWithGuardsExpr(): Unit = {
    def test(x: Int, y: Int): String = {
      (x: @switch) match {
        case 1            => "one"
        case 2 if y < 10  => "two special"
        case 2            => "two"
        case 3 if y < 10  => "three special"
        case 3 if y > 100 => "three big special"
        case z if y > 100 => "big " + z
        case _            => "None of those"
      }
    }

    assertEquals(test(1, 0), "one")
    assertEquals(test(2, 0), "two special")
    assertEquals(test(2, 50), "two")
    assertEquals(test(3, 5), "three special")
    assertEquals(test(3, 200), "three big special")
    assertEquals(test(3, 50), "None of those")
    assertEquals(test(5, 300), "big 5")
    assertEquals(test(5, 20), "None of those")
  }

  // #2554
  @Test def matchWithNonIdentityMatchEndScalaLib(): Unit = {
    val foo: Option[Int] = Some(42)

    /* This match generates a value class boxing operation in the matchEnd (in
     * 2.11).
     */
    val result =
      "foo = " ++ (foo match { case Some(0) => "zero" case _ => "unknown" })

    assertEquals(result, "foo = unknown")
  }


  // #2554
  @Test def matchWithNonIdentityMatchEndIndependent(): Unit = {
    import scala.language.implicitConversions

    implicit def toValueClass(x: Int): ValueClass = new ValueClass(x)
    def show[T](x: ValueClassBase[T]): String = x.f().toString

    val foo: Option[Int] = Some(42)
    assertEquals(show(foo match {
  case Some(0) => 1
  case _ => 2
}), "4")
  }

}

object MatchTest {
  trait ValueClassBase[T] extends Any {
    def f(): T
  }

  class ValueClass(private val x: Int) extends AnyVal with ValueClassBase[Int] {
    def f(): Int = x * 2
  }
}
