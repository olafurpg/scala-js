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

package org.scalajs.testsuite.javalib.lang.ref

import org.junit.Test
import org.junit.Assert._

class ReferenceTest {

  @Test def should_have_all_the_normal_operations(): Unit = {
    val s = "string"
    val ref = new java.lang.ref.WeakReference(s)
    assertEquals(ref.get, s)
    assertEquals(ref.enqueue, false)
    assertEquals(ref.isEnqueued, false)
    ref.clear()
    assert(ref.get == null)
  }
}
