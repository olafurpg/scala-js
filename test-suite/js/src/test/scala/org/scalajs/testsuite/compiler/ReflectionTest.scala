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

import scala.language.implicitConversions

import java.lang.Cloneable
import java.io.Serializable

import scala.reflect.{classTag, ClassTag}

import scala.scalajs.js
import js.annotation.JSGlobal

import org.junit.Test
import org.junit.Assert._
import org.junit.Assume._

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.Platform._

/** Tests the little reflection we support */
class ReflectionTest {
  import ReflectionTest._

  def implicitClassTagTest[A: ClassTag](x: Any): Boolean = x match {
    case x: A => true
    case _ => false
  }

  @Test def java_lang_Class_getName_under_normal_circumstances(): Unit = {
    @noinline
    def testNoInline(expected: String, cls: Class[_]): Unit =
      assertEquals(cls.getName(), expected)

    @inline
    def test(expected: String, cls: Class[_]): Unit = {
      testNoInline(expected, cls)
      assertEquals(cls.getName(), expected)
    }

    test("scala.Some", classOf[scala.Some[_]])
  }

  @Test def should_append_$_to_class_name_of_objects(): Unit = {
    assertEquals(TestObject.getClass.getName,
      "org.scalajs.testsuite.compiler.ReflectionTest$TestObject$")
  }

  @Test def java_lang_Class_getName_renamed_through_semantics(): Unit = {
    @noinline
    def testNoInline(expected: String, cls: Class[_]): Unit =
      assertEquals(cls.getName(), expected)

    @inline
    def test(expected: String, cls: Class[_]): Unit = {
      testNoInline(expected, cls)
      assertEquals(cls.getName(), expected)
    }

    test("renamed.test.Class", classOf[RenamedTestClass])
    test("renamed.test.byprefix.RenamedTestClass1",
        classOf[PrefixRenamedTestClass1])
    test("renamed.test.byprefix.RenamedTestClass2",
        classOf[PrefixRenamedTestClass2])
    test("renamed.test.byotherprefix.RenamedTestClass",
        classOf[OtherPrefixRenamedTestClass])
  }

  @Test def java_lang_Object_getClass_getName_renamed_through_semantics(): Unit = {
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

    test("renamed.test.Class", new RenamedTestClass)
    test("renamed.test.byprefix.RenamedTestClass1",
        new PrefixRenamedTestClass1)
    test("renamed.test.byprefix.RenamedTestClass2",
        new PrefixRenamedTestClass2)
    test("renamed.test.byotherprefix.RenamedTestClass",
        new OtherPrefixRenamedTestClass)
  }

  @Test def should_support_isInstance(): Unit = {
    class A
    class B extends A
    val b = new B
    assertTrue(classOf[A].isInstance(b))
    assertFalse(classOf[A].isInstance("hello"))

    assertTrue(classOf[Array[Seq[_]]].isInstance(Array(List(3))))

    assertTrue(classOf[Serializable].isInstance(1))
    assertTrue(classOf[Serializable].isInstance(1.4))
    assertTrue(classOf[Serializable].isInstance(true))
    assertTrue(classOf[Serializable].isInstance('Z'))
    assertTrue(classOf[Serializable].isInstance("hello"))

    assertTrue(classOf[Serializable].isInstance(new Array[Int](1)))
    assertTrue(classOf[Cloneable].isInstance(new Array[Int](1)))
    assertTrue(classOf[Serializable].isInstance(new Array[String](1)))
    assertTrue(classOf[Cloneable].isInstance(new Array[String](1)))
  }

  @Test def isInstance_for_JS_class(): Unit = {
    js.eval("""var ReflectionTestJSClass = (function() {})""")

    val obj = new ReflectionTestJSClass
    assertTrue(obj.isInstanceOf[ReflectionTestJSClass])
    assertTrue(classOf[ReflectionTestJSClass].isInstance(obj))

    val other = (5, 6): Any
    assertFalse(other.isInstanceOf[ReflectionTestJSClass])
    assertFalse(classOf[ReflectionTestJSClass].isInstance(other))

    val ct = classTag[ReflectionTestJSClass]
    assertTrue(ct.unapply(obj).isDefined)
    assertFalse(ct.unapply(other).isDefined)

    assertTrue(implicitClassTagTest[ReflectionTestJSClass](obj))
    assertFalse(implicitClassTagTest[ReflectionTestJSClass](other))
  }

  @Test def isInstance_for_JS_traits_should_fail(): Unit = {
    assertThrows(classOf[Exception], classOf[ReflectionTestJSTrait].isInstance(5))

    val ct = classTag[ReflectionTestJSTrait]
    assertThrows(classOf[Exception], ct.unapply(new AnyRef))

    assertThrows(classOf[Exception], implicitClassTagTest[ReflectionTestJSTrait](new AnyRef))
  }

  @Test def getClass_for_normal_types(): Unit = {
    class Foo {
      def bar(): Class[_] = super.getClass()
    }
    val foo = new Foo
    assertSame(foo.getClass(), classOf[Foo])
    assertSame(foo.bar(), classOf[Foo])
  }

  @Test def getClass_for_anti_boxed_primitive_types(): Unit = {
    implicit def classAsAny(c: java.lang.Class[_]): js.Any =
      c.asInstanceOf[js.Any]
    assertEquals((false: Any).getClass, classOf[java.lang.Boolean])
    assertEquals(('a': Any).getClass, classOf[java.lang.Character])
    assertEquals((1.toByte: Any).getClass, classOf[java.lang.Byte])
    assertEquals((1.toShort: Any).getClass, classOf[java.lang.Byte])
    assertEquals((1: Any).getClass, classOf[java.lang.Byte])
    assertEquals((1L: Any).getClass, classOf[java.lang.Long])
    assertEquals((1.5f: Any).getClass, classOf[java.lang.Float])
    assertEquals((1.5d: Any).getClass, classOf[java.lang.Float])
    assertEquals(((): Any).getClass, classOf[scala.runtime.BoxedUnit])
  }

  @Test def getSuperclass_issue_1489(): Unit = {
    assertEquals(classOf[SomeChildClass].getSuperclass, classOf[SomeParentClass])
    assertNull(classOf[AnyRef].getSuperclass)
    assertEquals(classOf[String].getSuperclass, classOf[AnyRef])
    assertEquals(classOf[Integer].getSuperclass, classOf[Number])

    assertEquals(classOf[ChildClassWhoseDataIsAccessedDirectly].getSuperclass.getName,
      "org.scalajs.testsuite.compiler.ReflectionTest$ParentClassWhoseDataIsNotAccessedDirectly")
  }

  @Test def cast_positive(): Unit = {
    assertNull(classOf[String].cast(null))
    assertEquals(classOf[String].cast("hello"), "hello")
    assertEquals(classOf[Seq[_]].cast(List(1, 2)), List(1, 2))
    classOf[Serializable].cast(Array(3)) // should not throw
    classOf[Cloneable].cast(Array(3)) // should not throw
    classOf[Object].cast(js.Array(3, 4)) // should not throw
  }

  @Test def cast_negative(): Unit = {
    assumeTrue("Assumed compliant asInstanceOf", hasCompliantAsInstanceOfs)
    assertThrows(classOf[Exception], classOf[String].cast(5))
    assertThrows(classOf[Exception], classOf[Seq[_]].cast(Some("foo")))
  }
}

object ReflectionTest {
  object TestObject

  class RenamedTestClass

  class PrefixRenamedTestClass1
  class PrefixRenamedTestClass2

  class OtherPrefixRenamedTestClass

  @JSGlobal("ReflectionTestJSClass")
  @js.native
  class ReflectionTestJSClass extends js.Object

  @js.native
  trait ReflectionTestJSTrait extends js.Object

  class SomeParentClass
  class SomeChildClass extends SomeParentClass

  class ParentClassWhoseDataIsNotAccessedDirectly
  class ChildClassWhoseDataIsAccessedDirectly extends ParentClassWhoseDataIsNotAccessedDirectly

}
