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

import org.junit.Test
import org.junit.Assert._

import scala.scalajs.js
import scala.scalajs.js.annotation._

package object packageobjectwithnatives {
  @js.native
  @JSGlobal
  object JSNativeObjectInPackageFoo extends js.Object

  @js.native
  @JSGlobal("JSNativeObjectInPackageBar")
  object JSNativeObjectInPackageBaz extends js.Object

  @js.native
  @JSGlobal
  class JSNativeClassInPackageFoo extends js.Object {
    def foo(): String = js.native
  }

  @js.native
  @JSGlobal("JSNativeClassInPackageBar")
  class JSNativeClassInPackageBaz extends js.Object {
    def baz(): String = js.native
  }
}

class JSNativeInPackage {
  import packageobjectwithnatives._
  import js.Dynamic.global

  @Test def testObjectDefaultJSGlobal(): Unit = {
    assertNotEquals("undefined", js.typeOf(global.JSNativeObjectInPackageFoo))
    assertSame(JSNativeObjectInPackageFoo, global.JSNativeObjectInPackageFoo)
  }

  @Test def testObjectJSGlobal(): Unit = {
    assertNotEquals("undefined", js.typeOf(global.JSNativeObjectInPackageBar))
    assertEquals(js.typeOf(global.JSNativeObjectInPackageBaz), "undefined")
    assertSame(JSNativeObjectInPackageBaz, global.JSNativeObjectInPackageBar)
  }

  @Test def testClassDefaultJSGlobal(): Unit = {
    assertNotEquals("undefined", js.typeOf(global.JSNativeClassInPackageFoo))
    assertEquals(global.JSNativeClassInPackageFoo,
        js.constructorOf[JSNativeClassInPackageFoo])

    val gJSNativeClassInPackageFoo =
      js.Dynamic.newInstance(global.JSNativeClassInPackageFoo)()
    assertEquals(gJSNativeClassInPackageFoo.foo(), "foo")
    assertEquals(new JSNativeClassInPackageFoo().foo(), "foo")
  }

  @Test def testClassJSGlobal(): Unit = {
    assertNotEquals("undefined", js.typeOf(global.JSNativeClassInPackageBar))
    assertSame(js.constructorOf[JSNativeClassInPackageBaz],
        global.JSNativeClassInPackageBar)
    assertEquals(js.typeOf(global.JSNativeClassInPackageBaz), "undefined")

    val gJSNativeClassInPackageBar =
      js.Dynamic.newInstance(global.JSNativeClassInPackageBar)()
    assertEquals(gJSNativeClassInPackageBar.baz(), "baz")
    assertEquals(new JSNativeClassInPackageBaz().baz(), "baz")
  }
}
