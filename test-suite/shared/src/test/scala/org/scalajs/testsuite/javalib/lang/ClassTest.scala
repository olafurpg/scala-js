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

import org.junit.Test
import org.junit.Assert._

import scala.runtime.BoxedUnit

import org.scalajs.testsuite.utils.Platform._

class ClassTest {

  private val PrimitiveClassOfs = Seq(
      classOf[Unit],
      classOf[Boolean],
      classOf[Char],
      classOf[Byte],
      classOf[Short],
      classOf[Int],
      classOf[Long],
      classOf[Float],
      classOf[Double]
  )

  private val BoxedClassOfs = Seq(
      classOf[java.lang.Void],
      classOf[java.lang.Boolean],
      classOf[java.lang.Character],
      classOf[java.lang.Byte],
      classOf[java.lang.Short],
      classOf[java.lang.Integer],
      classOf[java.lang.Long],
      classOf[java.lang.Float],
      classOf[java.lang.Double]
  )

  @Test def getPrimitiveTypeName(): Unit = {
    @noinline
    def testNoInline(expected: String, cls: Class[_]): Unit =
      assertEquals(cls.getName(), expected)

    @inline
    def test(expected: String, cls: Class[_]): Unit = {
      testNoInline(expected, cls)
      assertEquals(cls.getName(), expected)
    }

    test("void", classOf[Unit])
    test("boolean", classOf[Boolean])
    test("char", classOf[Char])
    test("byte", classOf[Byte])
    test("short", classOf[Short])
    test("int", classOf[Int])
    test("long", classOf[Long])
    test("float", classOf[Float])
    test("double", classOf[Double])
  }

  @Test def getClassGetName(): Unit = {
    // x.getClass().getName() is subject to optimizations

    @noinline
    def getClassOfNoInline(x: Any): Class[_] =
      x.getClass()

    @noinline
    def testNoInline(expected: String, x: Any): Unit = {
      assertEquals(getClassOfNoInline(x).getName(), expected)
      assertEquals(x.getClass().getName(), expected)
    }

    @inline
    def test(expected: String, x: Any): Unit = {
      testNoInline(expected, x)
      assertEquals(x.getClass().getName(), expected)
    }

    test(if (executingInJVM) "scala.runtime.BoxedUnit" else "java.lang.Void", ())
    test("java.lang.Boolean", true)
    test("java.lang.Character", 'A')
    test("java.lang.Byte", 0.toByte)
    test("java.lang.Byte", 5.toByte)
    test("java.lang.Short", 300.toShort)
    test("java.lang.Integer", 100000)
    test("java.lang.Long", Long.MaxValue)
    test("java.lang.Float", -0.0f)
    test("java.lang.Float", 1.5f)
    test("java.lang.Float", Float.NaN)
    test(if (hasStrictFloats) "java.lang.Double" else "java.lang.Float", 1.4)
    test("java.lang.String", "hello")
    test("java.lang.Object", new Object)
    test("scala.Some", Some(5))
    test("org.scalajs.testsuite.javalib.lang.ClassTest", this)

    test("[[I", new Array[Array[Int]](1))
    test("[[[Ljava.lang.String;", new Array[Array[Array[String]]](1))
  }

  @Test def wellKnownClasses(): Unit = {
    assertSame(classOf[Unit], scala.runtime.BoxedUnit.TYPE)
    assertSame(classOf[Unit], java.lang.Void.TYPE)
    assertSame(classOf[Boolean], java.lang.Boolean.TYPE)
    assertSame(classOf[Char], java.lang.Character.TYPE)
    assertSame(classOf[Byte], java.lang.Byte.TYPE)
    assertSame(classOf[Short], java.lang.Short.TYPE)
    assertSame(classOf[Int], java.lang.Integer.TYPE)
    assertSame(classOf[Long], java.lang.Long.TYPE)
    assertSame(classOf[Float], java.lang.Float.TYPE)
    assertSame(classOf[Double], java.lang.Double.TYPE)

    assertNotSame(classOf[java.lang.Void], scala.runtime.BoxedUnit.TYPE)
    assertNotSame(classOf[java.lang.Void], java.lang.Void.TYPE)
    assertNotSame(classOf[java.lang.Boolean], java.lang.Boolean.TYPE)
    assertNotSame(classOf[java.lang.Character], java.lang.Character.TYPE)
    assertNotSame(classOf[java.lang.Byte], java.lang.Byte.TYPE)
    assertNotSame(classOf[java.lang.Short], java.lang.Short.TYPE)
    assertNotSame(classOf[java.lang.Integer], java.lang.Integer.TYPE)
    assertNotSame(classOf[java.lang.Long], java.lang.Long.TYPE)
    assertNotSame(classOf[java.lang.Float], java.lang.Float.TYPE)
    assertNotSame(classOf[java.lang.Double], java.lang.Double.TYPE)
  }

  object TestObject

  @Test def getSimpleName(): Unit = {
    assertEquals(classOf[java.lang.Integer].getSimpleName(), "Integer")
    assertEquals(classOf[java.lang.Class[_]].getSimpleName(), "Class")
    assertEquals(classOf[scala.collection.Map[_, _]].getSimpleName(), "Map")
    assertEquals(classOf[ClassTestClass#InnerClass].getSimpleName(), "InnerClass")
    assertEquals(TestObject.getClass.getSimpleName(), "TestObject$")
  }

  @Test def isAssignableFrom(): Unit = {
    val SelectedClassOfs =
      PrimitiveClassOfs ++ BoxedClassOfs ++ Seq(classOf[Object], classOf[String])

    // All Classes are assignable from themselves
    for (cls <- SelectedClassOfs) {
      assertTrue(s"$cls should be assignable from itself",
          cls.isAssignableFrom(cls))
    }

    // Otherwise, if one side is a primitive, the result must be false
    for {
      left <- SelectedClassOfs
      right <- SelectedClassOfs
      if (left ne right) && (left.isPrimitive || right.isPrimitive)
    } {
      assertFalse(
          s"$left.isAssignableFrom($right) should be false",
          left.isAssignableFrom(right))
    }

    /* Positive tests with the special classes Object and String, as well as
     * with normal traits and classes.
     */

    assertTrue(classOf[Object].isAssignableFrom(classOf[String]))
    assertTrue(classOf[Seq[_]].isAssignableFrom(classOf[List[_]]))
    assertTrue(classOf[List[_]].isAssignableFrom(classOf[::[_]]))
    assertTrue(classOf[Seq[_]].isAssignableFrom(classOf[::[_]]))
    assertTrue(classOf[Object].isAssignableFrom(classOf[Array[String]]))
    assertTrue(classOf[Array[Seq[_]]].isAssignableFrom(classOf[Array[List[_]]]))

    // Negative tests

    assertFalse(classOf[String].isAssignableFrom(classOf[Object]))
    assertFalse(classOf[List[_]].isAssignableFrom(classOf[Seq[_]]))
    assertFalse(classOf[Option[_]].isAssignableFrom(classOf[::[_]]))
    assertFalse(classOf[Set[_]].isAssignableFrom(classOf[::[_]]))
    assertFalse(classOf[Array[String]].isAssignableFrom(classOf[Object]))
    assertFalse(classOf[Array[List[_]]].isAssignableFrom(classOf[Array[Seq[_]]]))

    /* All the boxed classes except Void extend Comparable, and since they are
     * hijacked, the code paths to test that they are assignable to Comparable
     * are different than for normal classes. The following test makes sure
     * these code paths are covered.
     */
    for {
      cls <- BoxedClassOfs
      if cls != classOf[java.lang.Void]
    } {
      assertTrue(
          s"classOf[Comparable[_]].isAssignableFrom($cls) should be true",
          classOf[Comparable[_]].isAssignableFrom(cls))
    }
  }

  @Test def getComponentType(): Unit = {
    @noinline
    def testNoInline(clazz: Class[_], componentType: Class[_]): Unit =
      assertEquals(clazz.getComponentType, componentType)

    @inline
    def test(clazz: Class[_], componentType: Class[_]): Unit = {
      testNoInline(clazz, componentType)
      assertEquals(clazz.getComponentType, componentType)
    }

    test(classOf[Array[Object]], classOf[Object])
    test(classOf[Array[Int]], classOf[Int])
    test(classOf[Array[String]], classOf[String])
    test(classOf[Array[Seq[_]]], classOf[Seq[_]])
    test(classOf[Array[Unit]], classOf[BoxedUnit]) // not Unit

    test(classOf[Array[Array[Object]]], classOf[Array[Object]])
    test(classOf[Array[Array[Int]]], classOf[Array[Int]])
    test(classOf[Array[Array[String]]], classOf[Array[String]])
    test(classOf[Array[Array[Seq[_]]]], classOf[Array[Seq[_]]])
    test(classOf[Array[Array[Unit]]], classOf[Array[Unit]])

    test(classOf[Object], null)
    test(classOf[Int], null)
    test(classOf[String], null)
    test(classOf[Seq[_]], null)
    test(classOf[Unit], null)
  }
}

class ClassTestClass {
  class InnerClass
}
