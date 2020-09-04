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

import java.lang.StringBuilder

import org.junit.Test
import org.junit.Assert._

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.Platform.executingInJVM

import WrappedStringCharSequence.charSequence

/* !!! This test class is basically copy-pasted in StringBufferTest.
 * Make sure to always update them in sync.
 */
class StringBuilderTest {

  private def newBuilder: StringBuilder =
    new StringBuilder

  private def initBuilder(str: String): StringBuilder =
    new StringBuilder(str)

  @Test def init(): Unit = {
    assertEquals(new StringBuilder().toString(), "")
  }

  @Test def initInt(): Unit = {
    assertEquals(new StringBuilder(5).toString(), "")
  }

  @Test def initString(): Unit = {
    assertEquals(new StringBuilder("hello").toString(), "hello")

    if (executingInJVM) {
      expectThrows(classOf[NullPointerException],
          new StringBuilder(null: String))
    }
  }

  @Test def initCharSequence(): Unit = {
    assertEquals(new StringBuilder(charSequence("hello")).toString(), "hello")

    if (executingInJVM) {
      expectThrows(classOf[NullPointerException],
          new StringBuilder(null: CharSequence))
    }
  }

  @Test def appendAnyRef(): Unit = {
    def resultFor(x: AnyRef): String = newBuilder.append(x).toString()

    assertEquals(resultFor(null), "null")
    assertEquals(resultFor(None), "None")
    assertEquals(resultFor("hello"), "hello")
    assertEquals(resultFor(charSequence("foobar")), "foobar")
  }

  @Test def appendString(): Unit = {
    def resultFor(x: String): String = newBuilder.append(x).toString()

    assertEquals(resultFor(null), "null")
    assertEquals(resultFor("hello"), "hello")
  }

  @Test def appendStringBuffer(): Unit = {
    def resultFor(x: StringBuffer): String = newBuilder.append(x).toString()

    assertEquals(resultFor(null), "null")
    assertEquals(resultFor(new StringBuffer()), "")
    assertEquals(resultFor(new StringBuffer("hello")), "hello")
  }

  @Test def appendCharSequence(): Unit = {
    def resultFor(x: CharSequence): String = newBuilder.append(x).toString()

    assertEquals(resultFor(null), "null")
    assertEquals(resultFor("hello"), "hello")
    assertEquals(resultFor(charSequence("")), "")
    assertEquals(resultFor(charSequence("foobar")), "foobar")
  }

  @Test def appendCharSequenceStartEnd(): Unit = {
    def resultFor(x: CharSequence, start: Int, end: Int): String =
      newBuilder.append(x, start, end).toString()

    assertEquals(resultFor(null, 1, 3), "ul")
    assertEquals(resultFor(null, 0, 4), "null")
    assertEquals(resultFor("hello", 1, 5), "ello")
    assertEquals(resultFor(charSequence("foobar"), 2, 4), "ob")

    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor(charSequence("he"), 1, 3))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor(charSequence("he"), -1, 2))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor(charSequence("he"), 2, 1))
  }

  @Test def appendCharArray(): Unit = {
    def resultFor(x: Array[Char]): String = newBuilder.append(x).toString()

    assertEquals(resultFor(Array('h', 'e', 'l', 'l', 'o')), "hello")

    if (executingInJVM)
      expectThrows(classOf[NullPointerException], resultFor(null))
  }

  @Test def appendCharArrayOffsetLen(): Unit = {
    def resultFor(x: Array[Char], offset: Int, len: Int): String =
      newBuilder.append(x, offset, len).toString()

    val arr = Array('h', 'e', 'l', 'l', 'o')
    assertEquals(resultFor(arr, 0, 5), "hello")
    assertEquals(resultFor(arr, 1, 3), "ell")

    if (executingInJVM)
      expectThrows(classOf[NullPointerException], resultFor(null, 0, 0))

    expectThrows(classOf[IndexOutOfBoundsException], resultFor(arr, -1, 2))
    expectThrows(classOf[IndexOutOfBoundsException], resultFor(arr, 3, 3))
    expectThrows(classOf[IndexOutOfBoundsException], resultFor(arr, 3, -2))
  }

  @Test def appendPrimitive(): Unit = {
    assertEquals(newBuilder.append(true).toString, "true")
    assertEquals(newBuilder.append('a').toString, "a")
    assertEquals(newBuilder.append(100000).toString, "100000")
    assertEquals(newBuilder.append(12345678910L).toString, "12345678910")
    assertEquals(newBuilder.append(2.5f).toString, "2.5")
    assertEquals(newBuilder.append(3.5d).toString, "3.5")

    // There is no overload for Byte nor Short; these call the Int version
    assertEquals(newBuilder.append(4.toByte).toString, "4")
    assertEquals(newBuilder.append(304.toShort).toString, "304")
  }

  @Test def appendCodePoint(): Unit = {
    def resultFor(codePoint: Int): String =
      newBuilder.appendCodePoint(codePoint).toString()

    assertEquals(resultFor(97), "a")
    assertEquals(resultFor(65536), "𐀀")
    assertEquals(resultFor(65537), "𐀁")
    assertEquals(resultFor(66561), "𐐁")
    assertEquals(resultFor(1114111), "􏿿")

    expectThrows(classOf[IllegalArgumentException], resultFor(0x111111))
    expectThrows(classOf[IllegalArgumentException], resultFor(-1))
  }

  @Test def delete(): Unit = {
    def resultFor(input: String, start: Int, end: Int): String =
      initBuilder(input).delete(start, end).toString()

    assertEquals(resultFor("hello", 2, 4), "heo")
    assertEquals(resultFor("foo𐀀bar", 4, 7), "foo?r")
    assertEquals(resultFor("hello", 0, 0), "hello")
    assertEquals(resultFor("hello", 5, 5), "hello")
    assertEquals(resultFor("hello", 3, 8), "hel")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("hello", -1, 2))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("hello", 3, 2))
  }

  @Test def deleteCharAt(): Unit = {
    def resultFor(input: String, index: Int): String =
      initBuilder(input).deleteCharAt(index).toString()

    assertEquals(resultFor("0123", 1), "023")
    assertEquals(resultFor("0123", 0), "123")
    assertEquals(resultFor("0123", 3), "012")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("0123", -1))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("0123", 4))
  }

  @Test def replace(): Unit = {
    def resultFor(input: String, start: Int, end: Int, str: String): String =
      initBuilder(input).replace(start, end, str).toString()

    assertEquals(resultFor("0123", 1, 3, "bc"), "0bc3")
    assertEquals(resultFor("0123", 0, 4, "abcd"), "abcd")
    assertEquals(resultFor("0123", 0, 10, "abcd"), "abcd")
    assertEquals(resultFor("0123", 3, 10, "defg"), "012defg")
    assertEquals(resultFor("0123", 0, 1, "xxxx"), "xxxx123")
    assertEquals(resultFor("0123", 1, 1, "xxxx"), "0xxxx123")
    assertEquals(resultFor("0123", 4, 5, "x"), "0123x")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("0123", -1, 3, "x"))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("0123", 4, 3, "x"))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("0123", 5, 8, "x"))

    if (executingInJVM)
      expectThrows(classOf[NullPointerException], resultFor("0123", 1, 3, null))
  }

  @Test def insertCharArrayOffsetLen(): Unit = {
    def resultFor(input: String, index: Int, str: Array[Char], offset: Int,
        len: Int): String = {
      initBuilder(input).insert(index, str, offset, len).toString()
    }

    val arr = Array('a', 'b', 'c', 'd', 'e')

    assertEquals(resultFor("012", 1, arr, 1, 2), "0bc12")
    assertEquals(resultFor("abef", 2, arr, 2, 2), "abcdef")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", -1, arr, 1, 2))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 6, arr, 1, 2))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 1, arr, -1, 2))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 1, arr, 1, -2))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 1, arr, 4, 3))

    if (executingInJVM) {
      expectThrows(classOf[NullPointerException],
          resultFor("1234", 1, null, 0, 0))
    }
  }

  @Test def insertAnyRef(): Unit = {
    def resultFor(input: String, index: Int, x: AnyRef): String =
      initBuilder(input).insert(index, x).toString()

    assertEquals(resultFor("01234", 2, null), "01null234")
    assertEquals(resultFor("01234", 2, None), "01None234")
    assertEquals(resultFor("01234", 2, "hello"), "01hello234")
    assertEquals(resultFor("01234", 2, charSequence("foobar")), "01foobar234")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", -1, "foo"))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 6, "foo"))
  }

  @Test def insertString(): Unit = {
    def resultFor(input: String, index: Int, x: String): String =
      initBuilder(input).insert(index, x).toString()

    assertEquals(resultFor("01234", 2, null), "01null234")
    assertEquals(resultFor("01234", 2, "hello"), "01hello234")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", -1, "foo"))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 6, "foo"))
  }

  @Test def insertCharArray(): Unit = {
    def resultFor(input: String, index: Int, str: Array[Char]): String =
      initBuilder(input).insert(index, str).toString()

    val arr = Array('a', 'b', 'c', 'd', 'e')

    assertEquals(resultFor("012", 1, arr), "0abcde12")
    assertEquals(resultFor("abef", 2, arr), "ababcdeef")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", -1, arr))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("1234", 6, arr))

    if (executingInJVM)
      expectThrows(classOf[NullPointerException], resultFor("1234", 1, null))
  }

  @Test def insertCharSequence(): Unit = {
    def resultFor(input: String, index: Int, x: CharSequence): String =
      initBuilder(input).insert(index, x).toString()

    assertEquals(resultFor("01234", 2, null), "01null234")
    assertEquals(resultFor("01234", 2, "hello"), "01hello234")
    assertEquals(resultFor("01234", 2, charSequence("foobar")), "01foobar234")

    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", -1, "foo"))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 6, "foo"))
  }

  @Test def insertCharSequenceStartEnd(): Unit = {
    def resultFor(input: String, index: Int, x: CharSequence, start: Int,
        end: Int): String = {
      initBuilder(input).insert(index, x, start, end).toString()
    }

    assertEquals(resultFor("01234", 2, null, 1, 3), "01ul234")
    assertEquals(resultFor("01234", 2, "hello", 1, 5), "01ello234")
    assertEquals(resultFor("01234", 2, charSequence("foobar"), 3, 5), "01ba234")

    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", -1, charSequence("foobar"), 1, 3))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 6, charSequence("foobar"), 1, 3))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 1, charSequence("foobar"), -1, 3))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 1, charSequence("foobar"), 2, -1))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 1, charSequence("foobar"), 3, 1))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 1, charSequence("foobar"), 7, 8))
    expectThrows(classOf[IndexOutOfBoundsException],
        resultFor("1234", 1, charSequence("foobar"), 2, 8))
  }

  @Test def insertPrimitive(): Unit = {
    assertEquals(initBuilder("abcd").insert(1, true).toString, "atruebcd")
    assertEquals(initBuilder("abcd").insert(1, 'x').toString, "axbcd")
    assertEquals(initBuilder("abcd").insert(1, 100000).toString, "a100000bcd")
    assertEquals(initBuilder("abcd").insert(1, 12345678910L).toString,
        "a12345678910bcd")
    assertEquals(initBuilder("abcd").insert(1, 2.5f).toString, "a2.5bcd")
    assertEquals(initBuilder("abcd").insert(1, 3.5d).toString, "a3.5bcd")

    // There is no overload for Byte nor Short; these call the Int version
    assertEquals(initBuilder("abcd").insert(1, 4.toByte).toString, "a4bcd")
    assertEquals(initBuilder("abcd").insert(1, 304.toShort).toString, "a304bcd")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        initBuilder("abcd").insert(5, 56))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        initBuilder("abcd").insert(-1, 56))
  }

  @Test def indexOfString(): Unit = {
    def resultFor(input: String, str: String): Int =
      initBuilder(input).indexOf(str)

    assertEquals(2, resultFor("ababcdeabcf", "abc"))
    assertEquals(-1, resultFor("ababcdeabcf", "acb"))
  }

  @Test def indexOfStringInt(): Unit = {
    def resultFor(input: String, str: String, fromIndex: Int): Int =
      initBuilder(input).indexOf(str, fromIndex)

    assertEquals(7, resultFor("ababcdeabcf", "abc", 4))
    assertEquals(2, resultFor("ababcdeabcf", "abc", 2))
    assertEquals(2, resultFor("ababcdeabcf", "abc", -5))
    assertEquals(-1, resultFor("ababcdeabcf", "abc", 10))
    assertEquals(-1, resultFor("ababcdeabcf", "abc", 20))
    assertEquals(-1, resultFor("ababcdeabcf", "acb", 2))
  }

  @Test def lastIndexOfString(): Unit = {
    def resultFor(input: String, str: String): Int =
      initBuilder(input).lastIndexOf(str)

    assertEquals(7, resultFor("ababcdeabcf", "abc"))
    assertEquals(-1, resultFor("ababcdeabcf", "acb"))
  }

  @Test def lastIndexOfStringInt(): Unit = {
    def resultFor(input: String, str: String, fromIndex: Int): Int =
      initBuilder(input).lastIndexOf(str, fromIndex)

    assertEquals(2, resultFor("ababcdeabcf", "abc", 2))
    assertEquals(2, resultFor("ababcdeabcf", "abc", 6))
    assertEquals(7, resultFor("ababcdeabcf", "abc", 8))
    assertEquals(7, resultFor("ababcdeabcf", "abc", 20))
    assertEquals(-1, resultFor("ababcdeabcf", "abc", 1))
    assertEquals(-1, resultFor("ababcdeabcf", "abc", -5))
    assertEquals(-1, resultFor("ababcdeabcf", "acb", 10))
  }

  @Test def reverse(): Unit = {
    def resultFor(input: String): String =
      initBuilder(input).reverse().toString()

    assertEquals(resultFor("123456789"), "987654321")
    assertEquals(resultFor("ab𐐂cd"), "dc𐐂ba")
    assertEquals(resultFor("ab??cd"), "dc??ba")
    assertEquals(resultFor("ab??cd"), "dc??ba")
    assertEquals(resultFor("ab?"), "?ba")
    assertEquals(resultFor("?cd"), "dc?")
  }

  @Test def length(): Unit = {
    assertEquals(5, initBuilder("hello").length())
    assertEquals(6, initBuilder("ab\ud801\udc02cd").length())
  }

  @Test def capacity(): Unit = {
    assertTrue(initBuilder("hello").capacity() >= 5)
    assertTrue(initBuilder("ab\ud801\udc02cd").capacity() >= 6)
  }

  @Test def ensureCapacity(): Unit = {
    // Just make sure it links
    newBuilder.ensureCapacity(10)
  }

  @Test def trimToSize(): Unit = {
    // Just make sure it links
    initBuilder("hello").trimToSize()
  }

  @Test def setLength(): Unit = {
    val b = initBuilder("foobar")

    expectThrows(classOf[StringIndexOutOfBoundsException], b.setLength(-3))

    b.setLength(3)
    assertEquals(b.toString, "foo")
    b.setLength(6)
    assertEquals(b.toString, "foo   ")
  }

  @Test def charAt(): Unit = {
    def resultFor(input: String, index: Int): Char =
      initBuilder(input).charAt(index)

    assertEquals('e', resultFor("hello", 1))
    assertEquals('\ud801', resultFor("ab\ud801\udc02cd", 2))
    assertEquals('\udc02', resultFor("ab\ud801\udc02cd", 3))

    if (executingInJVM) {
      expectThrows(classOf[IndexOutOfBoundsException], resultFor("hello", -1))
      expectThrows(classOf[IndexOutOfBoundsException], resultFor("hello", 5))
      expectThrows(classOf[IndexOutOfBoundsException], resultFor("hello", 6))
    }
  }

  @Test def codePointAt(): Unit = {
    def resultFor(input: String, index: Int): Int =
      initBuilder(input).codePointAt(index)

    assertEquals(0x61, resultFor("abc\ud834\udf06def", 0))
    assertEquals(0x1d306, resultFor("abc\ud834\udf06def", 3))
    assertEquals(0xdf06, resultFor("abc\ud834\udf06def", 4))
    assertEquals(0x64, resultFor("abc\ud834\udf06def", 5))
    assertEquals(0x1d306, resultFor("\ud834\udf06def", 0))
    assertEquals(0xdf06, resultFor("\ud834\udf06def", 1))
    assertEquals(0xd834, resultFor("\ud834abc", 0))
    assertEquals(0xdf06, resultFor("\udf06abc", 0))
    assertEquals(0xd834, resultFor("abc\ud834", 3))

    if (executingInJVM) {
      expectThrows(classOf[IndexOutOfBoundsException],
          resultFor("abc\ud834\udf06def", -1))
      expectThrows(classOf[IndexOutOfBoundsException],
          resultFor("abc\ud834\udf06def", 15))
    }
  }

  @Test def codePointBefore(): Unit = {
    def resultFor(input: String, index: Int): Int =
      initBuilder(input).codePointBefore(index)

    assertEquals(0x61, resultFor("abc\ud834\udf06def", 1))
    assertEquals(0x1d306, resultFor("abc\ud834\udf06def", 5))
    assertEquals(0xd834, resultFor("abc\ud834\udf06def", 4))
    assertEquals(0x64, resultFor("abc\ud834\udf06def", 6))
    assertEquals(0x1d306, resultFor("\ud834\udf06def", 2))
    assertEquals(0xd834, resultFor("\ud834\udf06def", 1))
    assertEquals(0xd834, resultFor("\ud834abc", 1))
    assertEquals(0xdf06, resultFor("\udf06abc", 1))

    if (executingInJVM) {
      expectThrows(classOf[IndexOutOfBoundsException],
          resultFor("abc\ud834\udf06def", 0))
      expectThrows(classOf[IndexOutOfBoundsException],
          resultFor("abc\ud834\udf06def", 15))
    }
  }

  @Test def codePointCount(): Unit = {
    val sb = initBuilder(
        "abc\uD834\uDF06de\uD834\uDF06fgh\uD834ij\uDF06\uD834kl\uDF06")

    assertEquals(18, sb.codePointCount(0, sb.length))
    assertEquals(1, sb.codePointCount(3, 5))
    assertEquals(1, sb.codePointCount(2, 3))
    assertEquals(2, sb.codePointCount(2, 4))
    assertEquals(2, sb.codePointCount(2, 5))
    assertEquals(3, sb.codePointCount(2, 6))
    assertEquals(5, sb.codePointCount(12, 17))
    assertEquals(2, sb.codePointCount(8, 10))
    assertEquals(2, sb.codePointCount(7, 10))
    assertEquals(0, sb.codePointCount(7, 7))
    assertEquals(1, sb.codePointCount(sb.length - 1, sb.length))
    assertEquals(0, sb.codePointCount(sb.length - 1, sb.length - 1))
    assertEquals(0, sb.codePointCount(sb.length, sb.length))

    expectThrows(classOf[IndexOutOfBoundsException], sb.codePointCount(-3, 4))
    expectThrows(classOf[IndexOutOfBoundsException], sb.codePointCount(6, 2))
    expectThrows(classOf[IndexOutOfBoundsException], sb.codePointCount(10, 30))
  }

  @Test def offsetByCodePoints(): Unit = {
    val sb = initBuilder(
        "abc\uD834\uDF06de\uD834\uDF06fgh\uD834ij\uDF06\uD834kl\uDF06")

    assertEquals(sb.length, sb.offsetByCodePoints(0, 18))
    assertEquals(5, sb.offsetByCodePoints(3, 1))
    assertEquals(3, sb.offsetByCodePoints(2, 1))
    assertEquals(5, sb.offsetByCodePoints(2, 2))
    assertEquals(6, sb.offsetByCodePoints(2, 3))
    assertEquals(17, sb.offsetByCodePoints(12, 5))
    assertEquals(10, sb.offsetByCodePoints(8, 2))
    assertEquals(10, sb.offsetByCodePoints(7, 2))
    assertEquals(7, sb.offsetByCodePoints(7, 0))
    assertEquals(sb.length, sb.offsetByCodePoints(sb.length - 1, 1))
    assertEquals(sb.length - 1, sb.offsetByCodePoints(sb.length - 1, 0))
    assertEquals(sb.length, sb.offsetByCodePoints(sb.length, 0))

    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(-3, 4))
    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(6, 18))
    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(30, 2))
  }

  @Test def offsetByCodePointsBackwards(): Unit = {
    val sb = initBuilder(
        "abc\uD834\uDF06de\uD834\uDF06fgh\uD834ij\uDF06\uD834kl\uDF06")

    assertEquals(0, sb.offsetByCodePoints(sb.length, -18))
    assertEquals(3, sb.offsetByCodePoints(5, -1))
    assertEquals(2, sb.offsetByCodePoints(3, -1))
    assertEquals(2, sb.offsetByCodePoints(4, -2))
    assertEquals(2, sb.offsetByCodePoints(5, -2))
    assertEquals(2, sb.offsetByCodePoints(6, -3))
    assertEquals(12, sb.offsetByCodePoints(17, -5))
    assertEquals(7, sb.offsetByCodePoints(10, -2))
    assertEquals(7, sb.offsetByCodePoints(7, -0))
    assertEquals(sb.length - 1, sb.offsetByCodePoints(sb.length, -1))
    assertEquals(sb.length - 1, sb.offsetByCodePoints(sb.length - 1, -0))
    assertEquals(sb.length, sb.offsetByCodePoints(sb.length, -0))

    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(-3, 4))
    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(6, 18))
    expectThrows(classOf[IndexOutOfBoundsException], sb.offsetByCodePoints(30, 2))
  }

  @Test def getChars(): Unit = {
    val dst = new Array[Char](10)
    initBuilder("asdf_foo").getChars(2, 6, dst, 3)
    assertArrayEquals(Array[Char](0, 0, 0, 'd', 'f', '_', 'f', 0, 0, 0), dst)
  }

  @Test def setCharAt(): Unit = {
    def resultFor(input: String, index: Int, ch: Char): String = {
      val sb = initBuilder(input)
      sb.setCharAt(index, ch)
      sb.toString()
    }

    assertEquals(resultFor("foobar", 2, 'x'), "foxbar")
    assertEquals(resultFor("foobar", 5, 'h'), "foobah")

    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("foobar", -1, 'h'))
    expectThrows(classOf[StringIndexOutOfBoundsException],
        resultFor("foobar", 6,  'h'))
  }

  @Test def substringStart(): Unit = {
    def resultFor(input: String, start: Int): String =
      initBuilder(input).substring(start)

    assertEquals(resultFor("hello", 2), "llo")
    assertEquals(resultFor("hello", 5), "")

    if (executingInJVM) {
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", -1))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 8))
    }
  }

  @Test def subSequence(): Unit = {
    def resultFor(input: String, start: Int, end: Int): CharSequence =
      initBuilder(input).subSequence(start, end)

    /* Note that the spec of subSequence says that it behaves exactly like
     * substring. Therefore, the returned CharSequence must necessarily be a
     * String.
     */
    assertEquals(resultFor("hello", 2, 4), "ll")
    assertEquals(resultFor("hello", 5, 5), "")
    assertEquals(resultFor("hello", 0, 3), "hel")

    if (executingInJVM) {
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", -1, 3))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 8, 8))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 3, 2))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 3, 8))
    }
  }

  @Test def substringStartEnd(): Unit = {
    def resultFor(input: String, start: Int, end: Int): String =
      initBuilder(input).substring(start, end)

    assertEquals(resultFor("hello", 2, 4), "ll")
    assertEquals(resultFor("hello", 5, 5), "")
    assertEquals(resultFor("hello", 0, 3), "hel")

    if (executingInJVM) {
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", -1, 3))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 8, 8))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 3, 2))
      expectThrows(classOf[StringIndexOutOfBoundsException],
          resultFor("hello", 3, 8))
    }
  }

  @Test def should_allow_string_interpolation_to_survive_null_and_undefined(): Unit = {
    assertEquals(s"a${null}b", "anullb")

    if (executingInJVM)
      assertEquals(s"a${()}b", "a()b")
    else
      assertEquals(s"a${()}b", "aundefinedb")
  }
}
