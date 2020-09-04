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
import org.junit.Assume._
import org.junit.Test

import org.scalajs.testsuite.utils.JSAssert._
import org.scalajs.testsuite.utils.Platform
import org.scalajs.testsuite.utils.AssertThrows.assertThrows

class NonNativeJSTypeTest {
  import org.scalajs.testsuite.jsinterop.{NonNativeJSTypeTestSeparateRun => SepRun}
  import NonNativeJSTypeTest._

  @Test def minimal_definition(): Unit = {
    val obj = new Minimal
    assertEquals(js.typeOf(obj), "object")
    assertEquals(js.Object.keys(obj).toList, List[String]())
    assertEquals(obj.toString(), "[object Object]")
    assertNull(obj.getClass().asInstanceOf[js.Any])

    assertTrue((obj: Any).isInstanceOf[Minimal])
    assertTrue((obj: Any).isInstanceOf[js.Object])
    assertFalse((obj: Any).isInstanceOf[js.Error])
  }

  @Test def minimal_static_object_with_lazy_initialization(): Unit = {
    assertEquals(0, staticNonNativeObjectInitCount)
    val obj = StaticNonNativeObject
    assertEquals(1, staticNonNativeObjectInitCount)
    assertSame(obj, StaticNonNativeObject)
    assertEquals(1, staticNonNativeObjectInitCount)

    assertEquals(js.typeOf(obj), "object")
    assertEquals(js.Object.keys(obj).toList, List[String]())
    assertEquals(obj.toString(), "[object Object]")
    assertNull(obj.getClass().asInstanceOf[js.Any])

    assertFalse((obj: Any).isInstanceOf[Minimal])
    assertTrue((obj: Any).isInstanceOf[js.Object])
    assertFalse((obj: Any).isInstanceOf[js.Error])
  }

  @Test def simple_method(): Unit = {
    val obj = new SimpleMethod
    assertEquals(8, obj.foo(5))
    assertEquals(obj.bar("hello", 42), "hello42")

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.foo(5), 8)
    assertEquals(dyn.bar("hello", 42), "hello42")
  }

  @Test def static_object_with_simple_method(): Unit = {
    val obj = StaticObjectSimpleMethod
    assertEquals(8, obj.foo(5))
    assertEquals(obj.bar("hello", 42), "hello42")

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.foo(5), 8)
    assertEquals(dyn.bar("hello", 42), "hello42")
  }

  @Test def simple_field(): Unit = {
    val obj = new SimpleField
    assertEquals(js.Object.keys(obj).toList, List("x", "y"))
    assertEquals(5, obj.x)
    assertEquals(10, obj.y)
    assertEquals(15, obj.sum())

    obj.y = 3
    assertEquals(3, obj.y)
    assertEquals(8, obj.sum())

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 5)
    assertEquals(dyn.y, 3)
    assertEquals(dyn.sum(), 8)

    dyn.y = 89
    assertEquals(dyn.y, 89)
    assertEquals(89, obj.y)
    assertEquals(dyn.sum(), 94)
  }

  @Test def static_object_with_simple_field(): Unit = {
    val obj = StaticObjectSimpleField
    assertEquals(js.Object.keys(obj).toList, List("x", "y"))
    assertEquals(5, obj.x)
    assertEquals(10, obj.y)
    assertEquals(15, obj.sum())

    obj.y = 3
    assertEquals(3, obj.y)
    assertEquals(8, obj.sum())

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 5)
    assertEquals(dyn.y, 3)
    assertEquals(dyn.sum(), 8)

    dyn.y = 89
    assertEquals(dyn.y, 89)
    assertEquals(89, obj.y)
    assertEquals(dyn.sum(), 94)
  }

  @Test def simple_accessors(): Unit = {
    val obj = new SimpleAccessors
    assertEquals(js.Object.keys(obj).toList, List("x"))
    assertEquals(1, obj.x)

    assertEquals(2, obj.readPlus1)
    assertEquals(-1, obj.neg)
    obj.neg = 4
    assertEquals(-4, obj.x)
    assertEquals(4, obj.neg)
    assertEquals(-3, obj.readPlus1)

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, -4)

    assertEquals(dyn.readPlus1, -3)
    assertEquals(dyn.neg, 4)
    dyn.neg = -9
    assertEquals(dyn.x, 9)
    assertEquals(dyn.neg, -9)
    assertEquals(dyn.readPlus1, 10)
  }

  @Test def simple_constructor(): Unit = {
    val obj = new SimpleConstructor(5, 10)
    assertEquals(js.Object.keys(obj).toList, List("x", "y"))
    assertEquals(5, obj.x)
    assertEquals(10, obj.y)
    assertEquals(15, obj.sum())

    obj.y = 3
    assertEquals(3, obj.y)
    assertEquals(8, obj.sum())

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 5)
    assertEquals(dyn.y, 3)
    assertEquals(dyn.sum(), 8)

    dyn.y = 89
    assertEquals(dyn.y, 89)
    assertEquals(89, obj.y)
    assertEquals(dyn.sum(), 94)
  }

  @Test def simple_constructor_with_automatic_fields(): Unit = {
    val obj = new SimpleConstructorAutoFields(5, 10)
    assertEquals(js.Object.keys(obj).toList, List("x", "y"))
    assertEquals(5, obj.x)
    assertEquals(10, obj.y)
    assertEquals(15, obj.sum())

    obj.y = 3
    assertEquals(3, obj.y)
    assertEquals(8, obj.sum())

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 5)
    assertEquals(dyn.y, 3)
    assertEquals(dyn.sum(), 8)

    dyn.y = 89
    assertEquals(dyn.y, 89)
    assertEquals(89, obj.y)
    assertEquals(dyn.sum(), 94)
  }

  @Test def simple_constructor_with_param_accessors(): Unit = {
    val obj = new SimpleConstructorParamAccessors(5, 10)
    assertNotEquals(Array("x", "y"), js.Object.keys(obj).toArray)
    assertEquals(15, obj.sum())

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.sum(), 15)
  }

  @Test def constructor_with_param_name_clashes_issue_3933(): Unit = {
    val obj = new ConstructorWithParamNameClashes(1, 2, 3, 4, 5, 6)
    assertEquals(obj.allArgs, List(1, 2, 3, 4, 5, 6))
  }

  @Test def default_values_for_fields(): Unit = {
    val obj = new DefaultFieldValues
    assertEquals(0, obj.int)
    assertEquals(obj.bool, false)
    assertEquals(0, obj.char.toInt)
    assertNull(obj.string)
    assertJSUndefined(obj.unit)

    /* Value class fields are initialized to null, instead of a boxed
     * representation of the zero of their underlying types, as for a
     * Scala class.
     */
    assertNull(obj.asInstanceOf[js.Dynamic].valueClass)
  }

  @Test def lazy_vals(): Unit = {
    val obj1 = new LazyValFields()
    assertEquals(0, obj1.initCount)
    assertEquals(42, obj1.field)
    assertEquals(1, obj1.initCount)
    assertEquals(42, obj1.field)
    assertEquals(1, obj1.initCount)
    assertEquals(obj1.asInstanceOf[js.Dynamic].field, 42)
    assertEquals(1, obj1.initCount)
    assertEquals(42, (obj1: LazyValFieldsSuperTrait).field)
    assertEquals(1, obj1.initCount)

    val obj2 = new LazyValFields().asInstanceOf[js.Dynamic]
    assertEquals(obj2.initCount, 0)
    assertEquals(obj2.field, 42)
    assertEquals(obj2.initCount, 1)
    assertEquals(obj2.field, 42)
    assertEquals(obj2.initCount, 1)
    assertEquals(42, obj2.asInstanceOf[LazyValFields].field)
    assertEquals(obj2.initCount, 1)
    assertEquals(42, obj2.asInstanceOf[LazyValFieldsSuperTrait].field)
    assertEquals(obj2.initCount, 1)

    val obj3: LazyValFieldsSuperTrait = new LazyValFields()
    assertEquals(0, obj3.initCount)
    assertEquals(42, obj3.field)
    assertEquals(1, obj3.initCount)
    assertEquals(42, obj3.field)
    assertEquals(1, obj3.initCount)
    assertEquals(42, obj3.asInstanceOf[LazyValFields].field)
    assertEquals(1, obj3.initCount)
    assertEquals(obj3.asInstanceOf[js.Dynamic].field, 42)
    assertEquals(1, obj3.initCount)
  }

  @Test def override_lazy_vals(): Unit = {
    val obj1 = new OverrideLazyValFields()
    assertEquals(0, obj1.initCount)
    assertEquals(53, obj1.field)
    assertEquals(1, obj1.initCount)
    assertEquals(53, obj1.field)
    assertEquals(1, obj1.initCount)
    assertEquals(obj1.asInstanceOf[js.Dynamic].field, 53)
    assertEquals(1, obj1.initCount)
    assertEquals(53, (obj1: LazyValFieldsSuperTrait).field)
    assertEquals(1, obj1.initCount)
    assertEquals(53, (obj1: LazyValFields).field)
    assertEquals(1, obj1.initCount)

    val obj2 = new OverrideLazyValFields()
    assertEquals(0, obj2.initCount)
    assertEquals(53, (obj2: LazyValFields).field)
    assertEquals(1, obj2.initCount)
    assertEquals(53, obj2.field)
    assertEquals(1, obj2.initCount)
    assertEquals(53, obj2.field)
    assertEquals(1, obj2.initCount)
    assertEquals(obj2.asInstanceOf[js.Dynamic].field, 53)
    assertEquals(1, obj2.initCount)
    assertEquals(53, (obj2: LazyValFieldsSuperTrait).field)
    assertEquals(1, obj2.initCount)
  }

  @Test def nullingOutLazyValField_issue3422(): Unit = {
    assertEquals(new NullingOutLazyValFieldBug3422("foo").str, "foo")
  }

  @Test def simple_inherited_from_a_native_class(): Unit = {
    val obj = new SimpleInheritedFromNative(3, 5)
    assertEquals(3, obj.x)
    assertEquals(5, obj.y)
    assertEquals(6, obj.bar)
    assertTrue(obj.isInstanceOf[SimpleInheritedFromNative])
    assertTrue(obj.isInstanceOf[NativeParentClass])
  }

  @Test def double_underscore_in_member_names_issue_3784(): Unit = {
    class DoubleUnderscoreInMemberNames extends js.Object {
      val x__y: String = "xy"
      def foo__bar(x: Int): Int = x + 1
      def ba__bar: String = "babar"
    }

    val obj = new DoubleUnderscoreInMemberNames
    assertEquals(obj.x__y, "xy")
    assertEquals(6, obj.foo__bar(5))
    assertEquals(obj.ba__bar, "babar")
  }

  @Test def lambda_inside_a_method_issue_2220(): Unit = {
    class LambdaInsideMethod extends js.Object {
      def foo(): Int = {
        List(1, 2, 3).map(_ * 2).sum
      }
    }

    assertEquals(12, new LambdaInsideMethod().foo())
  }

  @Test def nested_inside_a_Scala_class(): Unit = {
    class OuterScalaClass(val x: Int) {
      class InnerJSClass(val y: Int) extends js.Object {
        def sum(z: Int): Int = x + y + z
      }
    }

    val outerObj = new OuterScalaClass(3)
    val obj = new outerObj.InnerJSClass(6)
    assertEquals(6, obj.y)
    assertEquals(20, obj.sum(11))
  }

  @Test def nested_inside_a_Scala_js_defined_JS_class(): Unit = {
    class OuterJSClass(val x: Int) extends js.Object {
      class InnerJSClass(val y: Int) extends js.Object {
        def sum(z: Int): Int = x + y + z
      }
    }

    val outerObj = new OuterJSClass(3)
    val obj = new outerObj.InnerJSClass(6)
    assertEquals(6, obj.y)
    assertEquals(20, obj.sum(11))
  }

  @Test def Scala_class_nested_inside_a_Scala_js_defined_JS_class(): Unit = {
    class OuterJSClass(val x: Int) extends js.Object {
      class InnerScalaClass(val y: Int) {
        def sum(z: Int): Int = x + y + z
      }
    }

    val outerObj = new OuterJSClass(3)
    val obj = new outerObj.InnerScalaClass(6)
    assertEquals(6, obj.y)
    assertEquals(20, obj.sum(11))
  }

  @Test def Scala_object_nested_inside_a_Scala_js_defined_JS_class(): Unit = {
    class Foo extends js.Object {
      var innerInitCount: Int = _

      object Inner {
        innerInitCount += 1
      }
    }

    val foo = new Foo
    assertEquals(0, foo.innerInitCount)
    val inner1 = foo.Inner
    assertEquals(1, foo.innerInitCount)
    assertTrue((foo.Inner: AnyRef) eq inner1)
    assertEquals(1, foo.innerInitCount)

    val dyn = (new Foo).asInstanceOf[js.Dynamic]
    assertEquals(dyn.innerInitCount, 0)
    val inner2 = dyn.Inner
    assertEquals(dyn.innerInitCount, 1)
    assertTrue((dyn.Inner: AnyRef) eq inner2)
    assertEquals(dyn.innerInitCount, 1)

    assertFalse((inner2: AnyRef) eq inner1)
  }

  // #2772
  @Test def Scala_object_nested_inside_a_Scala_js_defined_JS_class_JSName(): Unit = {
    class Foo extends js.Object {
      var innerInitCount: Int = _

      @JSName("innerName")
      object Inner {
        innerInitCount += 1
      }
    }

    val foo = new Foo
    assertEquals(0, foo.innerInitCount)
    val inner1 = foo.Inner
    assertEquals(1, foo.innerInitCount)
    assertTrue((foo.Inner: AnyRef) eq inner1)
    assertEquals(1, foo.innerInitCount)

    val dyn = (new Foo).asInstanceOf[js.Dynamic]
    assertEquals(dyn.innerInitCount, 0)
    val inner2 = dyn.innerName
    assertEquals(dyn.innerInitCount, 1)
    assertTrue((dyn.innerName: AnyRef) eq inner2)
    assertEquals(dyn.innerInitCount, 1)

    assertFalse((inner2: AnyRef) eq inner1)
  }

  @Test def anonymous_class_with_captures(): Unit = {
    val x = (() => 5)()
    val obj = new js.Object {
      val y = 10
      def sum(z: Int): Int = x + y + z
    }

    val dyn = obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.y, 10)
    assertEquals(dyn.sum(11), 26)
  }

  @Test def anonymous_class_has_no_own_prototype(): Unit = {
    val obj = new js.Object {
      val x = 1
    }

    assertEquals(obj.asInstanceOf[js.Dynamic].x, 1)
    assertSame(js.Object.getPrototypeOf(obj),
        js.constructorOf[js.Object].prototype)
  }

  @Test def local_class_has_own_prototype(): Unit = {
    class Local extends js.Object {
      val x = 1
    }

    val obj = new Local

    assertEquals(obj.asInstanceOf[js.Dynamic].x, 1)

    val prototype = js.Object.getPrototypeOf(obj)

    assertNotSame(prototype, js.constructorOf[js.Object].prototype)
    assertSame(prototype, js.constructorOf[Local].prototype)
  }

  @Test def anonymous_class_non_trivial_supertype(): Unit = {
    val obj = new SimpleConstructor(1, 2) {
      val z = sum()
    }

    assertEquals(obj.asInstanceOf[js.Dynamic].z, 3)
  }

  @Test def anonymous_class_using_own_method_in_ctor(): Unit = {
    val obj = new js.Object {
      val y = inc(0)
      def inc(x: Int) = x + 1
    }

    assertEquals(obj.asInstanceOf[js.Dynamic].y, 1)
  }

  @Test def anonymous_class_uninitialized_fields(): Unit = {
    val obj = new js.Object {
      var x: String = _
      var y: Int = _
    }

    assertNull(obj.asInstanceOf[js.Dynamic].x)
    assertEquals(obj.asInstanceOf[js.Dynamic].y, 0)
  }

  @Test def anonymous_class_field_init_order(): Unit = {
    val obj = new js.Object {
      val x = getY
      val y = "Hello World"

      private def getY: String = y
    }.asInstanceOf[js.Dynamic]

    assertNull(obj.x)
    assertEquals(obj.y, "Hello World")
  }

  @Test def anonymous_class_dependent_fields(): Unit = {
    val obj = new js.Object {
      val x = 1
      val y = x + 1
    }

    assertEquals(obj.asInstanceOf[js.Dynamic].y, 2)
  }

  @Test def anonymous_class_use_this_in_ctor(): Unit = {
    var obj0: js.Object = null
    val obj1 = new js.Object {
      obj0 = this
    }

    assertSame(obj0, obj1)
  }

  @Test def nested_anonymous_classes(): Unit = {
    val outer = new js.Object {
      private var _x = 1
      def x = _x

      val inner = new js.Object {
        def inc() = _x += 1
      }
    }.asInstanceOf[js.Dynamic]

    val inner = outer.inner
    assertEquals(outer.x, 1)
    inner.inc()
    assertEquals(outer.x, 2)
  }

  @Test def nested_anonymous_classes_and_lambdas(): Unit = {
    def call(f: Int => js.Any) = f(1)

    // Also check that f's capture is properly transformed.
    val obj = call(x => new js.Object { val f: js.Any = (y: Int) => x + y })
    val res = obj.asInstanceOf[js.Dynamic].f(3)
    assertEquals(res, 4)

    assertEquals(call(x => x), 1)
  }

  @Test def anonymous_classes_private_fields_are_not_visible_issue2748(): Unit = {
    trait TheOuter extends js.Object {
      val id: String
      val paint: js.UndefOr[TheInner] = js.undefined
    }

    trait TheInner extends js.Object {
      val options: js.UndefOr[String] = js.undefined
    }

    def someValue = "some-value"

    val pcFn = someValue

    val r0 = new TheOuter {
      override val id: String = "some-" + pcFn
      override val paint: js.UndefOr[TheInner] = {
        new TheInner {
          override val options: js.UndefOr[String] = "{" + pcFn + "}"
        }
      }
    }

    assertEquals(
        js.JSON.stringify(r0),
        "{\"id\":\"some-some-value\",\"paint\":{\"options\":\"{some-value}\"}}")
  }

  @Test def local_object_is_lazy(): Unit = {
    var initCount: Int = 0

    object Obj extends js.Object {
      initCount += 1
    }

    assertEquals(0, initCount)
    val obj = Obj
    import js.DynamicImplicits.truthValue
    assertTrue(obj.asInstanceOf[js.Dynamic])
    assertEquals(1, initCount)
    assertSame(obj, Obj)
    assertEquals(1, initCount)
  }

  @Test def local_object_with_captures(): Unit = {
    val x = (() => 5)()

    object Obj extends js.Object {
      val y = 10
      def sum(z: Int): Int = x + y + z
    }

    assertEquals(10, Obj.y)
    assertEquals(26, Obj.sum(11))

    val dyn = Obj.asInstanceOf[js.Dynamic]
    assertEquals(dyn.y, 10)
    assertEquals(dyn.sum(11), 26)
  }

  @Test def object_in_Scala_js_defined_JS_class(): Unit = {
    class Foo extends js.Object {
      var innerInitCount: Int = _

      object Inner extends js.Object {
        innerInitCount += 1
      }
    }

    val foo = new Foo
    assertEquals(0, foo.innerInitCount)
    val inner1 = foo.Inner
    assertEquals(1, foo.innerInitCount)
    assertSame(inner1, foo.Inner)
    assertEquals(1, foo.innerInitCount)

    val dyn = (new Foo).asInstanceOf[js.Dynamic]
    assertEquals(dyn.innerInitCount, 0)
    val inner2 = dyn.Inner
    assertEquals(dyn.innerInitCount, 1)
    assertSame(inner2, dyn.Inner)
    assertEquals(dyn.innerInitCount, 1)

    assertNotSame(inner1, inner2)
  }

  @Test def local_defs_must_not_be_exposed(): Unit = {
    class LocalDefsMustNotBeExposed extends js.Object {
      def foo(): String = {
        def bar(): String = "hello"
        bar()
      }
    }

    val obj = new LocalDefsMustNotBeExposed
    assertFalse(js.Object.properties(obj).exists(_.contains("bar")))
  }

  @Test def local_objects_must_not_be_exposed(): Unit = {
    class LocalObjectsMustNotBeExposed extends js.Object {
      def foo(): String = {
        object Bar
        Bar.toString()
      }
    }

    val obj = new LocalObjectsMustNotBeExposed
    assertFalse(js.Object.properties(obj).exists(_.contains("Bar")))
  }

  @Test def local_defs_with_captures_issue_1975(): Unit = {
    class LocalDefsWithCaptures extends js.Object {
      def foo(suffix: String): String = {
        def bar(): String = "hello " + suffix
        bar()
      }
    }

    val obj = new LocalDefsWithCaptures
    assertEquals(obj.foo("world"), "hello world")
  }

  @Test def methods_with_explicit_name(): Unit = {
    class MethodsWithExplicitName extends js.Object {
      @JSName("theAnswer")
      def bar(): Int = 42
      @JSName("doubleTheParam")
      def double(x: Int): Int = x*2
    }

    val foo = new MethodsWithExplicitName
    assertEquals(42, foo.bar())
    assertEquals(6, foo.double(3))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertJSUndefined(dyn.bar)
    assertEquals("function", js.typeOf(dyn.theAnswer))
    assertEquals(dyn.theAnswer(), 42)
    assertEquals(dyn.doubleTheParam(3), 6)
  }

  @Test def methods_with_constant_folded_name(): Unit = {
    class MethodsWithConstantFoldedName extends js.Object {
      @JSName(JSNameHolder.MethodName)
      def bar(): Int = 42
    }

    val foo = new MethodsWithConstantFoldedName
    assertEquals(42, foo.bar())

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertJSUndefined(dyn.bar)
    assertEquals(dyn.myMethod(), 42)
  }

  @Test def protected_methods(): Unit = {
    class ProtectedMethods extends js.Object {
      protected def bar(): Int = 42

      protected[testsuite] def foo(): Int = 100
    }

    val foo = new ProtectedMethods
    assertEquals(100, foo.foo())

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.bar))
    assertEquals(dyn.bar(), 42)
    assertEquals("function", js.typeOf(dyn.foo))
    assertEquals(dyn.foo(), 100)
  }

  @Test def readonly_properties(): Unit = {
    // Named classes
    class Foo extends js.Object {
      def bar: Int = 1
    }

    val x: js.Dynamic = (new Foo()).asInstanceOf[js.Dynamic]
    assertThrows(classOf[js.JavaScriptException], {
      x.bar = 2
    })

    // Anonymous classes
    val y = new js.Object {
      def bar: Int = 1
    }.asInstanceOf[js.Dynamic]

    assertThrows(classOf[js.JavaScriptException], {
      y.bar = 2
    })
  }

  @Test def properties_are_not_enumerable(): Unit = {
    // Named classes
    class Foo extends js.Object {
      def myProp: Int = 1
    }

    val x: js.Any = (new Foo()).asInstanceOf[js.Any]
    assertFalse(js.Object.properties(x).contains("myProp"))

    // Anonymous classes
    val y = new js.Object {
      def myProp: Int = 1
    }

    assertFalse(js.Object.properties(y).contains("myProp"))
  }

  @Test def properties_are_configurable(): Unit = {
    // Named classes
    class Foo extends js.Object {
      def myProp: Int = 1
    }

    // Delete property from prototype.
    val prototype = js.constructorOf[Foo].prototype
    js.special.delete(prototype, "myProp")

    // Check it is actually gone.
    assertTrue(js.isUndefined((new Foo()).asInstanceOf[js.Dynamic].myProp))

    // Anonymous classes
    val y = new js.Object {
      def myProp: Int = 1
    }

    // The property should be on the instance itself.
    assertTrue(y.hasOwnProperty("myProp"))
    js.special.delete(y, "myProp")
    assertTrue(js.isUndefined(y.asInstanceOf[js.Dynamic].myProp))
    assertFalse(y.hasOwnProperty("myProp"))
  }

  @Test def properties_with_explicit_name(): Unit = {
    class PropertiesWithExplicitName extends js.Object {
      private[this] var myY: String = "hello"
      @JSName("answer")
      val answerScala: Int = 42
      @JSName("x")
      var xScala: Int = 3
      @JSName("doubleX")
      def doubleXScala: Int = xScala*2
      @JSName("y")
      def yGetter: String = myY + " get"
      @JSName("y")
      def ySetter_=(v: String): Unit = myY = v + " set"
    }

    val foo = new PropertiesWithExplicitName
    assertEquals(42, foo.answerScala)
    assertEquals(3, foo.xScala)
    assertEquals(6, foo.doubleXScala)
    foo.xScala = 23
    assertEquals(23, foo.xScala)
    assertEquals(46, foo.doubleXScala)
    assertEquals(foo.yGetter, "hello get")
    foo.ySetter_=("world")
    assertEquals(foo.yGetter, "world set get")

    val dyn = (new PropertiesWithExplicitName).asInstanceOf[js.Dynamic]
    assertJSUndefined(dyn.answerScala)
    assertEquals("number", js.typeOf(dyn.answer))
    assertEquals(dyn.answer, 42)
    assertEquals(dyn.x, 3)
    assertEquals(dyn.doubleX, 6)
    dyn.x = 23
    assertEquals(dyn.x, 23)
    assertEquals(dyn.doubleX, 46)
    assertEquals(dyn.y, "hello get")
    dyn.y = "world"
    assertEquals(dyn.y, "world set get")
  }

  @Test def protected_properties(): Unit = {
    class ProtectedProperties extends js.Object {
      protected val x: Int = 42
      protected[testsuite] val y: Int = 43
    }

    val foo = new ProtectedProperties
    assertEquals(43, foo.y)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 42)
    assertEquals(dyn.y, 43)
  }

  @Test def simple_overloaded_methods(): Unit = {
    class SimpleOverloadedMethods extends js.Object {
      def foo(): Int = 42
      def foo(x: Int): Int = x*2
    }

    val foo = new SimpleOverloadedMethods
    assertEquals(42, foo.foo())
    assertEquals(6, foo.foo(3))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.foo))
    assertEquals(dyn.foo(), 42)
    assertEquals(dyn.foo(3), 6)
  }

  @Test def simple_overloaded_methods_anon_js_class_issue_3054(): Unit = {
    trait SimpleOverloadedMethodsAnonJSClass extends js.Object {
      def foo(): Int
      def foo(x: Int): Int
    }

    val foo = new SimpleOverloadedMethodsAnonJSClass {
      def foo(): Int = 42
      def foo(x: Int): Int = x * 2
    }
    assertEquals(42, foo.foo())
    assertEquals(6, foo.foo(3))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.foo))
    assertEquals(dyn.foo(), 42)
    assertEquals(dyn.foo(3), 6)
  }

  @Test def renamed_overloaded_methods(): Unit = {
    class RenamedOverloadedMethods extends js.Object {
      @JSName("foobar")
      def foo(): Int = 42
      @JSName("foobar")
      def bar(x: Int): Int = x*2
    }

    val foo = new RenamedOverloadedMethods
    assertEquals(42, foo.foo())
    assertEquals(6, foo.bar(3))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.foobar))
    assertEquals(dyn.foobar(), 42)
    assertEquals(dyn.foobar(3), 6)
  }

  @Test def overloaded_methods_with_varargs(): Unit = {
    class OverloadedMethodsWithVarargs extends js.Object {
      def foo(x: Int): Int = x * 2
      def foo(strs: String*): Int = strs.foldLeft(0)(_ + _.length)
    }

    val foo = new OverloadedMethodsWithVarargs
    assertEquals(42, foo.foo(21))
    assertEquals(0, foo.foo())
    assertEquals(3, foo.foo("bar"))
    assertEquals(8, foo.foo("bar", "babar"))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.foo))
    assertEquals(dyn.foo(21), 42)
    assertEquals(dyn.foo(), 0)
    assertEquals(dyn.foo("bar"), 3)
    assertEquals(dyn.foo("bar", "babar"), 8)
  }

  @Test def overloaded_methods_with_varargs_anon_js_class_issue_3054(): Unit = {
    trait OverloadedMethodsWithVarargsAnonJSClass extends js.Object {
      def foo(x: Int): Int
      def foo(strs: String*): Int
    }

    val foo = new OverloadedMethodsWithVarargsAnonJSClass {
      def foo(x: Int): Int = x * 2
      def foo(strs: String*): Int = strs.foldLeft(0)(_ + _.length)
    }
    assertEquals(42, foo.foo(21))
    assertEquals(0, foo.foo())
    assertEquals(3, foo.foo("bar"))
    assertEquals(8, foo.foo("bar", "babar"))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals("function", js.typeOf(dyn.foo))
    assertEquals(dyn.foo(21), 42)
    assertEquals(dyn.foo(), 0)
    assertEquals(dyn.foo("bar"), 3)
    assertEquals(dyn.foo("bar", "babar"), 8)
  }

  @Test def overloaded_constructors_num_parameters_resolution(): Unit = {
    assertEquals(1, new OverloadedConstructorParamNumber(1).foo)
    assertEquals(3, new OverloadedConstructorParamNumber(1, 2).foo)
  }

  @Test def overloaded_constructors_parameter_type_resolution(): Unit = {
    assertEquals(1, new OverloadedConstructorParamType(1).foo)
    assertEquals(3, new OverloadedConstructorParamType("abc").foo)
  }

  @Test def overloaded_constructors_with_captured_parameters(): Unit = {
    class OverloadedConstructorWithOuterContextOnly(val x: Int) extends js.Object {
      def this(y: String) = this(y.length)
    }

    val z = (() => 5)()
    class OverloadedConstructorWithValCapture(val x: Int) extends js.Object {
      def this(y: String) = this(z)
    }

    assertEquals(1, new OverloadedConstructorWithOuterContextOnly(1).x)
    assertEquals(3, new OverloadedConstructorWithOuterContextOnly("abc").x)

    assertEquals(1, new OverloadedConstructorWithValCapture(1).x)
    assertEquals(5, new OverloadedConstructorWithValCapture("abc").x)
  }

  @Test def overloaded_constructors_with_super_class(): Unit = {
    class OverloadedConstructorSup(val x: Int) extends js.Object {
      def this(y: String) = this(y.length)
    }
    class OverloadedConstructorSub(x: Int)
        extends OverloadedConstructorSup(3 * x) {
      def this(y: String) = this(2 * y.length)
    }
    assertEquals(1, new OverloadedConstructorSup(1).x)
    assertEquals(3, new OverloadedConstructorSup("abc").x)

    assertEquals(9, new OverloadedConstructorSub(3).x)
    assertEquals(12, new OverloadedConstructorSub("ab").x)
  }

  @Test def overloaded_constructors_with_repeated_parameters(): Unit = {
    class OverloadedConstructorWithRepeatedParameters(xs: Int*)
        extends js.Object {
      def this(y: String, ys: String*) = this(y.length +: ys.map(_.length): _*)
      def sum: Int = xs.sum
    }

    assertEquals(0, new OverloadedConstructorWithRepeatedParameters().sum)
    assertEquals(1, new OverloadedConstructorWithRepeatedParameters(1).sum)
    assertEquals(3, new OverloadedConstructorWithRepeatedParameters(1, 2).sum)
    assertEquals(7, new OverloadedConstructorWithRepeatedParameters(1, 2, 4).sum)

    assertEquals(3, new OverloadedConstructorWithRepeatedParameters("abc").sum)
    assertEquals(3, new OverloadedConstructorWithRepeatedParameters("ab", "c").sum)
    assertEquals(3, new OverloadedConstructorWithRepeatedParameters("a", "b", "c").sum)
  }

  @Test def overloaded_constructors_complex_resolution(): Unit = {
    val bazPrim = new OverloadedConstructorComplex(1, 2)
    assertEquals(1, bazPrim.foo)
    assertEquals(2, bazPrim.bar)

    val baz1 = new OverloadedConstructorComplex()
    assertEquals(5, baz1.foo)
    assertEquals(6, baz1.bar)

    val baz2 = new OverloadedConstructorComplex(3)
    assertEquals(3, baz2.foo)
    assertEquals(3, baz2.bar)

    val baz3 = new OverloadedConstructorComplex(7, 8, 9)
    assertEquals(7, baz3.foo)
    assertEquals(9, baz3.bar)

    val baz4 = new OverloadedConstructorComplex("abc")
    assertEquals(3, baz4.foo)
    assertEquals(3, baz4.bar)

    val baz5 = new OverloadedConstructorComplex("abc", 10)
    assertEquals(3, baz5.foo)
    assertEquals(10, baz5.bar)

    val baz6 = new OverloadedConstructorComplex(11, "abc")
    assertEquals(11, baz6.foo)
    assertEquals(3, baz6.bar)

    val baz7 = new OverloadedConstructorComplex(1, 2, 4, 8)
    assertEquals(3, baz7.foo)
    assertEquals(4, baz7.bar)

    val baz8 = new OverloadedConstructorComplex("abc", "abcd")
    assertEquals(3, baz8.foo)
    assertEquals(4, baz8.bar)

    val baz9 = new OverloadedConstructorComplex("abc", "abcd", "zx")
    assertEquals(5, baz9.foo)
    assertEquals(4, baz9.bar)

    val baz10 = new OverloadedConstructorComplex("abc", "abcd", "zx", "tfd")
    assertEquals(5, baz10.foo)
    assertEquals(7, baz10.bar)
  }

  @Test def polytype_nullary_method_issue_2445(): Unit = {
    class PolyTypeNullaryMethod extends js.Object {
      def emptyArray[T]: js.Array[T] = js.Array()
    }

    val obj = new PolyTypeNullaryMethod
    val a = obj.emptyArray[Int]
    assertTrue((a: Any).isInstanceOf[js.Array[_]])
    assertEquals(0, a.length)

    val dyn = obj.asInstanceOf[js.Dynamic]
    val b = dyn.emptyArray
    assertTrue((b: Any).isInstanceOf[js.Array[_]])
    assertEquals(b.length, 0)
  }

  @Test def default_parameters(): Unit = {
    class DefaultParameters extends js.Object {
      def bar(x: Int, y: Int = 1): Int = x + y
      def dependent(x: Int)(y: Int = x + 1): Int = x + y

      def foobar(x: Int): Int = bar(x)
    }

    object DefaultParametersMod extends js.Object {
      def bar(x: Int, y: Int = 1): Int = x + y
      def dependent(x: Int)(y: Int = x + 1): Int = x + y

      def foobar(x: Int): Int = bar(x)
    }

    val foo = new DefaultParameters
    assertEquals(9, foo.bar(4, 5))
    assertEquals(5, foo.bar(4))
    assertEquals(4, foo.foobar(3))
    assertEquals(9, foo.dependent(4)(5))
    assertEquals(17, foo.dependent(8)())

    assertEquals(9, DefaultParametersMod.bar(4, 5))
    assertEquals(5, DefaultParametersMod.bar(4))
    assertEquals(4, DefaultParametersMod.foobar(3))
    assertEquals(9, DefaultParametersMod.dependent(4)(5))
    assertEquals(17, DefaultParametersMod.dependent(8)())

    def testDyn(dyn: js.Dynamic): Unit = {
      assertEquals(dyn.bar(4, 5), 9)
      assertEquals(dyn.bar(4), 5)
      assertEquals(dyn.foobar(3), 4)
      assertEquals(dyn.dependent(4, 5), 9)
      assertEquals(dyn.dependent(8), 17)
    }
    testDyn(foo.asInstanceOf[js.Dynamic])
    testDyn(DefaultParametersMod.asInstanceOf[js.Dynamic])
  }

  @Test def override_default_parameters(): Unit = {
    class OverrideDefaultParametersParent extends js.Object {
      def bar(x: Int, y: Int = 1): Int = x + y
      def dependent(x: Int)(y: Int = x + 1): Int = x + y

      def foobar(x: Int): Int = bar(x)
    }

    class OverrideDefaultParametersChild
        extends OverrideDefaultParametersParent {
      override def bar(x: Int, y: Int = 10): Int = super.bar(x, y)
      override def dependent(x: Int)(y: Int = x * 2): Int = x + y
    }

    val foo = new OverrideDefaultParametersChild
    assertEquals(9, foo.bar(4, 5))
    assertEquals(14, foo.bar(4))
    assertEquals(13, foo.foobar(3))
    assertEquals(9, foo.dependent(4)(5))
    assertEquals(24, foo.dependent(8)())

    val parent: OverrideDefaultParametersParent = foo
    assertEquals(9, parent.bar(4, 5))
    assertEquals(14, parent.bar(4))
    assertEquals(13, parent.foobar(3))
    assertEquals(9, parent.dependent(4)(5))
    assertEquals(24, parent.dependent(8)())

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.bar(4, 5), 9)
    assertEquals(dyn.bar(4), 14)
    assertEquals(dyn.foobar(3), 13)
    assertEquals(dyn.dependent(4, 5), 9)
    assertEquals(dyn.dependent(8), 24)
  }

  @Test def override_method_with_default_parameters_without_new_default(): Unit = {
    class OverrideDefaultParametersWithoutDefaultParent extends js.Object {
      def bar(x: Int, y: Int = 1): Int = x + y
      def dependent(x: Int)(y: Int = x + 1): Int = x + y

      def foobar(x: Int): Int = bar(x)
    }

    class OverrideDefaultParametersWithoutDefaultChild
        extends OverrideDefaultParametersWithoutDefaultParent {
      override def bar(x: Int, y: Int): Int = x - y
      override def dependent(x: Int)(y: Int): Int = x - y
    }

    val foo = new OverrideDefaultParametersWithoutDefaultChild
    assertEquals(-1, foo.bar(4, 5))
    assertEquals(3, foo.bar(4))
    assertEquals(2, foo.foobar(3))
    assertEquals(-4, foo.dependent(4)(8))
    assertEquals(-1, foo.dependent(8)())

    val parent: OverrideDefaultParametersWithoutDefaultParent = foo
    assertEquals(-1, parent.bar(4, 5))
    assertEquals(3, parent.bar(4))
    assertEquals(2, parent.foobar(3))
    assertEquals(-4, parent.dependent(4)(8))
    assertEquals(-1, parent.dependent(8)())

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.bar(4, 5), -1)
    assertEquals(dyn.bar(4), 3)
    assertEquals(dyn.foobar(3), 2)
    assertEquals(dyn.dependent(4, 8), -4)
    assertEquals(dyn.dependent(8), -1)
  }

  @Test def `constructors_with_default_parameters_(NonNative/-)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNonNativeNone().foo)
    assertEquals(1, new ConstructorDefaultParamJSNonNativeNone(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNonNativeNone(5).foo)
  }

  @Test def `constructors_with_default_parameters_(NonNative/NonNative)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNonNativeJSNonNative().foo)
    assertEquals(1, new ConstructorDefaultParamJSNonNativeJSNonNative(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNonNativeJSNonNative(5).foo)
  }

  @Test def `constructors_with_default_parameters_(NonNative/Scala)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNonNativeScala().foo)
    assertEquals(1, new ConstructorDefaultParamJSNonNativeScala(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNonNativeScala(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Scala/NonNative)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamScalaJSNonNative().foo)
    assertEquals(1, new ConstructorDefaultParamScalaJSNonNative(1).foo)
    assertEquals(5, new ConstructorDefaultParamScalaJSNonNative(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Native/-)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNativeNone().foo)
    assertEquals(1, new ConstructorDefaultParamJSNativeNone(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNativeNone(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Native/Scala)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNativeScala().foo)
    assertEquals(1, new ConstructorDefaultParamJSNativeScala(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNativeScala(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Native/NonNative)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNativeJSNonNative().foo)
    assertEquals(1, new ConstructorDefaultParamJSNativeJSNonNative(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNativeJSNonNative(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Native/Native)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamJSNativeJSNative().foo)
    assertEquals(1, new ConstructorDefaultParamJSNativeJSNative(1).foo)
    assertEquals(5, new ConstructorDefaultParamJSNativeJSNative(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Scala/Scala)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamScalaScala().foo)
    assertEquals(1, new ConstructorDefaultParamScalaScala(1).foo)
    assertEquals(5, new ConstructorDefaultParamScalaScala(5).foo)
  }

  @Test def `constructors_with_default_parameters_(Scala/-)`(): Unit = {
    assertEquals(-1, new ConstructorDefaultParamScalaNone().foo)
    assertEquals(1, new ConstructorDefaultParamScalaNone(1).foo)
    assertEquals(5, new ConstructorDefaultParamScalaNone(5).foo)
  }

  @Test def constructors_with_default_parameters_in_multi_param_lists(): Unit = {
    val foo1 = new ConstructorDefaultParamMultiParamList(5)("foobar")
    assertEquals(5, foo1.default)
    assertEquals(foo1.title, "foobar")
    assertEquals(foo1.description, "5")

    val foo2 = new ConstructorDefaultParamMultiParamList(56)("babar", "desc")
    assertEquals(56, foo2.default)
    assertEquals(foo2.title, "babar")
    assertEquals(foo2.description, "desc")
  }

  @Test def constructors_with_default_parameters_in_multi_param_lists_and_overloading(): Unit = {
    val foo1 = new ConstructorDefaultParamMultiParamListWithOverloading(5)(
        "foobar")
    assertEquals(5, foo1.default)
    assertEquals(foo1.title, "foobar")
    assertEquals(foo1.description, "5")

    val foo2 = new ConstructorDefaultParamMultiParamListWithOverloading(56)(
        "babar", "desc")
    assertEquals(56, foo2.default)
    assertEquals(foo2.title, "babar")
    assertEquals(foo2.description, "desc")

    val foo3 = new ConstructorDefaultParamMultiParamListWithOverloading('A')
    assertEquals(65, foo3.default)
    assertEquals(foo3.title, "char")
    assertEquals(foo3.description, "a char")

    val foo4 = new ConstructorDefaultParamMultiParamListWithOverloading(123, 456)
    assertEquals(123, foo4.default)
    assertEquals(foo4.title, "456")
    assertEquals(foo4.description, js.undefined)
  }

  @Test def `call_super_constructor_with_:__*`(): Unit = {
    class CallSuperCtorWithSpread(x: Int, y: Int, z: Int)
        extends NativeParentClassWithVarargs(x, Seq(y, z): _*)

    val foo = new CallSuperCtorWithSpread(4, 8, 23)
    assertEquals(4, foo.x)
    assertJSArrayEquals(js.Array(8, 23), foo.args)

    val dyn = foo.asInstanceOf[js.Dynamic]
    /* Dark magic is at play here: everywhere else in this compilation unit,
     * it's fine to do `assertEquals(4, dyn.x)` (for example, in the test
     * `override_native_method` below), but right here, it causes scalac to die
     * with a completely nonsensical compile error:
     *
     * > applyDynamic does not support passing a vararg parameter
     *
     * Extracting it in a separate `val` works around it.
     */
    val dynx = dyn.x
    assertEquals(dynx, 4)
    val args = dyn.args.asInstanceOf[js.Array[Int]]
    assertJSArrayEquals(js.Array(8, 23), args)
  }

  @Test def override_native_method(): Unit = {
    class OverrideNativeMethod extends NativeParentClass(3) {
      override def foo(s: String): String = s + s + x
    }

    val foo = new OverrideNativeMethod
    assertEquals(3, foo.x)
    assertEquals(foo.foo("hello"), "hellohello3")

    val parent: NativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(parent.foo("hello"), "hellohello3")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.foo("hello"), "hellohello3")
  }

  @Test def override_non_native_method(): Unit = {
    class OverrideNonNativeMethod extends NonNativeParentClass(3) {
      override def foo(s: String): String = s + s + x
    }

    val foo = new OverrideNonNativeMethod
    assertEquals(3, foo.x)
    assertEquals(foo.foo("hello"), "hellohello3")

    val parent: NonNativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(parent.foo("hello"), "hellohello3")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.foo("hello"), "hellohello3")
  }

  @Test def override_non_native_method_with_separate_compilation(): Unit = {
    val foo = new SepRun.SimpleChildClass
    assertEquals(6, foo.foo(3))

    val fooParent: SepRun.SimpleParentClass = foo
    assertEquals(6, fooParent.foo(3))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(6, foo.foo(3))
  }

  @Test def override_native_method_and_call_super(): Unit = {
    class OverrideNativeMethodSuperCall extends NativeParentClass(3) {
      override def foo(s: String): String = super.foo("bar") + s
    }

    val foo = new OverrideNativeMethodSuperCall
    assertEquals(3, foo.x)
    assertEquals(foo.foo("hello"), "bar3hello")

    val parent: NativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(parent.foo("hello"), "bar3hello")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.foo("hello"), "bar3hello")
  }

  @Test def override_non_native_method_and_call_super(): Unit = {
    class OverrideNonNativeMethodSuperCall extends NonNativeParentClass(3) {
      override def foo(s: String): String = super.foo("bar") + s
    }

    val foo = new OverrideNonNativeMethodSuperCall
    assertEquals(3, foo.x)
    assertEquals(foo.foo("hello"), "bar3hello")

    val parent: NonNativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(parent.foo("hello"), "bar3hello")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.foo("hello"), "bar3hello")
  }

  @Test def super_method_call_in_anon_JS_class_issue_3055(): Unit = {
    class Foo extends js.Object {
      def bar(msg: String): String = "super: " + msg
    }

    val foo = new Foo {
      override def bar(msg: String): String = super.bar("foo: " + msg)
    }

    assertEquals(foo.bar("foobar"), "super: foo: foobar")
  }

  @Test def override_native_val(): Unit = {
    class OverrideNativeVal extends NativeParentClass(3) {
      override val x: Int = 42
    }

    val foo = new OverrideNativeVal
    assertEquals(42, foo.x)
    assertEquals(84, foo.bar)
    assertEquals(foo.foo("hello"), "hello42")

    val parent: NativeParentClass = foo
    assertEquals(42, parent.x)
    assertEquals(84, parent.bar)
    assertEquals(parent.foo("hello"), "hello42")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 42)
    assertEquals(dyn.bar, 84)
    assertEquals(dyn.foo("hello"), "hello42")
  }

  @Test def override_non_native_val(): Unit = {
    class OverrideNonNativeVal extends NonNativeParentClass(3) {
      override val x: Int = 42
    }

    val foo = new OverrideNonNativeVal
    assertEquals(42, foo.x)
    assertEquals(84, foo.bar)
    assertEquals(foo.foo("hello"), "hello42")

    val parent: NonNativeParentClass = foo
    assertEquals(42, parent.x)
    assertEquals(84, parent.bar)
    assertEquals(parent.foo("hello"), "hello42")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 42)
    assertEquals(dyn.bar, 84)
    assertEquals(dyn.foo("hello"), "hello42")
  }

  @Test def override_native_getter(): Unit = {
    class OverrideNativeGetter extends NativeParentClass(3) {
      override def bar: Int = x * 3
    }

    val foo = new OverrideNativeGetter
    assertEquals(3, foo.x)
    assertEquals(9, foo.bar)

    val parent: NativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(9, parent.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.bar, 9)
  }

  @Test def override_non_native_getter(): Unit = {
    class OverrideNonNativeGetter extends NonNativeParentClass(3) {
      override def bar: Int = x * 3
    }

    val foo = new OverrideNonNativeGetter
    assertEquals(3, foo.x)
    assertEquals(9, foo.bar)

    val parent: NonNativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(9, parent.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.bar, 9)
  }

  @Test def override_native_getter_with_val(): Unit = {
    class OverrideNativeGetterWithVal extends NativeParentClass(3) {
      override val bar: Int = 1
    }

    val foo = new OverrideNativeGetterWithVal
    assertEquals(3, foo.x)
    assertEquals(1, foo.bar)

    val parent: NativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(1, parent.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.bar, 1)
  }

  @Test def override_non_native_getter_with_val(): Unit = {
    class OverrideNonNativeGetterWithVal extends NonNativeParentClass(3) {
      override val bar: Int = 1
    }

    val foo = new OverrideNonNativeGetterWithVal
    assertEquals(3, foo.x)
    assertEquals(1, foo.bar)

    val parent: NonNativeParentClass = foo
    assertEquals(3, parent.x)
    assertEquals(1, parent.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 3)
    assertEquals(dyn.bar, 1)
  }

  @Test def override_getter_with_super(): Unit = {
    class OverrideGetterSuperParent extends js.Object {
      def bar: Int = 43
    }
    class OverrideGetterSuperChild extends OverrideGetterSuperParent {
      override def bar: Int = super.bar * 3
    }

    val foo = new OverrideGetterSuperChild
    assertEquals(129, foo.bar)

    val parent: OverrideGetterSuperParent = foo
    assertEquals(129, parent.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.bar, 129)
  }

  @Test def override_setter_with_super(): Unit = {
    class OverrideSetterSuperParent extends js.Object {
      var x: Int = 43
      def bar_=(v: Int): Unit = x = v
    }
    class OverrideSetterSuperChild extends OverrideSetterSuperParent {
      override def bar_=(v: Int): Unit = super.bar_=(v * 3)
    }

    val foo = new OverrideSetterSuperChild
    foo.bar_=(4)
    assertEquals(12, foo.x)

    val parent: OverrideSetterSuperParent = foo
    parent.bar_=(5)
    assertEquals(15, parent.x)

    val dyn = foo.asInstanceOf[js.Dynamic]
    dyn.bar = 6
    assertEquals(dyn.x, 18)
  }

  @Test def super_property_get_set_in_anon_JS_class_issue_3055(): Unit = {
    class Foo extends js.Object {
      var x: Int = 1
      var lastSetValue: Int = 0

      def bar: Int = x
      def bar_=(v: Int): Unit = x = v
    }

    val foo = new Foo {
      override def bar: Int = super.bar * 2
      override def bar_=(v: Int): Unit = {
        lastSetValue = v
        super.bar = v + 3
      }
    }

    assertEquals(2, foo.bar)
    foo.bar = 6
    assertEquals(6, foo.lastSetValue)
    assertEquals(9, foo.x)
    assertEquals(18, foo.bar)
  }

  @Test def add_overload_in_subclass(): Unit = {
    class AddOverloadInSubclassParent extends js.Object {
      def bar(): Int = 53
    }
    class AddOverloadInSubclassChild extends AddOverloadInSubclassParent {
      def bar(x: Int): Int = x + 2
    }

    val foo = new AddOverloadInSubclassChild
    assertEquals(53, foo.bar())
    assertEquals(7, foo.bar(5))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.bar(), 53)
    assertEquals(dyn.bar(5), 7)
  }

  @Test def add_setter_in_subclass(): Unit = {
    class AddSetterInSubclassParent extends js.Object {
      var x: Int = 43
      def bar: Int = x
    }
    class AddSetterInSubclassChild extends AddSetterInSubclassParent {
      def bar_=(v: Int): Unit = x = v
    }

    val foo = new AddSetterInSubclassChild
    foo.bar = 4
    assertEquals(4, foo.x)
    assertEquals(4, foo.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    dyn.bar = 6
    assertEquals(dyn.x, 6)
    assertEquals(dyn.bar, 6)
  }

  @Test def add_getter_in_subclass(): Unit = {
    class AddGetterInSubclassParent extends js.Object {
      var x: Int = 43
      def bar_=(v: Int): Unit = x = v
    }
    class AddGetterInSubclassChild extends AddGetterInSubclassParent {
      def bar: Int = x
    }

    val foo = new AddGetterInSubclassChild
    foo.bar = 4
    assertEquals(4, foo.x)
    assertEquals(4, foo.bar)

    val dyn = foo.asInstanceOf[js.Dynamic]
    dyn.bar = 6
    assertEquals(dyn.x, 6)
    assertEquals(dyn.bar, 6)
  }

  @Test def overload_native_method(): Unit = {
    class OverloadNativeMethod extends NativeParentClass(3) {
      def foo(s: String, y: Int): String = foo(s) + " " + y
    }

    val foo = new OverloadNativeMethod
    assertEquals(foo.foo("hello"), "hello3")
    assertEquals(foo.foo("hello", 4), "hello3 4")

    val parent: NativeParentClass = foo
    assertEquals(parent.foo("hello"), "hello3")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.foo("hello"), "hello3")
    assertEquals(dyn.foo("hello", 4), "hello3 4")
  }

  @Test def overload_non_native_method(): Unit = {
    class OverloadNonNativeMethod extends NonNativeParentClass(3) {
      def foo(s: String, y: Int): String = foo(s) + " " + y
    }

    val foo = new OverloadNonNativeMethod
    assertEquals(foo.foo("hello"), "hello3")
    assertEquals(foo.foo("hello", 4), "hello3 4")

    val parent: NonNativeParentClass = foo
    assertEquals(parent.foo("hello"), "hello3")

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.foo("hello"), "hello3")
    assertEquals(dyn.foo("hello", 4), "hello3 4")
  }

  @Test def overload_with_default_parameter(): Unit = {
    class OverloadDefaultParameter extends js.Object {
      def foo(x: Int): Int = x
      def foo(x: String = ""): String = x
    }

    val foo = new OverloadDefaultParameter
    assertEquals(5, foo.foo(5))
    assertEquals(foo.foo(), "")
    assertEquals(foo.foo("hello"), "hello")
  }

  @Test def implement_a_simple_trait(): Unit = {
    class ImplementSimpleTrait extends js.Object with SimpleTrait {
      def foo(x: Int): Int = x + 1
    }

    val foo = new ImplementSimpleTrait
    assertEquals(4, foo.foo(3))

    val fooTrait: SimpleTrait = foo
    assertEquals(6, fooTrait.foo(5))
  }

  @Test def implement_a_simple_trait_under_separate_compilation(): Unit = {
    class ImplementSimpleTraitSepRun extends js.Object with SepRun.SimpleTrait {
      def foo(x: Int): Int = x + 1
    }

    val foo = new ImplementSimpleTraitSepRun
    assertEquals(4, foo.foo(3))

    val fooTrait: SepRun.SimpleTrait = foo
    assertEquals(6, fooTrait.foo(5))
  }

  @Test def implement_a_trait_with_a_val(): Unit = {
    trait TraitWithVal extends js.Object {
      val x: Int
    }

    class ImplWithVal extends TraitWithVal {
      val x: Int = 3
    }

    val foo = new ImplWithVal
    assertEquals(3, foo.x)

    val fooTrait: TraitWithVal = foo
    assertEquals(3, fooTrait.x)
  }

  @Test def implement_a_trait_with_a_var(): Unit = {
    trait TraitWithVar extends js.Object {
      var x: Int
    }

    class ImplWithVar extends TraitWithVar {
      var x: Int = 3
    }

    val foo = new ImplWithVar
    assertEquals(3, foo.x)

    val fooTrait: TraitWithVar = foo
    assertEquals(3, fooTrait.x)

    foo.x = 5
    assertEquals(5, fooTrait.x)
    fooTrait.x = 19
    assertEquals(19, foo.x)
  }

  @Test def implement_a_trait_extending_a_native_JS_class(): Unit = {
    trait TraitExtendsJSClass extends NativeParentClass {
      def foobar(x: Int): Int
    }

    class ImplExtendsJSClassAndTrait
        extends NativeParentClass(5) with TraitExtendsJSClass {
      def foobar(x: Int): Int = x * 3
    }

    val foo = new ImplExtendsJSClassAndTrait
    assertEquals(18, foo.foobar(6))
  }

  @Test def implement_abstract_members_coming_from_a_native_JS_class(): Unit = {
    class ImplDeferredMembersFromJSParent
        extends NativeParentClassWithDeferred {
      val x: Int = 43

      def bar(y: Int): Int = y * 2
    }

    val FooResult = (12 + 4) * 2 + 43

    val foo = new ImplDeferredMembersFromJSParent
    assertEquals(43, foo.x)
    assertEquals(64, foo.bar(32))
    assertEquals(FooResult, foo.foo(12))

    val fooParent: NativeParentClassWithDeferred = foo
    assertEquals(43, fooParent.x)
    assertEquals(64, fooParent.bar(32))
    assertEquals(FooResult, fooParent.foo(12))

    val dyn = foo.asInstanceOf[js.Dynamic]
    assertEquals(dyn.x, 43)
    assertEquals(dyn.bar(32), 64)
    assertEquals(dyn.foo(12), FooResult)
  }

  @Test def override_a_method_with_default_values_from_a_native_JS_class(): Unit = {
    class OverrideDefault extends NativeParentClass(7) {
      override def methodWithDefault(x: Int = 9): Int = x * 2
    }

    val child = new OverrideDefault
    assertEquals(18, child.methodWithDefault())
    assertEquals(14, child.methodWithDefault(7))

    val parent: NativeParentClass = child
    assertEquals(18, parent.methodWithDefault())
    assertEquals(14, parent.methodWithDefault(7))
  }

  // #2603
  @Test def default_values_in_non_exposed_methods(): Unit = {
    class DefaultParameterss(val default: Int) extends js.Object {
      /* We don't use a constant default value to make sure it actually comes
       * from the default parameter accessors.
       */
      private def privateWithDefault(x: Int = default) = x

      def callPrivate(): Int = privateWithDefault()
      def callNested(): Int = {
        def nested(x: Int = default) = x
        nested()
      }
    }

    val x = new DefaultParameterss(5)
    assertEquals(5, x.callPrivate())
    assertEquals(5, x.callNested())
  }

  // #3939
  @Test def java_lang_object_method_names(): Unit = {
    class JavaLangObjectMethods extends js.Object {
      @JSName("clone")
      def myClone(): String = "myClone"

      @JSName("equals")
      def myEquals(): String = "myEquals"

      @JSName("finalize")
      def myFinalize(): String = "myFinalize"

      @JSName("hashCode")
      def myHashCode(): String = "myHashCode"

      @JSName("notify")
      def myNotify(): String = "myNotify"

      @JSName("notifyAll")
      def myNotifyAll(): String = "myNotifyAll"

      @JSName("wait")
      def myWait(): String = "myWait"
    }

    val x = (new JavaLangObjectMethods).asInstanceOf[js.Dynamic]

    assertEquals(x.applyDynamic("clone")(), "myClone")
  }
}

object NonNativeJSTypeTest {

  // Defined in test-suite/src/test/resources/NonNativeJSTypeTestNatives.js
  @JSGlobal("NonNativeJSTypeTestNativeParentClass")
  @js.native
  class NativeParentClass(val x: Int) extends js.Object {
    def foo(s: String): String = js.native

    def bar: Int = js.native

    def methodWithDefault(x: Int = 5): Int = js.native
  }

  class NonNativeParentClass(val x: Int) extends js.Object {
    def foo(s: String): String = s + x

    def bar: Int = x * 2
  }

  @js.native
  trait NativeTraitWithDeferred extends js.Object {
    val x: Int
  }

  // Defined in test-suite/src/test/resources/NonNativeJSTypeTestNatives.js
  @JSGlobal("NonNativeJSTypeTestNativeParentClassWithDeferred")
  @js.native
  abstract class NativeParentClassWithDeferred extends NativeTraitWithDeferred {
    def foo(y: Int): Int = js.native // = bar(y + 4) + x

    def bar(y: Int): Int
  }

  // Defined in test-suite/src/test/resources/NonNativeJSTypeTestNatives.js
  @JSGlobal("NonNativeJSTypeTestNativeParentClassWithVarargs")
  @js.native
  class NativeParentClassWithVarargs(
      _x: Int, _args: Int*) extends js.Object {
    val x: Int = js.native
    val args: js.Array[Int] = js.native
  }

  trait SimpleTrait extends js.Any {
    def foo(x: Int): Int
  }

  class Minimal extends js.Object

  private var staticNonNativeObjectInitCount: Int = _

  object StaticNonNativeObject extends js.Object {
    staticNonNativeObjectInitCount += 1
  }

  class SimpleMethod extends js.Object {
    def foo(x: Int): Int = x + 3
    def bar(s: String, i: Int): String = s + i
  }

  object StaticObjectSimpleMethod extends js.Object {
    def foo(x: Int): Int = x + 3
    def bar(s: String, i: Int): String = s + i
  }

  class SimpleField extends js.Object {
    val x = 5
    var y = 10

    def sum(): Int = x + y
  }

  object StaticObjectSimpleField extends js.Object {
    val x = 5
    var y = 10

    def sum(): Int = x + y
  }

  class SimpleAccessors extends js.Object {
    var x = 1
    def readPlus1: Int = x + 1

    def neg: Int = -x
    def neg_=(v: Int): Unit = x = -v
  }

  class SimpleConstructor(_x: Int, _y: Int) extends js.Object {
    val x = _x
    var y = _y

    def sum(): Int = x + y
  }

  class ConstructorDefaultParamJSNonNativeNone(val foo: Int = -1) extends js.Object

  class ConstructorDefaultParamJSNonNativeJSNonNative(val foo: Int = -1) extends js.Object
  object ConstructorDefaultParamJSNonNativeJSNonNative extends js.Object

  class ConstructorDefaultParamJSNonNativeScala(val foo: Int = -1) extends js.Object
  object ConstructorDefaultParamJSNonNativeScala

  class ConstructorDefaultParamScalaJSNonNative(val foo: Int = -1)
  object ConstructorDefaultParamScalaJSNonNative extends js.Object

  @js.native
  @JSGlobal("ConstructorDefaultParam")
  class ConstructorDefaultParamJSNativeNone(val foo: Int = -1) extends js.Object

  @js.native
  @JSGlobal("ConstructorDefaultParam")
  class ConstructorDefaultParamJSNativeScala(val foo: Int = -1) extends js.Object
  object ConstructorDefaultParamJSNativeScala

  @js.native
  @JSGlobal("ConstructorDefaultParam")
  class ConstructorDefaultParamJSNativeJSNonNative(val foo: Int = -1) extends js.Object
  object ConstructorDefaultParamJSNativeJSNonNative extends js.Object

  @js.native
  @JSGlobal("ConstructorDefaultParam")
  class ConstructorDefaultParamJSNativeJSNative(val foo: Int = -1) extends js.Object
  @js.native
  @JSGlobal("ConstructorDefaultParam")
  object ConstructorDefaultParamJSNativeJSNative extends js.Object

  // sanity check
  object ConstructorDefaultParamScalaScala
  class ConstructorDefaultParamScalaScala(val foo: Int = -1)

  // sanity check
  class ConstructorDefaultParamScalaNone(val foo: Int = -1)

  class ConstructorDefaultParamMultiParamList(val default: Int)(
      val title: String, val description: js.UndefOr[String] = default.toString)
      extends js.Object

  class ConstructorDefaultParamMultiParamListWithOverloading(val default: Int)(
      val title: String, val description: js.UndefOr[String] = default.toString)
      extends js.Object {
    def this(c: Char) = this(c.toInt)("char", "a char")

    def this(x: Int, y: Int) = this(x)(y.toString, js.undefined)
  }

  class OverloadedConstructorParamNumber(val foo: Int) extends js.Object {
    def this(x: Int, y: Int) = this(x + y)
    def this(x: Int, y: Int, z: Int) = this(x + y, z)
  }

  class OverloadedConstructorParamType(val foo: Int) extends js.Object {
    def this(x: String) = this(x.length)
    def this(x: Option[String]) = this(x.get)
  }

  class OverloadedConstructorComplex(val foo: Int, var bar: Int) extends js.Object {
    def this() = this(5, 6)
    def this(x: Int) = this(x, x)
    def this(x: Int, y: Int, z: Int) = {
      this(x, y)
      bar = z
    }
    def this(x: String) = this(x.length)
    def this(x: String, y: Int) = this(x.length, y)
    def this(x: Int, y: String) = this(x, y.length)
    def this(w: Int, x: Int, y: Int, z: Int) = {
      this(w + x, y, z)
      bar = y
    }
    def this(a: String, x: String, b: String = "", y: String = "") =
      this((a + b).length, (x + y).length)
  }

  class SimpleConstructorAutoFields(val x: Int, var y: Int) extends js.Object {
    def sum(): Int = x + y
  }

  class SimpleConstructorParamAccessors(x: Int, y: Int) extends js.Object {
    def sum(): Int = x + y
  }

  class ConstructorWithParamNameClashes(arg: Int, arg$1: Int, arg$2: Int,
      prep: Int, prep$1: Int, prep$2: Int)
      extends js.Object {
    val allArgs = List(arg, arg$1, arg$2, prep, prep$1, prep$2)
  }

  class DefaultFieldValues extends js.Object {
    var int: Int = _
    var bool: Boolean = _
    var char: Char = _
    var string: String = _
    var unit: Unit = _
    var valueClass: SomeValueClass = _
  }

  trait LazyValFieldsSuperTrait extends js.Object {
    def initCount: Int
    def field: Int
  }

  class LazyValFields extends js.Object with LazyValFieldsSuperTrait {
    var initCount: Int = 0

    lazy val field: Int = {
      initCount += 1
      42
    }
  }

  class OverrideLazyValFields extends LazyValFields {
    override lazy val field: Int = {
      initCount += 1
      53
    }
  }

  class NullingOutLazyValFieldBug3422(initStr: String) extends js.Object {
    lazy val str: String = initStr
  }

  class SimpleInheritedFromNative(
      x: Int, val y: Int) extends NativeParentClass(x)

  class SomeValueClass(val i: Int) extends AnyVal

  object JSNameHolder {
    final val MethodName = "myMethod"
  }

  // #3998
  trait SelfTypeTest1 extends js.Object { self => }
  trait SelfTypeTest2 extends js.Object { self: js.Date => }
  trait SelfTypeTest3 extends js.Object { this: js.Date => }
}
