/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.jsinterop

import scala.scalajs.js
import js.annotation._
import org.scalajs.jasminetest.{JasmineTest, TestSuiteContext}
import org.scalajs.testsuite.utils.ExpectExceptions

import scala.annotation.meta
import scala.scalajs.js.TypeError

object ExportsTest extends JasmineTest with ExpectExceptions {
  /** This package in the JS (export) namespace */
  val jsPackage = js.Dynamic.global.org.scalajs.testsuite.jsinterop

  describe("@JSExport") {
    it("should offer exports for methods with implicit name") {

      class Foo {
        @JSExport
        def bar(): Int = 42
        @JSExport
        def double(x: Int): Int = x * 2
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.bar)).toBe("function")
      expect(foo.bar()).toEqual(42)
      expect(foo.double(3)).toEqual(6)
    }

    it("should offer exports for methods with explicit name") {

      class Foo {
        @JSExport("theAnswer")
        def bar(): Int = 42
        @JSExport("doubleTheParam")
        def double(x: Int): Int = x * 2
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.bar).toBeUndefined
      expect(js.typeOf(foo.theAnswer)).toBe("function")
      expect(foo.theAnswer()).toEqual(42)
      expect(foo.doubleTheParam(3)).toEqual(6)
    }

    it("should offer exports for methods with constant folded name") {

      class Foo {
        @JSExport(ExportNameHolder.methodName)
        def bar(): Int = 42
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.bar).toBeUndefined
      expect(foo.myMethod()).toEqual(42)
    }

    it("should offer exports for protected methods") {

      class Foo {
        @JSExport
        protected def bar(): Int = 42

        @JSExport
        protected[testsuite] def foo(): Int = 100
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.bar)).toBe("function")
      expect(foo.bar()).toEqual(42)
      expect(js.typeOf(foo.foo)).toBe("function")
      expect(foo.foo()).toEqual(100)
    }

    it("should offer exports for properties with implicit name") {

      class Foo {
        private [ this] var myY: String = "hello"
        @JSExport
        val answer: Int = 42
        @JSExport
        var x: Int = 3
        @JSExport
        def doubleX: Int = x * 2
        @JSExport
        def y: String = myY + " get"
        @JSExport
        def y_=(v: String): Unit = myY = v + " set"
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.answer)).toBe("number")
      expect(foo.answer).toEqual(42)
      expect(foo.x).toEqual(3)
      expect(foo.doubleX).toEqual(6)
      foo.x = 23
      expect(foo.x).toEqual(23)
      expect(foo.doubleX).toEqual(46)
      expect(foo.y).toEqual("hello get")
      foo.y = "world"
      expect(foo.y).toEqual("world set get")
    }

    it("should offer exports for properties with explicit name") {

      class Foo {
        private [ this] var myY: String = "hello"
        @JSExport("answer")
        val answerScala: Int = 42
        @JSExport("x")
        var xScala: Int = 3
        @JSExport("doubleX")
        def doubleXScala: Int = xScala * 2
        @JSExport("y")
        def yGetter: String = myY + " get"
        @JSExport("y")
        def ySetter_=(v: String): Unit = myY = v + " set"
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.answerScala).toBeUndefined
      expect(js.typeOf(foo.answer)).toBe("number")
      expect(foo.answer).toEqual(42)
      expect(foo.x).toEqual(3)
      expect(foo.doubleX).toEqual(6)
      foo.x = 23
      expect(foo.x).toEqual(23)
      expect(foo.doubleX).toEqual(46)
      expect(foo.y).toEqual("hello get")
      foo.y = "world"
      expect(foo.y).toEqual("world set get")
    }

    it("should offer exports for protected properties") {

      class Foo {
        @JSExport
        protected val x: Int = 42
        @JSExport
        protected[testsuite] val y: Int = 43
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.x).toEqual(42)
      expect(foo.y).toEqual(43)
    }

    it("should offer overloaded exports for methods") {

      class Foo {
        @JSExport("foobar")
        def foo(): Int = 42
        @JSExport("foobar")
        def bar(x: Int): Int = x * 2
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.foobar)).toBe("function")
      expect(foo.foobar()).toEqual(42)
      expect(foo.foobar(3)).toEqual(6)
    }

    it("should offer multiple exports for the same method") {

      class Foo {
        @JSExport
        @JSExport("b")
        @JSExport("c")
        def a(): Int = 1
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.a)).toBe("function")
      expect(js.typeOf(foo.b)).toBe("function")
      expect(js.typeOf(foo.c)).toBe("function")

      expect(foo.a()).toEqual(1)
      expect(foo.b()).toEqual(1)
      expect(foo.c()).toEqual(1)
    }

    it("should inherit exports from traits") {

      trait Foo {
        @JSExport def x: Int

        @JSExport def method(x: Int): Int
      }

      class Bar
          extends Foo {
        val x = 1

        def method(x: Int): Int = 2 * x
      }

      val bar = (new Bar).asInstanceOf[js.Dynamic]
      expect(bar.x).toEqual(1)
      expect(js.typeOf(bar.method)).toBe("function")
      expect(bar.method(2)).toEqual(4)
    }

    it("should offer overloading with inherited exports") {

      class A {
        @JSExport
        def foo(x: Int): Int = 2 * x
      }

      class B
          extends A {
        @JSExport("foo")
        def bar(x: String): String = s"Hello $x"
      }

      val b = (new B).asInstanceOf[js.Dynamic]
      expect(js.typeOf(b.foo)).toBe("function")
      expect(b.foo(1)).toEqual(2)
      expect(b.foo("World")).toEqual("Hello World")
    }

    it("should offer exports for generic methods") {

      class Foo {
        @JSExport
        def gen[T <: AnyRef](x: T): T = x
      }

      val x = (new Object).asInstanceOf[js.Any]

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.gen)).toBe("function")
      expect(foo.gen(x)).toBe(x)
    }

    it("should offer exports for lambda return types") {

      class Foo {
        @JSExport
        def lambda(x: Int): Int => Int = (y: Int) => x + y
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.lambda)).toBe("function")

      val lambda = foo.lambda(5).asInstanceOf[Function1[Int, Int]]

      expect(lambda(4)).toEqual(9)
    }

    it("should offer exports for multi parameter lists") {

      class Foo {
        @JSExport
        def multiParam(x: Int)(y: Int): Int = x + y
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.multiParam)).toBe("function")
      expect(foo.multiParam(5, 6)).toEqual(11)
    }

    it("should offer exports for default arguments") {

      class Foo {
        @JSExport
        def defArg(x: Int = 1): Int = x
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.defArg)).toBe("function")
      expect(foo.defArg(5)).toEqual(5)
    }

    it("should offer exports for weird stuff") {

      class UhOh {
        // Something no one should export
        @JSExport
        def ahem[T : Comparable](x: T)(implicit y: Int): Nothing = ???
      }

      val x = (new UhOh).asInstanceOf[js.Dynamic]
      expect(js.typeOf(x.ahem)).toBe("function")
    }

    it("should offer exports with value class return types") {

      class Foo {
        @JSExport
        def vc(x: Int): SomeValueClass = new SomeValueClass(x)
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.vc)).toBe("function")

      // The result should be a boxed SomeValueClass
      val result = foo.vc(5)
      expect(js.typeOf(result)).toEqual("object")
      expect((result: Any).isInstanceOf[SomeValueClass]).toBeTruthy
      expect((result: Any) == (new SomeValueClass(5))).toBeTruthy
    }

    it("should allow exports with Any as return type") {

      class A

      class Foo {
        @JSExport
        def foo(switch: Boolean): Any =
          if (switch) 1
          else new A
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.foo(true).isInstanceOf[Int]).toBeTruthy
      expect(foo.foo(false).isInstanceOf[A]).toBeTruthy
    }

    it("should accept boxed value classes as parameter") {

      class Foo {
        @JSExport
        def vc(x: SomeValueClass): Int = x.i
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(js.typeOf(foo.vc)).toBe("function")

      // The parameter should be a boxed SomeValueClass
      val valueCls = new SomeValueClass(7)
      val result = foo.vc(valueCls.asInstanceOf[js.Any])
      expect(js.typeOf(result)).toEqual("number")
      expect(result).toEqual(7)
    }

    it("should overload on boxed value classes as parameters") {

      class Foo {
        @JSExport
        def foo(x: String): Int = x.length
        @JSExport
        def foo(x: SomeValueClass): Int = x.i
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]
      val valueCls = new SomeValueClass(7)
      expect(foo.foo(valueCls.asInstanceOf[js.Any])).toEqual(7)
      expect(foo.foo("hello")).toEqual(5)
    }

    it("should offer exports for overridden methods with refined return type") {

      class A

      class B extends A

      class C1 {
        @JSExport
        def x: A = new A
      }

      class C2
          extends C1 {

        override def x: B = new B
      }

      val c2 = (new C2).asInstanceOf[js.Dynamic]
      expect(c2.x.isInstanceOf[B]).toBeTruthy
    }

    it("should offer exports for methods with refined types as return type") {

      class A {
        @JSExport
        def foo(x: String): js.Object with js.Dynamic =
          js.Dynamic.literal(arg = x)
      }

      val a = (new A).asInstanceOf[js.Dynamic]
      expect(a.foo("hello")).toEqual(js.Dynamic.literal(arg = "hello"))
    }

    it("should offer exports for variable argument methods - #393") {

      class A {
        @JSExport
        def foo(i: String *): String = i.mkString("|")
      }

      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo()).toEqual("")
      expect(a.foo("a", "b", "c")).toEqual("a|b|c")
      expect(a.foo("a", "b", "c", "d")).toEqual("a|b|c|d")
    }

    it("should correctly overload in view of difficult repeated parameter lists") {

      class A {
        @JSExport
        def foo(a: String, b: String, i: Int, c: String): Int = 1

        @JSExport
        def foo(a: String *): Int = 2

        @JSExport
        def foo(x: Int)(a: Int *): Int = x * 100000 + a.sum
      }

      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo()).toEqual(2)
      expect(a.foo("asdf")).toEqual(2)
      expect(a.foo("asdf", "foo")).toEqual(2)
      expect(a.foo("asdf", "foo", "bar")).toEqual(2)
      expect(a.foo("asdf", "foo", 1, "bar")).toEqual(1)
      expect(a.foo("asdf", "foo", "foo", "bar")).toEqual(2)
      expect(a.foo(5, 1, 2, 3, 10)).toEqual(500016)
      expect(a.foo(1)).toEqual(100000)
    }

    it("should offer exports with default arguments") {

      class A {
        var oneCount: Int = 0

        def one: Int = {
          oneCount += 1
          1
        }
        @JSExport
        def foo(a: Int = one)(b: Int = a + one)(c: Int = b + one): Int =
          a + b + c
      }

      val a = new A
      val jsa = a.asInstanceOf[js.Dynamic]

      expect(jsa.foo()).toEqual(6)
      expect(a.oneCount).toEqual(3)

      expect(jsa.foo(2)).toEqual(9)
      expect(a.oneCount).toEqual(5)

      expect(jsa.foo(2, 4)).toEqual(11)
      expect(a.oneCount).toEqual(6)

      expect(jsa.foo(2, 4, 10)).toEqual(16)
      expect(a.oneCount).toEqual(6)

      expect(jsa.foo((), 4, 10)).toEqual(15)
      expect(a.oneCount).toEqual(7)

      expect(jsa.foo((), 4)).toEqual(10)
      expect(a.oneCount).toEqual(9)
    }

    it("should correctly overload methods in presence of default parameters") {

      class A {
        @JSExport
        def foo(a: Int)(b: Int = 5)(c: Int = 7): Int = 1000 + a + b + c

        @JSExport
        def foo(a: Int, b: String): Int = 2

        @JSExport
        def foo(a: Int, b: Int, c: String): Int = 3
      }

      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo(1)).toEqual(1013)
      expect(a.foo(1, 4)).toEqual(1012)
      expect(a.foo(1, 4, 5)).toEqual(1010)
      expect(a.foo(1, "foo")).toEqual(2)
      expect(a.foo(1, 2, "foo")).toEqual(3)
    }

    it("should prefer overloads taking a Unit over methods with default parameters") {

      class A {
        @JSExport
        def foo(a: Int)(b: String = "asdf"): String = s"$a $b"

        @JSExport
        def foo(a: Int, b: Unit): String = "woot"
      }

      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo(1)).toEqual("1 asdf")
      expect(a.foo(2, "omg")).toEqual("2 omg")
      expect(a.foo(1, ())).toEqual("woot")
    }

    it("should correctly overload methods in presence of default parameters and repeated parameters") {

      class A {
        @JSExport
        def foo(x: Int, y: Int = 1): Int = x + y
        @JSExport
        def foo(x: String *): String = x.mkString("|")
      }

      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo(1)).toEqual(2)
      expect(a.foo(1, 2)).toEqual(3)
      expect(a.foo()).toEqual("")
      expect(a.foo("foo")).toEqual("foo")
      expect(a.foo("foo", "bar")).toEqual("foo|bar")
    }

    it("should correctly overload exports called `toString`") {

      class A {

        override def toString(): String = "no arg"
        @JSExport
        def toString(x: Int): String = s"with arg: $x"
      }

      val a = (new A).asInstanceOf[js.Dynamic]
      expect(a.applyDynamic("toString")()).toEqual("no arg")
      expect(a.applyDynamic("toString")(1)).toEqual("with arg: 1")
    }

    it("should allow to explicitly export toString") {

      class A {
        @JSExport("toString")
        override def toString(): String = "called"
      }

      val a = (new A).asInstanceOf[js.Dynamic]
      expect(a.applyDynamic("toString")()).toEqual("called")
    }

    it("should correctly box repeated parameter lists with value classes") {

      class A {
        @JSExport
        def foo(vcs: SomeValueClass *): Int = vcs.map(_.i).sum
      }

      val vc1 = new SomeValueClass(1)
      val vc2 = new SomeValueClass(2)
      val a = (new A).asInstanceOf[js.Dynamic]

      expect(a.foo(vc1.asInstanceOf[js.Any], vc2.asInstanceOf[js.Any]))
        .toEqual(3)
    }

    it("should offer exports for objects with implicit name") {
      val accessor = jsPackage.ExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for Scala.js-defined JS objects with implicit name") {
      val accessor = jsPackage.SJSDefinedExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for objects with explicit name") {
      val accessor = js.Dynamic.global.TheExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for Scala.js-defined JS objects with explicit name") {
      val accessor = js.Dynamic.global.TheSJSDefinedExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for objects with qualified name") {
      val accessor = js.Dynamic.global.qualified.testobject.ExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for objects with constant folded name") {
      val accessor = js.Dynamic.global.ConstantFoldedObjectExport
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for protected objects") {
      val accessor = jsPackage.ProtectedExportedObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(js.typeOf(obj)).toEqual("object")
      expect(obj.witness).toEqual("witness")
    }

    it("should offer exports for classes with implicit name") {
      val constr = jsPackage.ExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for Scala.js-defined JS classes with implicit name") {
      val constr = jsPackage.SJSDefinedExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect((obj: Any).isInstanceOf[SJSDefinedExportedClass]).toBeTruthy
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for classes with explicit name") {
      val constr = js.Dynamic.global.TheExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for Scala.js-defined JS classes with explicit name") {
      val constr = js.Dynamic.global.TheSJSDefinedExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect((obj: Any).isInstanceOf[SJSDefinedExportedClass]).toBeTruthy
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for classes with qualified name") {
      val constr = js.Dynamic.global.qualified.testclass.ExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for classes with qualified name") {
      val constr =
        js.Dynamic.global.qualified.testclass.SJSDefinedExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect((obj: Any).isInstanceOf[SJSDefinedExportedClass]).toBeTruthy
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for classes with constant folded name") {
      val constr = js.Dynamic.global.ConstantFoldedClassExport
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect(obj.x).toEqual(5)
    }

    it("should offer exports for protected classes") {
      val constr = jsPackage.ProtectedExportedClass
      expect(constr).toBeDefined
      expect(js.typeOf(constr)).toEqual("function")
      val obj = js.Dynamic.newInstance(constr)(5)
      expect(obj.x).toEqual(5)
    }

    it("should offer export for classes with repeated parameters in ctor") {
      val constr = jsPackage.ExportedVarArgClass
      expect(js.Dynamic.newInstance(constr)().result).toEqual("")
      expect(js.Dynamic.newInstance(constr)("a").result).toEqual("a")
      expect(js.Dynamic.newInstance(constr)("a", "b").result).toEqual("a|b")
      expect(js.Dynamic.newInstance(constr)("a", "b", "c").result)
        .toEqual("a|b|c")
      expect(js.Dynamic.newInstance(constr)(5, "a").result)
        .toEqual("Number: <5>|a")
    }

    it("should offer export for classes with default parameters in ctor") {
      val constr = jsPackage.ExportedDefaultArgClass
      expect(js.Dynamic.newInstance(constr)(1, 2, 3).result).toEqual(6)
      expect(js.Dynamic.newInstance(constr)(1).result).toEqual(106)
      expect(js.Dynamic.newInstance(constr)(1, 2).result).toEqual(103)
    }

    it("should correctly disambiguate overloads involving longs") {

      class Foo {
        @JSExport
        def foo(x: Int): Int = 1
        @JSExport
        def foo(x: Long): Int = 2
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      // Create a long factory we can call dynamically to retrieve an unboxed
      // long which is typed as a js.Any

      object LongFactory {
        @JSExport
        def aLong: Long = 1L
      }
      val trueJsLong = LongFactory.asInstanceOf[js.Dynamic].aLong

      expect(foo.foo(1)).toEqual(1)
      expect(foo.foo(trueJsLong)).toEqual(2)
    }

    it("should return boxed Chars") {

      class Foo {
        @JSExport
        def bar(x: Int): Char = x.toChar
      }
      val foo = (new Foo).asInstanceOf[js.Dynamic]

      val funs = js.eval("""
          var funs = {
            testIsChar: function(foo) { return JSUtils().isChar(foo.bar(65)); },
            testCharValue: function(foo) { return JSUtils().charToString(foo.bar(65)); }
          }; funs;
          """).asInstanceOf[js.Dynamic]

      expect(funs.testIsChar(foo)).toBeTruthy
      expect(funs.testCharValue(foo)).toEqual("A")
    }

    it("should take boxed Chars as parameter") {

      class Foo {
        @JSExport
        def bar(x: Char): Int = x.toInt
      }
      val foo = (new Foo).asInstanceOf[js.Dynamic]

      val f = js.eval("""
          var f = function(foo) { return foo.bar(JSUtils().stringToChar('e')); };
          f;
          """).asInstanceOf[js.Dynamic]

      expect(f(foo)).toEqual('e'.toInt)
    }

    it("should be able to disambiguate an Int from a Char") {

      class Foo {
        @JSExport
        def bar(x: Char): String = "char: " + x
        @JSExport
        def bar(x: Int): String = "int: " + x
      }
      val foo = (new Foo).asInstanceOf[js.Dynamic]

      val funs = js.eval("""
          var funs = {
            testChar: function(foo) { return foo.bar(JSUtils().stringToChar('S')); },
            testInt: function(foo) { return foo.bar(68); }
          }; funs;
          """).asInstanceOf[js.Dynamic]

      expect(funs.testChar(foo)).toEqual("char: S")
      expect(funs.testInt(foo)).toEqual("int: 68")
    }

    it("should support exporting constructor parameter fields - #970") {

      class Foo(@(JSExport @meta.field) val x: Int)
      val foo = (new Foo(1)).asInstanceOf[js.Dynamic]
      expect(foo.x).toEqual(1)
    }

    it("should support exporting case class fields - #970") {

      case class Foo(@(JSExport @meta.field) x: Int)
      val foo = (new Foo(1)).asInstanceOf[js.Dynamic]
      expect(foo.x).toEqual(1)
    }

    it("should support exporting lazy values - #977") {

      class Foo {
        @JSExport
        lazy val x = 1
      }
      val foo = (new Foo).asInstanceOf[js.Dynamic]
      expect(foo.x).toEqual(1)
    }

    it("should support exporting all members of a class") {
      @JSExportAll
      class Foo {
        val a = 1

        @JSExport // double annotation allowed
        def b: Int = 2

        lazy val c = 3

        class Bar // not exported, but should not fail
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      expect(foo.a).toEqual(1)
      expect(foo.b).toEqual(2)
      expect(foo.c).toEqual(3)
    }

    it("should not export synthetic members with @JSExportAll - #1195") {
      @JSExportAll
      case class Foo(x: Int)

      val foo = Foo(1).asInstanceOf[js.Dynamic]

      expect(foo.x).toEqual(1)
      expect(foo.copy).toBeUndefined
    }

    it("should allow mutliple equivalent JSExport annotations") {

      class Foo {
        @JSExport
        @JSExport("a")
        @JSExport
        @JSExport("a")
        def b: Int = 1
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      expect(foo.b).toEqual(1)
    }

    it("should support named exports") {
      import js.Dynamic.{literal => lit}

      class FooNamed {
        @JSExportNamed("bar1")
        def bar(x: Int, y: Int): Int = x + y

        @JSExportNamed("bar2")
        @JSExport
        def bar(x: Int = 1)(y: Int = x)(z: Int = y): Int = x + y + z
      }

      val foo = (new FooNamed).asInstanceOf[js.Dynamic]

      expect(foo.bar1(lit(x = 1, y = 2))).toEqual(3)
      if (TestSuiteContext.hasTag("compliant-asinstanceofs"))
        expect(() => foo.bar1(lit(x = 1))).toThrow // missing arg
      expect(foo.bar2(lit())).toEqual(3)
      expect(foo.bar2(lit(x = 2))).toEqual(6)
      expect(foo.bar2(lit(y = 2))).toEqual(5)
      expect(foo.bar2(lit(y = 2, z = 1))).toEqual(4)
      expect(foo.bar(2)).toEqual(6)
      expect(foo.bar(2, 3)).toEqual(8)
    }

    it("should support named constructor exports") {
      import js.Dynamic.{literal => lit}

      val constr = jsPackage.ExportedNamedArgClass
      expect(js.Dynamic.newInstance(constr)(lit(x = 2)).result)
        .toEqual("22true")
      expect(js.Dynamic.newInstance(constr)(lit(y = "foo")).result)
        .toEqual("1foofalse")
      expect(js.Dynamic.newInstance(constr)(lit(z = true, y = "foo")).result)
        .toEqual("1footrue")
    }

    it("should support exporting under 'org' namespace - #364") {
      val accessor = js.Dynamic.global.org.ExportedUnderOrgObject
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBe(ExportedUnderOrgObject.asInstanceOf[js.Any])
    }

    it("should accept null for arguments of primitive value type - #1719") {
      @JSExportAll
      class Foo {

        def doBool(x: Boolean): Unit =
          expect((x: Any) == false).toBeTruthy // scalastyle:ignore

        def doChar(x: Char): Unit = expect(x.equals('\0')).toBeTruthy

        def doByte(x: Byte): Unit = expect(x).toEqual(0)

        def doShort(x: Short): Unit = expect(x).toEqual(0)

        def doInt(x: Int): Unit = expect(x).toEqual(0)

        def doLong(x: Long): Unit = expect(x.equals(0L)).toBeTruthy

        def doFloat(x: Float): Unit = expect(x).toEqual(0.0f)

        def doDouble(x: Double): Unit = expect(x).toEqual(0.0)

        def doUnit(x: Unit): Unit = expect((x: Any) == null).toBeTruthy
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      foo.doBool(null)
      foo.doChar(null)
      foo.doByte(null)
      foo.doShort(null)
      foo.doInt(null)
      foo.doLong(null)
      foo.doFloat(null)
      foo.doDouble(null)
      foo.doUnit(null)
    }

    when("compliant-asinstanceofs")
      .it("should reject bad values for arguments of primitive value type") {
      @JSExportAll
      class Foo {

        def doBool(x: Boolean): Boolean = x

        def doChar(x: Char): Char = x

        def doByte(x: Byte): Byte = x

        def doShort(x: Short): Short = x

        def doInt(x: Int): Int = x

        def doLong(x: Long): Long = x

        def doFloat(x: Float): Float = x

        def doDouble(x: Double): Double = x

        def doUnit(x: Unit): Unit = x
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      // Class type
      expect(() => foo.doBool(foo)).toThrow
      expect(() => foo.doChar(foo)).toThrow
      expect(() => foo.doByte(foo)).toThrow
      expect(() => foo.doShort(foo)).toThrow
      expect(() => foo.doInt(foo)).toThrow
      expect(() => foo.doLong(foo)).toThrow
      expect(() => foo.doFloat(foo)).toThrow
      expect(() => foo.doDouble(foo)).toThrow
      expect(() => foo.doUnit(foo)).toThrow

      // Bad values
      expect(() => foo.doBool(1)).toThrow
      expect(() => foo.doBool("a")).toThrow

      expect(() => foo.doChar(1)).toThrow
      expect(() => foo.doChar("a")).toThrow

      expect(() => foo.doByte(300)).toThrow
      expect(() => foo.doByte("a")).toThrow

      expect(() => foo.doShort(32768)).toThrow
      expect(() => foo.doShort("a")).toThrow

      expect(() => foo.doInt(3.2)).toThrow
      expect(() => foo.doInt("a")).toThrow

      expect(() => foo.doLong(3.2)).toThrow
      expect(() => foo.doLong(3)).toThrow
      expect(() => foo.doLong("a")).toThrow

      expect(() => foo.doFloat("a")).toThrow
    }

    when("compliant-asinstanceofs").it(
        "should reject bad values for arguments of value class type - #613") {

      class Foo {
        @JSExport
        def doVC(x: SomeValueClass): SomeValueClass = x
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      expect(() => foo.doVC(null)).toThrow
      expect(() => foo.doVC(foo)).toThrow
      expect(() => foo.doVC(1)).toThrow
      expect(() => foo.doVC("a")).toThrow
    }

    when("compliant-asinstanceofs")
      .it("should reject bad values for arguments of class type") {

      class A

      class B

      class Foo {
        @JSExport
        def doA(x: A): A = x
      }

      val foo = (new Foo).asInstanceOf[js.Dynamic]

      expect(() => foo.doA(1)).toThrow
      expect(() => foo.doA((new B).asInstanceOf[js.Any])).toThrow
      expect(() => foo.doA("a")).toThrow
    }

    it("should offer exports for classes ending in _= - #1090") {
      val constr = jsPackage.ExportClassSetterNamed_=
      val obj = js.Dynamic.newInstance(constr)()
      expect(obj.x).toBe(1)
    }

    it("should offer exports for objects ending in _= - #1090") {
      expect(jsPackage.ExportObjSetterNamed_=().x).toBe(1)
    }

    it("should expose public members of new js.Object{...} - #1899") {

      // Test that the bug is fixed for js.Any classes.

      def testExposure(obj: js.Object): Unit = {
        expect(obj).toBeDefined
        expect(obj.hasOwnProperty("x1")).toBeTruthy
        expect(obj.hasOwnProperty("y1")).toBeTruthy
        expect(obj.hasOwnProperty("x2")).toBeFalsy
        expect(obj.hasOwnProperty("y2")).toBeFalsy
        expect(obj.hasOwnProperty("x3")).toBeFalsy
        expect(obj.hasOwnProperty("y3")).toBeFalsy

        val dynObj = obj.asInstanceOf[js.Dynamic]
        expect(dynObj.x1).toEqual("x1")
        expect(dynObj.x2).not.toBeDefined
        expect(dynObj.x3).not.toBeDefined

        expect(dynObj.y1).toEqual("y1")
        expect(dynObj.y2).not.toBeDefined
        expect(dynObj.y3).not.toBeDefined

        expect(dynObj.z1()).toEqual("z1")
        expect(dynObj.z2).not.toBeDefined
        expect(dynObj.z2).not.toBeDefined
        expect(dynObj.z3).not.toBeDefined

        dynObj.y1 = "y1+"
        dynObj.y2 = "y2+"
        dynObj.y3 = "y3+"
        expect(dynObj.y1).toEqual("y1+")
        expect(dynObj.y2).toEqual("y2+")
        expect(dynObj.y3).toEqual("y3+")
        expect(dynObj.checkOriginalY1()).toEqual("y1+")
        expect(dynObj.checkOriginalY2()).toEqual("y2")
        expect(dynObj.checkOriginalY3()).toEqual("y3")
      }

      def getJSObj(): js.Object =
        new js.Object {
          val x1 = "x1"
          var y1 = "y1"

          def z1() = "z1"
          private val x2 = "x2"
          private var y2 = "y2"

          private def z2() = "z2"
          private [ this] val x3 = "x3"
          private [ this] var y3 = "y3"

          private [ this] def z3() = "z3"

          def checkOriginalY1() = y1

          def checkOriginalY2() = y2

          def checkOriginalY3() = y3
        }

      @ScalaJSDefined
      class JSClass extends js.Object

      def getJSObj2(): js.Object =
        new JSClass {
          val x1 = "x1"
          var y1 = "y1"

          def z1() = "z1"
          private val x2 = "x2"
          private var y2 = "y2"

          private def z2() = "z2"
          private [ this] val x3 = "x3"
          private [ this] var y3 = "y3"

          private [ this] def z3() = "z3"

          def checkOriginalY1() = y1

          def checkOriginalY2() = y2

          def checkOriginalY3() = y3
        }

      @ScalaJSDefined
      abstract class JSAbstractClass extends js.Object

      def getJSObj3(): js.Object =
        new JSAbstractClass {
          val x1 = "x1"
          var y1 = "y1"

          def z1() = "z1"
          private val x2 = "x2"
          private var y2 = "y2"

          private def z2() = "z2"
          private [ this] val x3 = "x3"
          private [ this] var y3 = "y3"

          private [ this] def z3() = "z3"

          def checkOriginalY1() = y1

          def checkOriginalY2() = y2

          def checkOriginalY3() = y3
        }

      @ScalaJSDefined
      abstract class JSTrait extends js.Object

      def getJSObj4(): js.Object =
        new JSTrait {
          val x1 = "x1"
          var y1 = "y1"

          def z1() = "z1"
          private val x2 = "x2"
          private var y2 = "y2"

          private def z2() = "z2"
          private [ this] val x3 = "x3"
          private [ this] var y3 = "y3"

          private [ this] def z3() = "z3"

          def checkOriginalY1() = y1

          def checkOriginalY2() = y2

          def checkOriginalY3() = y3
        }

      testExposure(getJSObj())
      testExposure(getJSObj2())
      testExposure(getJSObj3())
      testExposure(getJSObj4())

      // Test that non js.Any classes were unaffected by the fix.

      def getObj(): AnyRef =
        new {
          val x1 = "x1"
          var y1 = "y1"

          def z1() = "z1"
          private val x2 = "x2"
          private var y2 = "y2"

          private def z2() = "z2"
          private [ this] val x3 = "x3"
          private [ this] var y3 = "y3"

          private [ this] def z3() = "z3"
        }

      import scala.language.reflectiveCalls

      val obj2 = getObj().asInstanceOf[ {
        val x1: String;
        var y1: String;
        def z1(): String
      }]

      expectThrows[Throwable](obj2.x1)
      expectThrows[Throwable](obj2.y1)
      expectThrows[Throwable](obj2.y1 = "y1+")
      expectThrows[Throwable](obj2.z1)
    }
  } // describe

  describe("@JSExportDescendentObjects") {
    it("should offer auto exports for objects extending a trait") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportedTraitObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(AutoExportedTraitObject.asInstanceOf[js.Any])
    }

    it("should offer auto exports for objects extending a class") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportedClassObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(AutoExportedClassObject.asInstanceOf[js.Any])
    }

    it("should offer auto exports for Scala.js-defined JS objects extending a trait") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedTraitObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(SJSDefinedAutoExportedTraitObject)
    }

    it("should offer auto exports for Scala.js-defined JS objects extending a class") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedClassObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(SJSDefinedAutoExportedClassObject)
    }

    it("should offer auto exports for objects extending a trait with ignoreInvalidDescendants") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportIgnoreTraitObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(AutoExportIgnoreTraitObject.asInstanceOf[js.Any])
    }

    it("should offer auto exports for objects extending a class with ignoreInvalidDescendants") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportIgnoreClassObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(AutoExportIgnoreClassObject.asInstanceOf[js.Any])
    }

    it("should offer auto exports for Scala.js-defined JS objects extending " + "a class with ignoreInvalidDescendants") {
      val accessor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedIgnoreClassObject
      expect(accessor).toBeDefined
      expect(js.typeOf(accessor)).toEqual("function")
      val obj = accessor()
      expect(obj).toBeDefined
      expect(obj).toBe(SJSDefinedAutoExportedIgnoreClassObject)
    }

    it("should ignore invalid descendants") {

      // This is just to check that everything here compiles

      object A extends AutoExportIgnoreTrait { var x = 1 }

      object B extends AutoExportIgnoreClass { var x = 2 }

      @ScalaJSDefined
      object C extends SJSDefinedAutoExportIgnoreClass { var x = 3 }

      // Check that the objects are usable
      expect(A.x).toEqual(1)
      expect(B.x).toEqual(2)
      expect(C.x).toEqual(3)

      A.x = 3
      B.x = 4
      C.x = 2

      expect(A.x).toEqual(3)
      expect(B.x).toEqual(4)
      expect(C.x).toEqual(2)
    }
  }

  describe("@JSExportDescendentClasses") {
    it("should offer auto exports for classes extending a trait") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportedTraitClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj1 = js.Dynamic.newInstance(ctor)()
      expect(obj1).toBeDefined
      expect(obj1.x).toBe(5)

      val obj2 = js.Dynamic.newInstance(ctor)(100)
      expect(obj2).toBeDefined
      expect(obj2.x).toBe(100)
    }

    it("should offer auto exports for classes extending a class") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportedClassClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj1 = js.Dynamic.newInstance(ctor)()
      expect(obj1).toBeDefined
      expect(obj1.x).toBe(5)

      val obj2 = js.Dynamic.newInstance(ctor)(100)
      expect(obj2).toBeDefined
      expect(obj2.x).toBe(100)
    }

    it("should offer auto exports for Scala.js-defined JS classes extending a trait") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedTraitClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj = js.Dynamic.newInstance(ctor)(100)
      expect((obj: Any).isInstanceOf[SJSDefinedAutoExportedTraitClass]).toBeTruthy
      expect(obj).toBeDefined
      expect(obj.x).toBe(100)
    }

    it("should offer auto exports for Scala.js-defined JS classes extending a class") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedClassClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj = js.Dynamic.newInstance(ctor)(100)
      expect((obj: Any).isInstanceOf[SJSDefinedAutoExportedClassClass]).toBeTruthy
      expect(obj).toBeDefined
      expect(obj.x).toBe(100)
    }

    it("should offer auto exports for classes extending a trait with ignoreInvalidDescendants") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportIgnoreTraitClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj1 = js.Dynamic.newInstance(ctor)()
      expect(obj1).toBeDefined
      expect(obj1.x).toBe(5)

      val obj2 = js.Dynamic.newInstance(ctor)(100)
      expect(obj2).toBeDefined
      expect(obj2.x).toBe(100)
    }

    it("should offer auto exports for classes extending a class with ignoreInvalidDescendants") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.AutoExportIgnoreClassClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj1 = js.Dynamic.newInstance(ctor)()
      expect(obj1).toBeDefined
      expect(obj1.x).toBe(5)

      val obj2 = js.Dynamic.newInstance(ctor)(100)
      expect(obj2).toBeDefined
      expect(obj2.x).toBe(100)
    }

    it("should offer auto exports for Scala.js-defined JS classes extending " + "a class with ignoreInvalidDescendants") {
      val ctor =
        js.Dynamic.global.org.scalajs.testsuite.jsinterop.SJSDefinedAutoExportedIgnoreClassClass
      expect(ctor).toBeDefined
      expect(js.typeOf(ctor)).toEqual("function")

      val obj = js.Dynamic.newInstance(ctor)(100)
      expect((obj: Any).isInstanceOf[SJSDefinedAutoExportedIgnoreClassClass]).toBeTruthy
      expect(obj).toBeDefined
      expect(obj.x).toBe(100)
    }

    it("should ignore invalid descendants") {

      trait HasBar { def bar: Int }

      @ScalaJSDefined
      trait SJSDefinedHasBar extends js.Any { def bar: Int }

      // This is just to check that everything here compiles

      class A extends AutoExportIgnoreTrait { def foo: Int = 1 }

      class B extends AutoExportIgnoreClass { def foo: Int = 2 }

      @ScalaJSDefined
      class C extends SJSDefinedAutoExportIgnoreClass { def foo: Int = 3 }

      val a = new A { override def foo: Int = 3 }
      val b = new B { override def foo: Int = 4 }
      val c = new C { override def foo: Int = 5 }
      val d = new AutoExportIgnoreClass with HasBar { def bar: Int = 1 }
      val e = new AutoExportIgnoreTrait with HasBar { def bar: Int = 1 }
      val f = new SJSDefinedAutoExportIgnoreClass with SJSDefinedHasBar {

        def bar: Int = 1
      }

      // Check the classes are usable
      expect((new A).foo).toEqual(1)
      expect((new B).foo).toEqual(2)
      expect((new C).foo).toEqual(3)
      expect(a.foo).toEqual(3)
      expect(b.foo).toEqual(4)
      expect(c.foo).toEqual(5)
      expect(d.bar).toEqual(1)
      expect(e.bar).toEqual(1)
      expect(f.bar).toEqual(1)
    }
  }
}

object ExportNameHolder {
  final val className = "ConstantFoldedClassExport"
  final val objectName = "ConstantFoldedObjectExport"
  final val methodName = "myMethod"
}

@JSExport
@JSExport("TheExportedObject")
@JSExport("qualified.testobject.ExportedObject") // purposefully halfway the same as ExportedClass
@JSExport(ExportNameHolder.objectName)
object ExportedObject {
  @JSExport
  def witness: String = "witness"
}

@JSExport
@JSExport("TheSJSDefinedExportedObject")
@ScalaJSDefined
object SJSDefinedExportedObject extends js.Object {

  def witness: String = "witness"
}

@JSExport
protected object ProtectedExportedObject {
  @JSExport
  def witness: String = "witness"
}

@JSExport
@JSExport("TheExportedClass")
@JSExport("qualified.testclass.ExportedClass") // purposefully halfway the same as ExportedObject
@JSExport(ExportNameHolder.className)
class ExportedClass(_x: Int) {
  @JSExport
  val x = _x
}

@JSExport
@JSExport("TheSJSDefinedExportedClass")
@JSExport("qualified.testclass.SJSDefinedExportedClass")
@ScalaJSDefined
class SJSDefinedExportedClass(val x: Int) extends js.Object

@JSExport
protected class ProtectedExportedClass(_x: Int) {
  @JSExport
  val x = _x
}

@JSExport
class ExportedVarArgClass(x: String *) {
  @JSExport def this (x: Int, y: String) = this(s"Number: <$x>", y)

  @JSExport
  def result: String = x.mkString("|")
}

@JSExport
class ExportedDefaultArgClass(x: Int, y: Int, z: Int) {
  @JSExport def this (x: Int, y: Int = 5) = this(x, y, 100)

  @JSExport
  def result: Int = x + y + z
}

@JSExport("org.ExportedUnderOrgObject")
object ExportedUnderOrgObject

@JSExportDescendentClasses
@JSExportDescendentObjects
trait AutoExportTrait

object AutoExportedTraitObject extends AutoExportTrait

class AutoExportedTraitClass(_x: Int) extends AutoExportTrait {
  def this () = this(5)
  @JSExport
  def x: Int = _x
}

@JSExportDescendentClasses
@JSExportDescendentObjects
class AutoExportClass

object AutoExportedClassObject extends AutoExportClass

class AutoExportedClassClass(_x: Int) extends AutoExportTrait {
  def this () = this(5)
  @JSExport
  def x: Int = _x
}

@JSExportDescendentClasses
@JSExportDescendentObjects
@ScalaJSDefined
trait SJSDefinedAutoExportTrait extends js.Object

@ScalaJSDefined
object SJSDefinedAutoExportedTraitObject extends SJSDefinedAutoExportTrait
@ScalaJSDefined
class SJSDefinedAutoExportedTraitClass(val x: Int)
    extends SJSDefinedAutoExportTrait

@JSExportDescendentClasses
@JSExportDescendentObjects
@ScalaJSDefined
class SJSDefinedAutoExportClass extends js.Object

@ScalaJSDefined
object SJSDefinedAutoExportedClassObject extends SJSDefinedAutoExportClass
@ScalaJSDefined
class SJSDefinedAutoExportedClassClass(val x: Int)
    extends SJSDefinedAutoExportClass

@JSExportDescendentClasses(ignoreInvalidDescendants = true)
@JSExportDescendentObjects(ignoreInvalidDescendants = true)
trait AutoExportIgnoreTrait

object AutoExportIgnoreTraitObject extends AutoExportIgnoreTrait

class AutoExportIgnoreTraitClass(_x: Int) extends AutoExportIgnoreTrait {
  def this () = this(5)
  @JSExport
  def x: Int = _x
}

@JSExportDescendentClasses(ignoreInvalidDescendants = true)
@JSExportDescendentObjects(ignoreInvalidDescendants = true)
class AutoExportIgnoreClass

object AutoExportIgnoreClassObject extends AutoExportIgnoreClass

class AutoExportIgnoreClassClass(_x: Int) extends AutoExportIgnoreTrait {
  def this () = this(5)
  @JSExport
  def x: Int = _x
}

@JSExportDescendentClasses(ignoreInvalidDescendants = true)
@JSExportDescendentObjects(ignoreInvalidDescendants = true)
@ScalaJSDefined
class SJSDefinedAutoExportIgnoreClass extends js.Object

@ScalaJSDefined
object SJSDefinedAutoExportedIgnoreClassObject extends SJSDefinedAutoExportIgnoreClass
@ScalaJSDefined
class SJSDefinedAutoExportedIgnoreClassClass(val x: Int)
    extends SJSDefinedAutoExportIgnoreClass

@js.native
object NativeInvalidExportObject extends SJSDefinedAutoExportIgnoreClass
@js.native
class NativeInvalidExportClass extends SJSDefinedAutoExportIgnoreClass
@ScalaJSDefined
class SJSDefinedInvalidExportClass private()
    extends SJSDefinedAutoExportIgnoreClass

class SomeValueClass(val i: Int) extends AnyVal

@JSExportNamed
class ExportedNamedArgClass(x: Int = 1)(y: String = x.toString)(
    z: Boolean = y != "foo") {
  @JSExport
  val result = x + y + z
}

@JSExport
class ExportClassSetterNamed_= {
  // scalastyle:ignore
  @JSExport
  val x = 1
}

@JSExport
object ExportObjSetterNamed_= {
  // scalastyle:ignore
  @JSExport
  val x = 1
}
