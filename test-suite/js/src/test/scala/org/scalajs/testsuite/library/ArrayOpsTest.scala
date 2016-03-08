/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.library

import scala.scalajs.js
import org.scalajs.jasminetest.JasmineTest

import scala.reflect.ClassTag

import js.JSConverters._

object ArrayOpsTest extends JasmineTest {
  describe("scala.scalajs.js.ArrayOps") {
    // Methods we actually implement

    it("should implement apply") {
      val array = js.Array(3, 4, 5, 6, 3, 4)
      val ops: js.ArrayOps[Int] = array

      expect(ops(0)).toEqual(3)
      expect(ops(3)).toEqual(6)

      array (0) = 4
      expect(ops(0)).toEqual(4)
    }

    it("should implement update") {
      val array = js.Array(3, 4, 5, 6, 3, 4)
      val ops: js.ArrayOps[Int] = array

      expect(array(1)).toEqual(4)
      ops (1) = 5
      expect(array(1)).toEqual(5)

      ops (5) = 10
      expect(array(5)).toEqual(10)
    }

    it("should implement length") {
      val array = js.Array(3, 4, 5, 6, 3, 4)
      val ops: js.ArrayOps[Int] = array

      expect(ops.length).toEqual(6)
      array.push(1)
      expect(ops.length).toEqual(7)
    }

    it("should implement seq") {
      val array = js.Array(3, 4, 5, 6, 3, 4)
      val ops: js.ArrayOps[Int] = array
      val seq = ops.seq

      expect(seq.toList == List(3, 4, 5, 6, 3, 4)).toBeTruthy
    }

    it("should implement reduceLeft") {
      val array = js.Array(100, 6, 2, 56, -1)
      expect(array.reduceLeft(_ - _)).toEqual(37)
      expect(() => js.Array[Int]().reduceLeft(_ + _)).toThrow
    }

    it("should implement reduceRight") {
      val array = js.Array("hello", "world")
      expect(array.reduceRight(_ + ", " + _)).toEqual("hello, world")
      expect(() => js.Array[Int]().reduceRight(_ + _)).toThrow
    }

    it("should implement ++") {
      val left = js.Array("hello", "world")
      val right = js.Array("and", "everyone", "else")
      expect(left ++ right)
        .toEqual(js.Array("hello", "world", "and", "everyone", "else"))

      val ints = js.Array(4, 3)
      expect(left ++ ints).toEqual(js.Array("hello", "world", 4, 3))
    }

    // Some arbitrary methods to test the builders

    it("should implement collect") {

      def ct[A : ClassTag](x: A): ClassTag[A] = implicitly[ClassTag[A]]
      val array = js.Array(3, 4, 5, 6, 3, 4)
      val res = array.collect {
        case x if x > 4 => 2 * x
      }

      expect(ct(res).runtimeClass == classOf[js.Array[Int]]).toBeTruthy
      expect(res).toEqual(js.Array(10, 12))
    }

    it("should implement diff") {
      val array = js.Array(1, 2, 1, 3, 1, 10, 9)
      val diff = array.diff(Seq(1, 3, 9))
      expect(diff).toEqual(js.Array(2, 1, 1, 10))
    }

    it("should implement toList - #843") {
      val array = js.Array(1, 2, 1, 3, 1, 10, 9)
      val list = array.toList
      expect(list.toJSArray).toEqual(array)
    }

    it("should implement to[T] - #843") {
      val array = js.Array(1, 2, 1, 3, 1, 10, 9)
      val list = array.to[List]
      expect(list.toJSArray).toEqual(array)
    }
  }
}
