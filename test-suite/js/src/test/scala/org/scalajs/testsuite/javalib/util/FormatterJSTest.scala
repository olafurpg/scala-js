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

package org.scalajs.testsuite.javalib.util

import scala.scalajs.js

import org.junit.Assert._
import org.junit.Test

import java.util.Formatter

class FormatterJSTest {

  @Test def `should_survive_undefined`(): Unit = {
    val fmt = new Formatter()
    val res = fmt.format("%s", js.undefined).toString()
    fmt.close()
    assertEquals(res, "undefined")
  }

  @Test def `should_allow_f_string_interpolation_to_survive_undefined`(): Unit = {
    assertEquals(f"${js.undefined}%s", "undefined")
  }
}
