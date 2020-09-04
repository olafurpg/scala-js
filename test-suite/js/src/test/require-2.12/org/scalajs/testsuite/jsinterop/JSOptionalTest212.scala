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
import scala.scalajs.js.annotation._

import org.junit.Assert._
import org.junit.Test

import org.scalajs.testsuite.utils.JSAssert._

/** 2.12+ tests for `@JSOptional`.
 *
 *  This class is basically a copy-paste of `JSOptionalTest`, where
 *  `override val/def`s do not have an explicit result type. Instead, they
 *  are inferred from the superclasses.
 */
class JSOptionalTest212 {
  import JSOptionalTest212._

  @Test def classImplementsTraitWithOptional(): Unit = {
    val obj = new ClassImplementsTraitWithOptional

    assertEquals(obj.x, js.undefined)
    assertFalse(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertFalse(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def anonClassImplementsTraitWithOptional(): Unit = {
    val obj = new TraitWithOptional {}

    assertEquals(obj.x, js.undefined)
    assertFalse(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertFalse(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def undefinedInClassIsNotOptional(): Unit = {
    val obj = new UndefinedInClassIsNotOptional

    assertEquals(obj.x, js.undefined)
    assertTrue(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertTrue(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertTrue(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertTrue(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def overrideWithUndefinedInClassIsNotOptional(): Unit = {
    val obj = new OverrideWithUndefinedInClassIsNotOptional

    assertEquals(obj.x, js.undefined)
    assertTrue(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertTrue(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertTrue(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertTrue(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def classOverrideOptionalWithConcrete(): Unit = {
    val obj = new ClassImplementsTraitWithOptionalOverrideWithConcrete

    assertEquals(obj.x, 42)
    assertTrue(obj.hasOwnProperty("x"))

    assertEquals(obj.y, "hello")
    assertTrue(obj.hasOwnProperty("y"))

    assertEquals(obj.y2, "world")
    assertTrue(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, Some(5))
    assertTrue(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def anonClassOverrideOptionalWithConcrete(): Unit = {
    val obj = new TraitWithOptional {
      override val x = 42
      override val y = "hello"
      override def y2 = "world" // scalastyle:ignore
      z = Some(5)
    }

    assertEquals(obj.x, 42)
    assertTrue(obj.hasOwnProperty("x"))

    assertEquals(obj.y, "hello")
    assertTrue(obj.hasOwnProperty("y"))

    assertEquals(obj.y2, "world")
    assertTrue(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, Some(5))
    assertTrue(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def overrideClassAbstractWithOptional(): Unit = {
    trait OverrideClassAbstractWithOptional extends ClassWithAbstracts {
      val x = js.undefined
      def y = js.undefined
      val y2 = js.undefined
      var z: js.UndefOr[Option[Int]] = js.undefined
    }

    val obj = new OverrideClassAbstractWithOptional {}

    assertEquals(obj.x, js.undefined)
    assertFalse(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertFalse(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def overrideTraitAbstractWithOptional(): Unit = {
    trait TraitWithAbstracts extends js.Object {
      val x: js.UndefOr[Int]
      def y: js.UndefOr[String]
      def y2: js.UndefOr[String]
      var z: js.UndefOr[Option[Int]]
    }

    trait OverrideTraitAbstractWithOptional extends TraitWithAbstracts {
      val x = js.undefined
      def y = js.undefined
      val y2 = js.undefined
      var z: js.UndefOr[Option[Int]] = js.undefined
    }

    val obj = new OverrideTraitAbstractWithOptional {}

    assertEquals(obj.x, js.undefined)
    assertFalse(obj.hasOwnProperty("x"))

    assertEquals(obj.y, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y"))

    assertEquals(obj.y2, js.undefined)
    assertFalse(js.Object.hasProperty(obj, "y2"))

    assertEquals(obj.z, js.undefined)
    assertFalse(obj.hasOwnProperty("z"))
    obj.z = Some(3)
    assertEquals(obj.z, Some(3))
  }

  @Test def traitWithOptionalFunction(): Unit = {
    val obj = new TraitWithOptionalFunction {
      override val f = js.defined(x => x + 1)
    }

    assertEquals(js.typeOf(obj.f), "function")
    assertEquals(6, obj.f.get(5))
  }
}

object JSOptionalTest212 {
  trait TraitWithOptional extends js.Object {
    val x: js.UndefOr[Int] = js.undefined
    def y: js.UndefOr[String] = js.undefined
    def y2: js.UndefOr[String] = js.undefined
    var z: js.UndefOr[Option[Int]] = js.undefined
  }

  class ClassImplementsTraitWithOptional extends TraitWithOptional

  class UndefinedInClassIsNotOptional extends js.Object {
    val x: js.UndefOr[Int] = js.undefined
    def y: js.UndefOr[String] = js.undefined
    def y2: js.UndefOr[String] = js.undefined
    var z: js.UndefOr[Option[Int]] = js.undefined
  }

  class OverrideWithUndefinedInClassIsNotOptional extends TraitWithOptional {
    override val x = js.undefined
    override def y = js.undefined // scalastyle:ignore
    override def y2 = js.undefined // scalastyle:ignore
    z = js.undefined
  }

  class ClassImplementsTraitWithOptionalOverrideWithConcrete
      extends TraitWithOptional {
    override val x = 42
    override val y = "hello"
    override def y2 = "world" // scalastyle:ignore
    z = Some(5)
  }

  abstract class ClassWithAbstracts extends js.Object {
    val x: js.UndefOr[Int]
    def y: js.UndefOr[String]
    def y2: js.UndefOr[String]
    var z: js.UndefOr[Option[Int]]
  }

  trait TraitWithOptionalFunction extends js.Object {
    val f: js.UndefOr[js.Function1[Int, Int]] = js.undefined
  }
}
