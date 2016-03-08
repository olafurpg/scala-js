/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.jsinterop

import scala.scalajs.js
import js.JSConverters._
import org.scalajs.jasminetest.JasmineTest

import js.annotation.JSExport

object DynamicTest extends JasmineTest {
  describe("scala.scalajs.js.Dynamic") {
    it("should workaround Scala 2.10 issue with implicit conversion for dynamic fields named x - #8") {

      class Point(val x: Int, val y: Int)

      def jsonToPoint(json: js.Dynamic): Point = {
        new Point(json.x.toString.toInt, json.y.toString.toInt)
      }

      val json =
        js.eval("var dynamicTestPoint = { x: 1, y: 2 }; dynamicTestPoint;")
      val point = jsonToPoint(json.asInstanceOf[js.Dynamic])

      expect(point.x).toEqual(1)
      expect(point.y).toEqual(2)
    }

    it("should allow to call functions with arguments named x") {

      class A {

        def a: Int = 1
      }

      class B
          extends A {
        @JSExport
        def x(par: Int): Int =
          a + par // make sure `this` is bound correctly in JS
      }

      val b = (new B).asInstanceOf[js.Dynamic]

      expect(b.x(10)).toEqual(11)
    }

    it("should allow instanciating JS classes dynamically - #10") {
      val DynamicTestClass = js.eval("""
          var DynamicTestClass = function(x) {
            this.x = x;
          };
          DynamicTestClass;
          """).asInstanceOf[js.Dynamic]
      val obj = js.Dynamic.newInstance(DynamicTestClass)("Scala.js")
      expect(obj.x).toEqual("Scala.js")
    }

    it("should allow instantiating JS classes dynamically with varargs - #708") {
      val DynamicTestClassVarArgs = js.eval("""
          var DynamicTestClassVarArgs = function() {
            this.count = arguments.length;
            for (var i = 0; i < arguments.length; i++)
              this['elem'+i] = arguments[i];
          };
          DynamicTestClassVarArgs;
          """).asInstanceOf[js.Dynamic]

      val obj1 = js.Dynamic.newInstance(DynamicTestClassVarArgs)("Scala.js")
      expect(obj1.count).toEqual(1)
      expect(obj1.elem0).toEqual("Scala.js")

      val obj2 =
        js.Dynamic.newInstance(DynamicTestClassVarArgs)("Scala.js", 42, true)
      expect(obj2.count).toEqual(3)
      expect(obj2.elem0).toEqual("Scala.js")
      expect(obj2.elem1).toEqual(42)
      expect(obj2.elem2).toEqual(true)

      def obj3Args: Seq[js.Any] = Seq("Scala.js", 42, true)
      val obj3 = js.Dynamic.newInstance(DynamicTestClassVarArgs)(obj3Args: _ *)
      expect(obj3.count).toEqual(3)
      expect(obj3.elem0).toEqual("Scala.js")
      expect(obj3.elem1).toEqual(42)
      expect(obj3.elem2).toEqual(true)

      // Check backward binary compatibility with the 0.6.{0,1,2} codegen output
      val obj4 = scala.scalajs.runtime.newJSObjectWithVarargs(
          DynamicTestClassVarArgs, obj3Args.toJSArray).asInstanceOf[js.Dynamic]
      expect(obj4.count).toEqual(3)
      expect(obj4.elem0).toEqual("Scala.js")
      expect(obj4.elem1).toEqual(42)
      expect(obj4.elem2).toEqual(true)
    }

    it("should provide an object literal construction") {
      import js.Dynamic.{literal => obj}
      val x = obj(foo = 3, bar = "foobar")
      expect(x.foo).toEqual(3)
      expect(x.bar).toEqual("foobar")
      expect(x.unknown).toBeUndefined()

      val y = obj(
          inner = obj(name = "inner obj"),
          fun = { () =>
            42
          }
      )
      expect(y.inner.name).toEqual("inner obj")
      expect(y.fun()).toEqual(42)

      expect(obj().anything).toBeUndefined()
    }

    it("object literal in statement position - #1627") {
      // Just make sure it does not cause a SyntaxError
      js.Dynamic.literal(foo = "bar")
      // and also test the case without param (different code path in Printers)
      js.Dynamic.literal()
    }

    it("should provide object literal construction with dynamic naming") {
      import js.Dynamic.{literal => obj}
      val x = obj("foo" -> 3, "bar" -> "foobar")
      expect(x.foo).toEqual(3)
      expect(x.bar).toEqual("foobar")
      expect(x.unknown).toBeUndefined()

      val tup1 = ("hello1", 3: js.Any)
      val tup2 = ("hello2", 10: js.Any)

      val y = obj(tup1, tup2)
      expect(y.hello1).toEqual(3)
      expect(y.hello2).toEqual(10)

      var count = 0
      val z = obj({
        count += 1;
        ("foo", "bar")
      })
      expect(z.foo).toEqual("bar")
      expect(count).toEqual(1)
    }

    it("should allow to create an empty object with the literal syntax") {
      import js.Dynamic.{literal => obj}
      val x = obj()
      expect(x.isInstanceOf[js.Object]).toBeTruthy()
    }

    it("should properly encode object literal property names") {
      import js.Dynamic.{literal => obj}

      val obj0 = obj("3-" -> 42)
      expect(obj0.`3-`).toEqual(42)

      val obj0Dict = obj0.asInstanceOf[js.Dictionary[js.Any]]
      expect(obj0Dict("3-")).toEqual(42)

      val checkEvilProperties = js.eval("""
        function dynamicLiteralNameEncoding_checkEvilProperties(x) {
          return x['.o[3√!|-pr()per7:3$];'] === ' such eval ';
        }
        dynamicLiteralNameEncoding_checkEvilProperties
      """).asInstanceOf[js.Function1[js.Any, Boolean]]
      val obj1 = obj(".o[3√!|-pr()per7:3$];" -> " such eval ").asInstanceOf[
          js.Dictionary[js.Any]]
      expect(obj1(".o[3√!|-pr()per7:3$];")).toEqual(" such eval ")
      expect(checkEvilProperties(obj1)).toEqual(true)

      val checkQuotesProperty = js.eval("""
        function dynamicLiteralNameEncoding_quote(x) {
          return x["'" + '"'] === 7357;
        }
        dynamicLiteralNameEncoding_quote
      """).asInstanceOf[js.Function1[js.Any, Boolean]]

      val quote = '"'

      Seq(
          obj("'" + quote -> 7357),
          obj(s"'$quote" -> 7357),
          obj("'\"" -> 7357),
          obj("'" + quote -> 7357)
      ).foreach { o =>
        val dict = o.asInstanceOf[js.Dictionary[js.Any]]
        expect(dict("'\"")).toEqual(7357)
        expect(dict("'" + quote)).toEqual(7357)
        expect(dict(s"'$quote")).toEqual(7357)
        expect(checkQuotesProperty(o)).toEqual(true)
      }
    }

    it("should accept : _* arguments for literal construction - #1743") {
      import js.Dynamic.literal

      val fields = Seq[(String, js.Any)]("foo" -> 42, "bar" -> "foobar")

      /* Note: we cannot write
       * literal(fields: _*)
       * because scalac does not like it. But we still have to support the
       * expanded notation.
       */

      val x = literal.applyDynamic("apply")(fields: _ *)
      expect(x.foo).toEqual(42)
      expect(x.bar).toEqual("foobar")

      val y = literal.applyDynamicNamed("apply")(fields: _ *)
      expect(y.foo).toEqual(42)
      expect(y.bar).toEqual("foobar")
    }

    it("should allow object literals to have duplicate keys - #1595") {
      import js.Dynamic.{literal => obj}

      // Basic functionality
      val a = obj(foo = 4, bar = 5, foo = 6)
      expect(a.foo).toEqual(6) // last wins
      expect(a.bar).toEqual(5)

      // Side-effects of overwritten properties are kept
      var counter = 0
      val b = obj(foo = {
        counter += 1;
        "foo"
      }, bar = "bar", foo = "foobar")
      expect(counter).toEqual(1)
      expect(b.foo).toEqual("foobar")
      expect(b.bar).toEqual("bar")

      // In a position where unnesting is required - #1628
      @noinline
      def test(x: js.Dynamic): Unit = {
        expect(x.foo).toEqual(6) // last wins
        expect(x.bar).toEqual(5)
      }
      test(obj(foo = 4, bar = 5, foo = 6))
    }

    it("should return subclasses of js.Object in literal construction - #783") {
      import js.Dynamic.{literal => obj}

      val a: js.Object = obj(theValue = 1)
      expect(a.hasOwnProperty("theValue")).toBeTruthy
      expect(a.hasOwnProperty("noValue")).toBeFalsy

      val b: js.Object = obj("theValue" -> 2)
      expect(b.hasOwnProperty("theValue")).toBeTruthy
      expect(b.hasOwnProperty("noValue")).toBeFalsy
    }
  }
}
