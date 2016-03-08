/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.compiler

import scala.scalajs.js
import org.scalajs.jasminetest.JasmineTest

import language.reflectiveCalls

import java.lang.{Float => JFloat, Double => JDouble}

object ReflectiveCallTest extends JasmineTest {
  describe("Reflective Calls") {
    it("should allow subtyping in return types") {

      class A { def x: Int = 1 }

      class B extends A { override def x: Int = 2 }

      object Generator {

        def generate(): B = new B
      }

      def f(x: { def generate(): A }): A = x.generate

      expect(f(Generator).x).toEqual(2)
    }

    it("should allow this.type in return types") {
      type ValueType = { def value: this. type }

      def f(x: ValueType): ValueType = x.value

      class StringValue(x: String) {

        def value: this. type = this

        override def toString(): String = s"StringValue($x)"
      }

      expect(f(new StringValue("foo")).toString).toEqual("StringValue(foo)")
    }

    it("should allow generic return types") {

      case class Tata(name: String)

      object Rec {

        def e(x: Tata): Tata = new Tata("iei")
      }

      def m[T](r: Object { def e(x: Tata): T }): T = r.e(new Tata("foo"))

      expect(m[Tata](Rec).toString).toEqual("Tata(iei)")
    }

    it("should work with unary methods on primitive types") {

      // scalastyle:off disallow.space.before.token

      def fInt(x: Any { def unary_- : Int }): Int = -x
      expect(fInt(1.toByte)).toEqual(-1)
      expect(fInt(1.toShort)).toEqual(-1)
      expect(fInt(1.toChar)).toEqual(-1)
      expect(fInt(1)).toEqual(-1)

      def fLong(x: Any { def unary_- : Long }): Long = -x
      expect(fLong(1L) == - 1L).toBeTruthy

      def fFloat(x: Any { def unary_- : Float }): Float = -x
      expect(fFloat(1.5f)).toEqual(-1.5f)

      def fDouble(x: Any { def unary_- : Double }): Double = -x
      expect(fDouble(1.5)).toEqual(-1.5)

      def fBoolean(x: Any { def unary_! : Boolean }): Boolean = !x
      expect(fBoolean(false)).toBeTruthy
      expect(fBoolean(true)).toBeFalsy
      // scalastyle:on disallow.space.before.token
    }

    it("should work with binary operators on primitive types") {

      def fLong(x: Any { def +(x: Long): Long }): Long = x + 5L
      expect(fLong(5.toByte) == 10L).toBeTruthy
      expect(fLong(10.toShort) == 15L).toBeTruthy
      expect(fLong(10.toChar) == 15L).toBeTruthy
      expect(fLong(-1) == 4L).toBeTruthy
      expect(fLong(17L) == 22L).toBeTruthy

      def fInt(x: Any { def /(x: Int): Int }): Int = x / 7
      expect(fInt(65.toByte)).toEqual(9)
      expect(fInt(15.toShort)).toEqual(2)
      expect(fInt(25.toChar)).toEqual(3)
      expect(fInt(-40)).toEqual(-5)

      def fShort(x: Any { def +(x: Short): Int }): Int = x + 6.toShort
      expect(fShort(65.toByte)).toEqual(71)
      expect(fShort(15.toShort)).toEqual(21)
      expect(fShort(25.toChar)).toEqual(31)
      expect(fShort(-40)).toEqual(-34)

      def fFloat(x: Any { def %(x: Float): Float }): Float = x % 3.4f
      expect(fFloat(5.5f)).toEqual(2.1f)

      def fDouble(x: Any { def /(x: Double): Double }): Double = x / 1.4
      expect(fDouble(-1.5)).toEqual(-1.0714285714285714)

      def fBoolean(x: Any { def &&(x: Boolean): Boolean }): Boolean =
        x && true // scalastyle:ignore
      expect(fBoolean(false)).toBeFalsy
      expect(fBoolean(true)).toBeTruthy
    }

    it("should work with equality operators on primitive types") {

      def fNum(obj: Any { def ==(x: Int): Boolean }): Boolean = obj == 5
      expect(fNum(5.toByte)).toBeTruthy
      expect(fNum(6.toByte)).toBeFalsy
      expect(fNum(5.toShort)).toBeTruthy
      expect(fNum(7.toShort)).toBeFalsy
      expect(fNum(5.toChar)).toBeTruthy
      expect(fNum('r')).toBeFalsy
      expect(fNum(5)).toBeTruthy
      expect(fNum(-4)).toBeFalsy
      expect(fNum(5L)).toBeTruthy
      expect(fNum(400L)).toBeFalsy
      expect(fNum(5.0f)).toBeTruthy
      expect(fNum(5.6f)).toBeFalsy
      expect(fNum(5.0)).toBeTruthy
      expect(fNum(7.9)).toBeFalsy

      def fBool(obj: Any { def ==(x: Boolean): Boolean }): Boolean =
        obj == false // scalastyle:ignore
      expect(fBool(true)).toBeFalsy
      expect(fBool(false)).toBeTruthy

      def fNumN(obj: Any { def !=(x: Int): Boolean }): Boolean = obj != 5
      expect(fNumN(5.toByte)).toBeFalsy
      expect(fNumN(6.toByte)).toBeTruthy
      expect(fNumN(5.toShort)).toBeFalsy
      expect(fNumN(7.toShort)).toBeTruthy
      expect(fNumN(5.toChar)).toBeFalsy
      expect(fNumN('r')).toBeTruthy
      expect(fNumN(5)).toBeFalsy
      expect(fNumN(-4)).toBeTruthy
      expect(fNumN(5L)).toBeFalsy
      expect(fNumN(400L)).toBeTruthy
      expect(fNumN(5.0f)).toBeFalsy
      expect(fNumN(5.6f)).toBeTruthy
      expect(fNumN(5.0)).toBeFalsy
      expect(fNumN(7.9)).toBeTruthy

      def fBoolN(obj: Any { def !=(x: Boolean): Boolean }): Boolean =
        obj != false // scalastyle:ignore
      expect(fBoolN(true)).toBeTruthy
      expect(fBoolN(false)).toBeFalsy
    }

    it("should work with Arrays") {
      type UPD = { def update(i: Int, x: String): Unit }
      type APL = { def apply(i: Int): String }
      type LEN = { def length: Int }
      type CLONE = Any { def clone(): Object }

      def upd(obj: UPD, i: Int, x: String): Unit = obj.update(i, x)

      def apl(obj: APL, i: Int): String = obj.apply(i)

      def len(obj: LEN): Int = obj.length

      def clone(obj: CLONE): Object = obj.clone

      val x = Array("asdf", "foo", "bar")
      val y = clone(x).asInstanceOf[Array[String]]

      expect(len(x)).toEqual(3)
      expect(apl(x, 0)).toEqual("asdf")
      upd(x, 1, "2foo")
      expect(x(1)).toEqual("2foo")
      expect(y(1)).toEqual("foo")
    }

    it("should work with Arrays of primitive values") {
      type UPD = { def update(i: Int, x: Int): Unit }
      type APL = { def apply(i: Int): Int }
      type LEN = { def length: Int }
      type CLONE = Any { def clone(): Object }

      def upd(obj: UPD, i: Int, x: Int): Unit = obj.update(i, x)

      def apl(obj: APL, i: Int): Int = obj.apply(i)

      def len(obj: LEN): Int = obj.length

      def clone(obj: CLONE): Object = obj.clone

      val x = Array(5, 2, 8)
      val y = clone(x).asInstanceOf[Array[Int]]

      expect(len(x)).toEqual(3)
      expect(apl(x, 0)).toEqual(5)
      upd(x, 1, 1000)
      expect(x(1)).toEqual(1000)
      expect(y(1)).toEqual(2)
    }

    it("should work with Strings") {

      def get(obj: { def codePointAt(str: Int): Int }): Int =
        obj.codePointAt(1)
      expect(get("Hi")).toEqual('i'.toInt)

      def sub(x: { def substring(x: Int): AnyRef }): AnyRef = x.substring(5)
      expect(sub("asdfasdfasdf") == "sdfasdf").toBeTruthy

      type LEN_A = { def length: Any }

      def lenA(x: LEN_A): Any = x.length
      expect(lenA("asdf") == 4).toBeTruthy
    }

    it("should properly generate forwarders for inherited methods") {

      trait A {
        def foo: Int
      }

      abstract class B extends A

      class C
          extends B {

        def foo: Int = 1
      }

      def call(x: { def foo: Int }): Int = x.foo

      expect(call(new C)).toEqual(1)
    }

    it("should be bug-compatible with Scala/JVM for inherited overloads") {

      class Base {

        def foo(x: Option[Int]): String = "a"
      }

      class Sub
          extends Base {

        def foo(x: Option[String]): Int = 1
      }

      val sub = new Sub

      val x: { def foo(x: Option[Int]): Any } = sub
      expect(x.foo(Some(1)).asInstanceOf[js.Any])
        .toEqual(1) // here is the "bug"

      val y: { def foo(x: Option[String]): Any } = sub
      expect(y.foo(Some("hello")).asInstanceOf[js.Any]).toEqual(1)
    }

    it("should work on java.lang.Object.{ notify, notifyAll } - #303") {
      type ObjNotifyLike = Any {
        def notify(): Unit
        def notifyAll(): Unit
      }

      def objNotifyTest(obj: ObjNotifyLike): Int = {
        obj.notify()
        obj.notifyAll()
        1
      }

      class A

      expect(objNotifyTest(new A())).toEqual(1)
    }

    it("should work on java.lang.Object.clone - #303") {
      type ObjCloneLike = Any { def clone(): AnyRef }

      def objCloneTest(obj: ObjCloneLike): AnyRef = obj.clone()

      class B(val x: Int)
          extends Cloneable {

        override def clone(): AnyRef = super.clone()
      }

      val b = new B(1)
      val bClone = objCloneTest(b).asInstanceOf[B]

      expect(b eq bClone).toBeFalsy
      expect(bClone.x).toEqual(1)
    }

    it("should work on scala.AnyRef.{ eq, ne } - #303") {
      type ObjEqLike = Any {
        def eq(that: AnyRef): Boolean
        def ne(that: AnyRef): Boolean
      }

      def objEqTest(obj: ObjEqLike, that: AnyRef): Boolean = obj eq that

      def objNeTest(obj: ObjEqLike, that: AnyRef): Boolean = obj ne that

      class A

      val a1 = new A
      val a2 = new A

      expect(objEqTest(a1, a2)).toBeFalsy
      expect(objEqTest(a1, a1)).toBeTruthy

      expect(objNeTest(a1, a2)).toBeTruthy
      expect(objNeTest(a1, a1)).toBeFalsy
    }

    it("should work on java.lang.{Float,Double}.{isNaN,isInfinite}") {
      type FloatingNumberLike = Any {
        def isNaN(): Boolean
        def isInfinite(): Boolean
      }

      def test(
          x: FloatingNumberLike, isNaN: Boolean, isInfinite: Boolean): Unit = {
        expect(x.isNaN()).toEqual(isNaN)
        expect(x.isInfinite()).toEqual(isInfinite)
      }

      test(new JFloat(Float.NaN), true, false)
      test(new JFloat(Float.PositiveInfinity), false, true)
      test(new JFloat(Float.NegativeInfinity), false, true)
      test(new JFloat(54.67), false, false)

      test(new JDouble(Double.NaN), true, false)
      test(new JDouble(Double.PositiveInfinity), false, true)
      test(new JDouble(Double.NegativeInfinity), false, true)
      test(new JDouble(54.67), false, false)
    }

    it("should work with default arguments - #390") {

      def pimpIt(a: Int) =
        new {

          // scalastyle:ignore

          def foo(b: Int, c: Int = 1): Int = a + b + c
        }

      expect(pimpIt(1).foo(2)).toEqual(4)
      expect(pimpIt(2).foo(2, 4)).toEqual(8)
    }

    it("should unbox all types of arguments - #899") {

      class Foo {

        def makeInt: Int = 5

        def testInt(x: Int): Unit = expect(x).toEqual(5)

        def makeRef: Option[String] = Some("hi")

        def testRef(x: Option[String]): Unit =
          expect(x == Some("hi")).toBeTruthy
      }

      /* Note: we should also test with value classes, except that Scala itself
       * does not support value classes as parameters or result type of
       * methods in structural types.
       */

      def test(foo: {
            def makeInt: Int
            def testInt(x: Int): Unit
            def makeRef: Option[String]
            def testRef(x: Option[String]): Unit
          }): Unit = {
        foo.testInt(foo.makeInt)
        foo.testRef(foo.makeRef)
      }

      test(new Foo)
    }
  }
}
