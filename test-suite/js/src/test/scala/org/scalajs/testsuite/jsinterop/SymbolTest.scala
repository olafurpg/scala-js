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
import org.junit.Assume._
import org.junit.{BeforeClass, Test}

object SymbolTest {
  @BeforeClass def assumeSymbolsAreSupported(): Unit = {
    assumeTrue("Assuming JavaScript symbols are supported",
        org.scalajs.testsuite.utils.Platform.jsSymbols)
  }
}

class SymbolTest {

  val namedSymbol = js.Symbol.forKey("namedsym")
  val opaqueSymbolWithDesc = js.Symbol("opaqueSymbolWithDesc")
  val opaqueSymbolWithoutDesc = js.Symbol()

  @Test def typeOf(): Unit = {
    assertEquals(js.typeOf(namedSymbol), "symbol")
    assertEquals(js.typeOf(opaqueSymbolWithDesc), "symbol")
    assertEquals(js.typeOf(opaqueSymbolWithoutDesc), "symbol")
  }

  @Test def keyFor(): Unit = {
    assertEquals(js.Symbol.keyFor(namedSymbol), "namedsym")
    assertEquals(js.Symbol.keyFor(opaqueSymbolWithDesc), js.undefined)
    assertEquals(js.Symbol.keyFor(opaqueSymbolWithoutDesc), js.undefined)
  }

  @Test def identity(): Unit = {
    assertSame(namedSymbol, js.Symbol.forKey("namedsym"))
    assertNotSame(namedSymbol, js.Symbol("namedsym"))
    assertNotSame(opaqueSymbolWithDesc, js.Symbol("opaqueSymbolWithDesc"))
    assertNotSame(opaqueSymbolWithoutDesc, js.Symbol())
  }

  @Test def testToString(): Unit = {
    assertEquals(namedSymbol.toString(), "Symbol(namedsym)")
    assertEquals(opaqueSymbolWithDesc.toString(), "Symbol(opaqueSymbolWithDesc)")
    assertEquals(opaqueSymbolWithoutDesc.toString(), "Symbol()")
  }

  @Test def wellKnownSymbolIterator(): Unit = {
    val sym = js.Symbol.iterator
    assertEquals(js.typeOf(sym), "symbol")
    assertEquals(js.Symbol.keyFor(sym), js.undefined)
    assertEquals(sym.toString(), "Symbol(Symbol.iterator)")
  }

}
