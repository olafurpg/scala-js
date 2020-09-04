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

import scala.scalajs.js

import org.junit.Test
import org.junit.Assert._

class StringBufferJSTest {

  def newBuf: java.lang.StringBuffer =
    new java.lang.StringBuffer

  @Test def append(): Unit =
    assertEquals(newBuf.append(js.undefined).toString, "undefined")

  @Test def insert(): Unit =
    assertEquals(newBuf.insert(0, js.undefined).toString, "undefined")
}

class StringBuilderJSTest {

  def newBuilder: java.lang.StringBuilder =
    new java.lang.StringBuilder

  @Test def append(): Unit = {
    assertEquals(newBuilder.append(js.undefined).toString, "undefined")
  }

  @Test def insert(): Unit =
    assertEquals(newBuilder.insert(0, js.undefined).toString, "undefined")

  @Test def should_allow_string_interpolation_to_survive_null_and_undefined(): Unit =
    assertEquals(s"${js.undefined}", "undefined")
}
