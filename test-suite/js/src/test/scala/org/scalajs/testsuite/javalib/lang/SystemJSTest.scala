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

import org.scalajs.testsuite.utils.Platform

import language.implicitConversions

import scala.scalajs.js
import scala.scalajs.LinkingInfo.assumingES6
import scala.scalajs.runtime.linkingInfo

import org.junit.Test
import org.junit.Assert._

class SystemJSTest {

  @Test def identityHashCode_should_survive_if_an_object_is_sealed(): Unit = {
    /* This is mostly forward-checking that, should we have an implementation
     * that seals Scala.js objects, identityHashCode() survives.
     */
    class HasIDHashCodeToBeSealed

    // Seal before the first call to hashCode()
    val x1 = new HasIDHashCodeToBeSealed
    js.Object.seal(x1.asInstanceOf[js.Object])
    val x1FirstHash = x1.hashCode()
    assertEquals(x1FirstHash, x1.hashCode())

    // Seal after the first call to hashCode()
    val x2 = new HasIDHashCodeToBeSealed
    val x2FirstHash = x2.hashCode()
    js.Object.seal(x2.asInstanceOf[js.Object])
    assertEquals(x2FirstHash, x2.hashCode())
  }

  @Test def identityHashCode_for_JS_objects(): Unit = {
    if (assumingES6 || js.typeOf(js.Dynamic.global.WeakMap) != "undefined") {
      /* This test is more restrictive than the spec, but we know our
       * implementation will always pass the test.
       */
      val x1 = new js.Object
      val x2 = new js.Object
      val x1FirstHash = x1.hashCode()
      assertEquals(x1FirstHash, x1.hashCode())
      assertNotEquals(x1.hashCode(), x2.hashCode())
      assertEquals(x1FirstHash, x1.hashCode())

      assertEquals(x1FirstHash, System.identityHashCode(x1))
      assertEquals(x2.hashCode(), System.identityHashCode(x2))
    } else {
      val x1 = new js.Object
      val x1FirstHash = x1.hashCode()
      assertEquals(x1FirstHash, x1.hashCode())
      assertEquals(x1FirstHash, System.identityHashCode(x1))
    }
  }

  @Test def systemProperties(): Unit = {
    def get(key: String): String = java.lang.System.getProperty(key)

    def trueCount(xs: Boolean*): Int = xs.count(identity)

    // Defined in System.scala

    assertEquals(get("java.version"), "1.8")
    assertEquals(get("java.vm.specification.version"), "1.8")
    assertEquals(get("java.vm.specification.vendor"), "Oracle Corporation")
    assertEquals(get("java.vm.name"), "Scala.js")
    assertEquals(get("java.specification.version"), "1.8")
    assertEquals(get("file.separator"), "/")
    assertEquals(get("path.separator"), ":")
    assertEquals(get("line.separator"), """
""")
    assertEquals(get("java.vm.version"), linkingInfo.linkerVersion)
  }
}
