/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.compiler

import java.lang.Cloneable
import java.io.Serializable

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName
import org.scalajs.jasminetest.JasmineTest

import scala.util.{Try, Failure}

object RuntimeTypesTest extends JasmineTest {
  describe("Scala Arrays") {
    it("are instances of Serializable and Cloneable - #2094") {
      expect((Array(3): Any).isInstanceOf[Serializable]).toBeTruthy
      expect((Array(3): Any).isInstanceOf[Cloneable]).toBeTruthy
      expect((Array("hello"): Any).isInstanceOf[Serializable]).toBeTruthy
      expect((Array("hello"): Any).isInstanceOf[Cloneable]).toBeTruthy
    }

    it("cast to Serializable and Cloneable - #2094") {
      expect(() => (Array(3): Any).asInstanceOf[Serializable]).not.toThrow
      expect(() => (Array(3): Any).asInstanceOf[Cloneable]).not.toThrow
      expect(() => (Array("hello"): Any).asInstanceOf[Serializable]).not.toThrow
      expect(() => (Array("hello"): Any).asInstanceOf[Cloneable]).not.toThrow
    }
  }

  describe("scala.Nothing") {
    when("compliant-asinstanceofs").it("casts to scala.Nothing should fail") {

      def test(x: Any): Unit = {
        try {
          x.asInstanceOf[Nothing]
          fail("casting " + x + " to Nothing did not fail")
        } catch {
          case th: Throwable =>
            expect(th.isInstanceOf[ClassCastException]).toBeTruthy
            expect(th.getMessage)
              .toEqual(x + " is not an instance of scala.runtime.Nothing$")
        }
      }
      test("a")
      test(null)
    }

    when("compliant-asinstanceofs")
      .it("reflected casts to scala.Nothing should fail") {

      def test(x: Any): Unit = {
        try {
          classOf[Nothing].cast(x)
          fail("casting " + x + " to Nothing did not fail")
        } catch {
          case th: Throwable =>
            expect(th.isInstanceOf[ClassCastException]).toBeTruthy
            expect(th.getMessage)
              .toEqual(x + " is not an instance of scala.runtime.Nothing$")
        }
      }
      test("a")
      test(null)
    }

    it("Array[Nothing] should be allowed to exists and be castable") {
      val arr = Array[Nothing]()
      arr.asInstanceOf[Array[Nothing]]
    }

    it("Array[Array[Nothing]], too") {
      val arr = Array[Array[Nothing]]()
      arr.asInstanceOf[Array[Array[Nothing]]]
      // This apparently works too... Dunno why
      arr.asInstanceOf[Array[Nothing]]
    }
  }

  describe("scala.Null") {
    when("compliant-asinstanceofs")
      .it("casts to scala.Null should fail for everything else but null") {
      val msg = Try("a".asInstanceOf[Null]) match {
        case Failure(thr: ClassCastException) => thr.getMessage
        case _ => "not failed"
      }
      expect(msg).toEqual("a is not an instance of scala.runtime.Null$")
    }

    it("classTag of scala.Null should contain proper Class[_] - #297") {
      val tag = scala.reflect.classTag[Null]
      expect(tag.runtimeClass != null).toBeTruthy
      expect(tag.runtimeClass.getName).toEqual("scala.runtime.Null$")
    }

    it("casts to scala.Null should succeed on null") {
      null.asInstanceOf[Null]
    }

    it("Array[Null] should be allowed to exist and be castable") {
      val arr = Array.fill[Null](5)(null)
      arr.asInstanceOf[Array[Null]]
    }

    it("Array[Array[Null]] too") {
      val arr = Array.fill[Null](5, 5)(null)
      arr.asInstanceOf[Array[Array[Null]]]
      // This apparently works too... Dunno why
      arr.asInstanceOf[Array[Null]]
    }
  }

  describe("Raw JS types") {
    it("Arrays of raw JS types") {
      val arrayOfParentJSType = new Array[ParentJSType](0)
      val arrayOfJSInterface = new Array[SomeJSInterface](0)
      val arrayOfJSClass = new Array[SomeJSClass](0)

      expect(arrayOfParentJSType.isInstanceOf[Array[AnyRef]]).toBeTruthy
      expect(arrayOfJSInterface.isInstanceOf[Array[AnyRef]]).toBeTruthy
      expect(arrayOfJSClass.isInstanceOf[Array[AnyRef]]).toBeTruthy

      expect(arrayOfParentJSType.isInstanceOf[Array[ParentJSType]]).toBeTruthy
      expect(arrayOfJSInterface.isInstanceOf[Array[SomeJSInterface]]).toBeTruthy
      expect(arrayOfJSClass.isInstanceOf[Array[SomeJSClass]]).toBeTruthy

      expect(arrayOfJSInterface.isInstanceOf[Array[ParentJSType]]).toBeTruthy
      expect(arrayOfJSClass.isInstanceOf[Array[ParentJSType]]).toBeTruthy

      expect(arrayOfParentJSType.isInstanceOf[Array[SomeJSInterface]]).toBeFalsy
      expect(arrayOfParentJSType.isInstanceOf[Array[SomeJSClass]]).toBeFalsy

      expect(arrayOfJSInterface.isInstanceOf[Array[js.Object]]).toBeFalsy
      expect(arrayOfJSClass.isInstanceOf[Array[js.Object]]).toBeTruthy
    }
  }

  @js.native
  trait ParentJSType extends js.Object

  @js.native
  trait SomeJSInterface extends ParentJSType

  @JSName("SomeJSClass")
  @js.native
  class SomeJSClass extends ParentJSType
}
