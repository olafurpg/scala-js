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

import scala.scalajs.js
import scala.scalajs.js.annotation._

import org.junit.Test
import org.junit.Assert._
import org.junit.Assume._

import org.scalajs.testsuite.utils.Platform._

class OptimizerTest {
  import OptimizerTest._

  // Inlineable classes

  @Test def must_update_fields_of_this_in_the_computation_of_other_fields_issue_1153(): Unit = {
    val foo = new InlineClassDependentFields(5)
    assertEquals(5, foo.x)
    assertTrue(foo.b)
    assertEquals(11, foo.y)
  }

  @Test def must_not_break_code_that_assigns_this_to_a_field(): Unit = {
    val foo = new InlineClassThisAlias(5)
    assertEquals(5, foo.z)
  }

  // Optimizer regression tests

  @Test def `must_not_break_*_(-1)_for_Int_issue_1453`(): Unit = {
    @noinline
    def start0: Int = (() => 10) ()

    val start = start0
    val step = -1
    val numRangeElements = start - 1
    val lastElement = start + (numRangeElements - 1) * step
    assertEquals(2, lastElement)
  }

  @Test def `must_not_break_*_(-1)_for_Float_and_Double_issue_1478`(): Unit = {
    @noinline
    def a: Float = (() => 5.0f) ()
    assertEquals(-5.0f, a * -1.0f, 0.0)

    @noinline
    def b: Double = (() => 7.0) ()
    assertEquals(-7.0, b * -1.0, 0.0)
  }

  @Test def must_not_break_foreach_on_downward_Range_issue_1453(): Unit = {
    @noinline
    def start0: Int = (() => 10) ()

    val elements = js.Array[Int]()
    for (i <- start0 to 2 by -1) {
      if (i < 0)
        throw new AssertionError("Going into infinite loop")
      elements.push(i)
    }
    assertArrayEquals(Array(10, 9, 8, 7, 6, 5, 4, 3, 2), elements.toArray)
  }

  @Test def must_not_break_classOf_T_eqeq_classOf_U_issue_1658(): Unit = {
    assertEquals(classOf[String], classOf[String])
    assertEquals(classOf[Int], classOf[Int])
    assertEquals(classOf[Array[Int]], classOf[Array[Int]])
    assertEquals(classOf[Array[String]], classOf[Array[String]])

    assertFalse(classOf[String] == classOf[Int])
    assertFalse(classOf[Seq[_]] == classOf[List[_]])
    assertFalse(classOf[Array[Int]] == classOf[Array[Integer]])
    assertFalse(classOf[Array[Object]] == classOf[Array[Integer]])
    assertFalse(classOf[String] == classOf[Array[String]])
    assertFalse(classOf[Array[Array[Object]]] == classOf[Array[Object]])
  }

  @Test def side_effect_discard_in_eliminated_binding_issue_2467(): Unit = {
    val b = Array.newBuilder[AnyRef]
    def mockPrintln(x: Any): Unit =
      b += ("" + x)

    def get[T](x: T) = { mockPrintln("get: "+ x); x }

    def bn2(a: Int, b: => Int)(c: Int = b) = a + b
    mockPrintln(bn2(b = get(2), a = get(1))()) // should get: 1, 2, 2

    assertArrayEquals(Array[AnyRef]("get: 1", "get: 2", "get: 2", "3"),
        b.result())
  }

  @Test def must_not_break_bitset_oreq_issue_2523(): Unit = {
    import scala.collection.mutable.BitSet

    val b0 = BitSet(5, 6)
    val b1 = BitSet(7)
    val b2 = BitSet(1, 5)
    val b3 = BitSet(6, 7)
    val b4 = BitSet(6, 7)

    b1 |= b0
    assertEquals(b1.toString, "BitSet(5, 6, 7)")
    b2 &= b0
    assertEquals(b2.toString, "BitSet(5)")
    b3 ^= b0
    assertEquals(b3.toString, "BitSet(5, 7)")
    b4 &~= b0
    assertEquals(b4.toString, "BitSet(7)")
    b0 ^= b0 |= b1
    assertEquals(b0.toString, "BitSet(5, 6, 7)")
  }

  @Test def must_not_eliminate_break_to_label_within_finally_block_issue2689(): Unit = {
    // scalastyle:off return
    val logs = js.Array[String]()

    @noinline def log(s: String): Unit =
      logs += s

    def a1(): Unit = log("a1")

    def i1: Int = { log("i1"); 1 }
    def i2: Int = { log("i2"); 2 }

    def e1: Int = { log("e1"); throw new Exception("Boom! #2689") }

    def t4(i: => Int): Int = {
      log("t4")
      try {
        return i
      } finally {
        return i2
      }
    }

    assertEquals(2, t4(i1))
    assertArrayEquals(Array[AnyRef]("t4", "i1", "i2"), logs.toArray[AnyRef])
    logs.clear()

    assertEquals(2, t4(e1))
    assertArrayEquals(Array[AnyRef]("t4", "e1", "i2"), logs.toArray[AnyRef])
    logs.clear()
    // scalastyle:on return
  }

  // === constant folding

  @Test def constant_folding_===(): Unit = {
    @inline def test(expectEq: Boolean, lhs: Any, rhs: Any): Unit = {
      assertEquals(lhs.asInstanceOf[AnyRef] eq (rhs.asInstanceOf[AnyRef]),
          expectEq)
      assertEquals(lhs.asInstanceOf[AnyRef] ne (rhs.asInstanceOf[AnyRef]),
          !expectEq)
    }

    test(true, false, false)
    test(true, 5, 5)
    test(true, 5.toByte, 5.toByte)
    test(true, 5.toByte, 5)
    test(true, 5.0, 5)
    test(true, 5.0f, 5.toShort)
    test(true, classOf[String], classOf[String])
    test(true, "hello", "hello")

    test(false, false, true)
    test(false, 'A', 'A') // they're boxed, so not ===
    test(false, 5, 6)
    test(false, 5.toByte, 6.toByte)
    test(false, 5.toByte, 5L)
    test(false, 5, 5L)
    test(false, 5L, 6L)
    test(false, false, 0)
    test(false, 65, 'A')
    test(false, classOf[String], classOf[Boolean])
    test(false, "hello", "world")

    /* When using BigInts for Longs, equal Longs will be ===, but not when
     * using RuntimeLongs since the instances will be different.
     */
    val usingBigIntForLongs = js.typeOf(5L) == "bigint"
    test(usingBigIntForLongs, 5L, 5L)
  }

  @Test def constant_folding_==(): Unit = {
    @inline def testChar(expectEq: Boolean, lhs: Char, rhs: Char): Unit = {
      assertEquals(lhs == rhs, expectEq)
      assertEquals(lhs != rhs, !expectEq)
    }

    testChar(true, 'A', 'A')
    testChar(false, 'A', 'B')

    @inline def testInt(expectEq: Boolean, lhs: Int, rhs: Int): Unit = {
      assertEquals(lhs == rhs, expectEq)
      assertEquals(lhs != rhs, !expectEq)
    }

    testInt(true, 5, 5)
    testInt(false, 5, 6)

    @inline def testLong(expectEq: Boolean, lhs: Long, rhs: Long): Unit = {
      assertEquals(lhs == rhs, expectEq)
      assertEquals(lhs != rhs, !expectEq)
    }

    testLong(true, 5L, 5L)
    testLong(false, 5L, 6L)

    @inline def testDouble(expectEq: Boolean, lhs: Double, rhs: Double): Unit = {
      assertEquals(lhs == rhs, expectEq)
      assertEquals(lhs != rhs, !expectEq)
    }

    testDouble(true, 5.5, 5.5)
    testDouble(false, 5.5, 6.5)
  }

  // +[string] constant folding

  @Test def must_not_break_when_folding_two_constant_strings(): Unit = {
    @inline def str: String = "I am "
    assertEquals(str + "constant", "I am constant")
  }

  @Test def must_not_break_when_folding_the_empty_string_when_associated_with_a_string(): Unit = {
    @noinline def str: String = "hello"
    assertEquals(str + "", "hello")
    assertEquals("" + str, "hello")
  }

  @Test def `must_not_break_when_folding_1.4f_and_a_stringLit`(): Unit = {
    assertEquals(1.4f + "hello", "1.399999976158142hello")
    assertEquals("hello" + 1.4f, "hello1.399999976158142")
  }

  @Test def must_not_break_when_folding_cascading_+[string](): Unit = {
    @noinline def str: String = "awesome! 10/10"
    assertEquals("Scala.js" + (" is " + str), "Scala.js is awesome! 10/10")
    assertEquals(str + " is " + "Scala.js", "awesome! 10/10 is Scala.js")
  }

  @Test def must_not_break_when_folding_a_chain_of_+[string](): Unit = {
    @inline def b: String = "b"
    @inline def d: String = "d"
    @inline def f: String = "f"
    assertEquals("a" + b + "c" + d + "e" + f + "g", "abcdefg")
  }

  @Test def must_not_break_when_folding_integer_in_double_and_stringLit(): Unit = {
    assertEquals(1.0d + "hello", "1hello")
    assertEquals("hello" + 1.0d, "hello1")
  }

  @Test def must_not_break_when_folding_zero_and_stringLit(): Unit = {
    assertEquals(0.0d + "hello", "0hello")
    assertEquals("hello" + 0.0d, "hello0")
    assertEquals(0.0d + "hello", "0hello")
    assertEquals("hello" + 0.0d, "hello0")
  }

  @Test def must_not_break_when_folding_Infinities_and_stringLit(): Unit = {
    assertEquals(Double.PositiveInfinity + "hello", "Infinityhello")
    assertEquals("hello" + Double.PositiveInfinity, "helloInfinity")
    assertEquals(Double.NegativeInfinity + "hello", "-Infinityhello")
    assertEquals("hello" + Double.NegativeInfinity, "hello-Infinity")
  }

  @Test def must_not_break_when_folding_NaN_and_stringLit(): Unit = {
    assertEquals(Double.NaN + "hello", "NaNhello")
    assertEquals("hello" + Double.NaN, "helloNaN")
  }

  @Test def must_not_break_when_folding_double_with_decimal_and_stringLit(): Unit = {
    assumeFalse("Assumed not executing in FullOpt", isInFullOpt)
    assertEquals(1.2323919403474454E+21d + "hello", "1.2323919403474454e+21hello")
    assertEquals("hello" + 1.2323919403474454E+21d, "hello1.2323919403474454e+21")
  }

  @Test def must_not_break_when_folding_double_that_JVM_would_print_in_scientific_notation_and_stringLit(): Unit = {
    assumeFalse("Assumed not executing in FullOpt", isInFullOpt)
    assertEquals(123456789012345d + "hello", "123456789012345hello")
    assertEquals("hello" + 123456789012345d, "hello123456789012345")
  }

  @Test def must_not_break_when_folding_doubles_to_String(): Unit = {
    assumeFalse("Assumed not executing in FullOpt", isInFullOpt)
    @noinline def toStringNoInline(v: Double): String = v.toString
    @inline def test(v: Double): Unit =
      assertEquals(v.toString, toStringNoInline(v))

    // Special cases
    test(0.0)
    test(-0.0)
    test(Double.NaN)
    test(Double.PositiveInfinity)
    test(Double.NegativeInfinity)

    // k <= n <= 21
    test(1.0)
    test(12.0)
    test(123.0)
    test(1234.0)
    test(12345.0)
    test(123456.0)
    test(1234567.0)
    test(12345678.0)
    test(123456789.0)
    test(1234567890.0)
    test(12345678901.0)
    test(123456789012.0)
    test(1234567890123.0)
    test(12345678901234.0)
    test(123456789012345.0)
    test(1234567890123456.0)
    test(12345678901234657.0)
    test(123456789012345678.0)
    test(1234567890123456789.0)
    test(12345678901234567890.0)
    test(123456789012345678901.0)

    // 0 < n <= 21
    test(1.42)
    test(12.42)
    test(123.42)
    test(1234.42)
    test(12345.42)
    test(123456.42)
    test(1234567.42)
    test(12345678.42)
    test(123456789.42)
    test(1234567890.42)
    test(12345678901.42)
    test(123456789012.42)
    test(1234567890123.42)
    test(12345678901234.42)
    test(123456789012345.42)
    test(1234567890123456.42)
    test(12345678901234657.42)
    test(123456789012345678.42)
    test(1234567890123456789.42)
    test(12345678901234567890.42)
    test(123456789012345678901.42)

    // -6 < n <= 0
    test(0.1)
    test(0.01)
    test(0.001)
    test(0.0001)
    test(0.00001)
    test(0.000001)

    // k == 1
    test(1e22)
    test(2e25)
    test(3e50)
    test(4e100)
    test(5e200)
    test(6e300)
    test(7e307)
    test(1e-22)
    test(2e-25)
    test(3e-50)
    test(4e-100)
    test(5e-200)
    test(6e-300)
    test(7e-307)

    // else
    test(1.42e22)
    test(2.42e25)
    test(3.42e50)
    test(4.42e100)
    test(5.42e200)
    test(6.42e300)
    test(7.42e307)
    test(1.42e-22)
    test(2.42e-25)
    test(3.42e-50)
    test(4.42e-100)
    test(5.42e-200)
    test(6.42e-300)
    test(7.42e-307)

    // special cases when ulp > 1
    test(18271179521433728.0)
    test(1.15292150460684685E18)
    test(1234567890123456770.0)
    test(2234567890123456770.0)
    test(4234567890123450000.0)
    test(149170297077708820000.0)
    test(296938164846899230000.0)
    test(607681513323520000000.0)
  }

  @Test def must_not_break_when_folding_long_and_stringLit(): Unit = {
    assertEquals(1L + "hello", "1hello")
    assertEquals("hello" + 1L, "hello1")
  }

  @Test def must_not_break_when_folding_integer_and_stringLit(): Unit = {
    assertEquals(42 + "hello", "42hello")
    assertEquals("hello" + 42, "hello42")
  }

  @Test def must_not_break_when_folding_boolean_and_stringLit(): Unit = {
    assertEquals("false is not " + true, "false is not true")
  }

  @Test def must_not_break_when_folding_unit_and_stringLit(): Unit = {
    assertEquals("undefined is " + (), "undefined is undefined")
  }

  @Test def must_not_break_when_folding_null_and_stringLit(): Unit = {
    assertEquals("Damien is not " + null, "Damien is not null")
  }

  @Test def must_not_break_when_folding_char_and_stringLit(): Unit = {
    assertEquals('S' + "cala.js", "Scala.js")
    assertEquals("Scala.j" + 's', "Scala.js")
  }

  // Virtualization of JSArrayConstr

  @Test def must_not_break_virtualized_jsarrayconstr(): Unit = {
    @noinline def b = 42

    val a = js.Array[Any]("hello", b)

    assertEquals(2, a.length)
    assertEquals(a(0), "hello")
    assertEquals(a(1), 42)
    assertEquals(a(-1), js.undefined)
    assertEquals(a(2), js.undefined)
  }

  @Test def must_not_break_escaped_jsarrayconstr(): Unit = {
    @noinline def escape[A](a: A): A = a

    val a = js.Array[Any]("hello", 42)

    assertEquals(2, a.length)
    assertEquals(a(0), "hello")
    assertEquals(a(1), 42)
    assertEquals(a(-1), js.undefined)
    assertEquals(a(2), js.undefined)

    assertEquals(2, escape(a).length)
  }

  @Test def must_not_break_modified_jsarrayconstr(): Unit = {
    @noinline def escape[A](a: A): A = a

    val a = js.Array[Any]("hello", 42)

    assertEquals(2, a.length)
    assertEquals(a(0), "hello")
    assertEquals(a(1), 42)
    assertEquals(a(-1), js.undefined)
    assertEquals(a(2), js.undefined)

    a(0) = "bar"

    assertEquals(a(0), "bar")
  }

  @Test def must_not_break_virtualized_jsarrayconstr_in_spread(): Unit = {
    class Foo extends js.Object {
      def check(a: Int, b: String, rest: Any*): Unit = {
        assertEquals(5, a)
        assertEquals(b, "foobar")
        assertEquals(2, rest.length)
        assertEquals(rest(0), "hello")
        assertEquals(rest(1), 42)
      }
    }

    val a = js.Array[Any]("hello", 42)
    val foo = new Foo
    foo.check(5, "foobar", a.toIndexedSeq: _*)
  }

  @Test def must_not_break_virtualized_tuple(): Unit = {
    @noinline def b = 42

    val a = js.Tuple2("hello", b)

    assertEquals(a._1, "hello")
    assertEquals(42, a._2)
  }

  @Test def must_not_break_escaped_tuple(): Unit = {
    @noinline def escape[A](a: A): A = a

    val a = js.Tuple2("hello", 42)

    assertEquals(a._1, "hello")
    assertEquals(42, a._2)

    assertEquals(escape(a)._1, "hello")
  }

  // Bug #3415

  @Test def infinite_recursion_inlining_issue3415_original(): Unit = {
    assumeTrue("linking only", false)
    doWhile1("foo")(f => f(true))
  }

  @inline def doWhile1[Domain2](endDoWhile1: => Domain2)(
      condition2: (Boolean => Domain2) => Domain2): Domain2 = {
    condition2 { (conditionValue2) =>
      if (conditionValue2)
        doWhile1[Domain2](endDoWhile1)(condition2)
      else
        endDoWhile1
    }
  }

  @Test def infinite_recursion_inlining_issue3415_minimized(): Unit = {
    assumeTrue("linking only", false)
    doWhile(???)
  }

  @inline def doWhile(
      condition: js.Function1[js.Function1[Boolean, String], String]): String = {
    condition { (conditionValue: Boolean) =>
      doWhile(condition)
    }
  }

}

object OptimizerTest {

  @inline
  class InlineClassDependentFields(val x: Int) {
    val b = x > 3
    val y = if (b) x + 6 else x-2
  }

  @inline
  class InlineClassThisAlias(val x: Int) {
    val t = this
    val y = x
    val z = t.y
  }

}
