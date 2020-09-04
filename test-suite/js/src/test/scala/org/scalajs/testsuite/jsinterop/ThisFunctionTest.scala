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

class ThisFunctionTest {

  @Test def should_provide_an_implicit_conversion_from_Scala_function_to_js_ThisFunction(): Unit = {
    val g = js.eval("""
        var g = function(f, x) { return f.call(x, 42, x.foo); }; g;
    """).asInstanceOf[js.Function2[js.ThisFunction2[ // scalastyle:ignore
        js.Dynamic, Int, String, String], js.Dynamic, String]]

    val f = { (thiz: js.Dynamic, v: Int, u: String) =>
      import js.DynamicImplicits.truthValue
      assertTrue(thiz)
      val thiz_foobar = thiz.foobar
      assertEquals(thiz_foobar, "foobar")
      u + v
    }
    val obj = js.Object().asInstanceOf[js.Dynamic]
    obj.foo = "foo"
    obj.foobar = "foobar"
    assertEquals(g(f, obj), "foo42")
  }

  @Test def should_accept_a_lambda_where_a_js_ThisFunction_is_expected(): Unit = {
    val g = js.eval("""
        var g = function(f, x) { return f.call(x, 42, x.foo); }; g;
    """).asInstanceOf[js.Function2[js.ThisFunction2[ // scalastyle:ignore
        js.Dynamic, Int, String, String], js.Dynamic, String]]

    val obj = js.Object().asInstanceOf[js.Dynamic]
    obj.foo = "foo"
    obj.foobar = "foobar"
    val res = g({ (thiz: js.Dynamic, v: Int, u: String) =>
      import js.DynamicImplicits.truthValue
      assertTrue(thiz)
      val thiz_foobar = thiz.foobar
      assertEquals(thiz_foobar, "foobar")
      u + v
    }, obj)
    assertEquals(res, "foo42")
  }

  @Test def should_bind_the_first_argument_to_this_when_applying_js_ThisFunctionN(): Unit = {
    val g = js.eval("""
        var g = function(x) { return this.foo + ":" + x; }; g;
    """).asInstanceOf[js.ThisFunction1[js.Dynamic, Int, String]]
    val obj = js.Object().asInstanceOf[js.Dynamic]
    obj.foo = "foo"
    assertEquals(g(obj, 42), "foo:42")
  }

  @Test def should_provide_an_implicit_conversion_from_js_ThisFunction_to_Scala_function(): Unit = {
    val g = js.eval("""
        var g = function(x) { return this.foo + ":" + x; }; g;
    """).asInstanceOf[js.ThisFunction1[js.Dynamic, Int, String]]
    val f: scala.Function2[js.Dynamic, Int, String] = g
    val obj = js.Object().asInstanceOf[js.Dynamic]
    obj.foo = "foo"
    assertEquals(f(obj, 42), "foo:42")
  }

  @Test def thisFunction_in_trait_issue2643(): Unit = {
    trait TraitWithThisFunction {
      def create = {
        val f = { (passedThis: js.Dynamic) =>
          passedThis
        }
        js.Dynamic.literal(
          "foo" -> ({ (passedThis: js.Dynamic) => {
            passedThis
          } }: js.ThisFunction0[js.Dynamic, js.Dynamic]),
          "bar" -> js.ThisFunction.fromFunction1(f),
          "foobar" -> (f: js.ThisFunction)
        )
      }
    }

    class TraitWithThisFunctionImpl extends TraitWithThisFunction

    val objFactory = new TraitWithThisFunctionImpl()
    val obj = new TraitWithThisFunctionImpl().create
    val thisValue = new js.Object

    assertSame(thisValue, obj.foo.call(thisValue))
    assertSame(thisValue, obj.bar.call(thisValue))
    assertSame(thisValue, obj.foobar.call(thisValue))
  }

  @Test def thisFunctionWithConversionCanBeConstructed(): Unit = {
    val ctor: js.ThisFunction = {
      (thiz: js.Dynamic, x: js.Any) =>
        thiz.x = x
        thiz.y = 42
    }
    val ctorDyn = ctor.asInstanceOf[js.Dynamic]

    assertEquals(js.typeOf(ctorDyn.prototype), "object")

    val obj = js.Dynamic.newInstance(ctorDyn)("foo")
    assertEquals(obj.x, "foo")
    assertEquals(obj.y, 42)
    assertSame(ctor, obj.constructor)
  }

  @Test def thisFunctionWithSAMCanBeConstructed(): Unit = {
    val ctor: js.ThisFunction1[js.Dynamic, js.Any, Any] = {
      (thiz: js.Dynamic, x: js.Any) =>
        thiz.x = x
        thiz.y = 42
    }
    val ctorDyn = ctor.asInstanceOf[js.Dynamic]

    assertEquals(js.typeOf(ctorDyn.prototype), "object")

    val obj = js.Dynamic.newInstance(ctorDyn)("foo")
    assertEquals(obj.x, "foo")
    assertEquals(obj.y, 42)
    assertSame(ctor, obj.constructor)
  }

}
