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

package org.scalajs.testsuite.scalalib

import org.junit.Test
import org.junit.Assert._

class ArrayTest {
  @Test def unapplySeq_issue_3445(): Unit = {
    val args: Array[String] = Array("foo", "bar", "foobar")
    val Array(x, xs @ _*) = args
    assertEquals(x, "foo")
    assertEquals(xs, Seq("bar", "foobar"))
  }
}
