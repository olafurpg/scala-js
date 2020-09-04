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
import js.annotation._

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.Platform._

import org.junit.Assert._
import org.junit.Assume._
import org.junit.Test

class JSExportStaticTest {
  // Methods

  @Test def toplevel_basic_static_method_export(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    assertEquals(statics.basic(), 1)
  }

  @Test def toplevel_overloaded_static_method_export(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    assertEquals(statics.overload("World"), "Hello World")
    assertEquals(statics.overload(2), 2)
    assertEquals(statics.overload(2, 7), 9)
    assertEquals(statics.overload(1, 2, 3, 4), 10)
  }

  @Test def toplevel_renamed_static_method_export(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    assertEquals(statics.renamed(8), 11)
  }

  @Test def toplevel_renamed_overloaded_static_method_export(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    assertEquals(statics.renamedOverload("World"), "Hello World")
    assertEquals(statics.renamedOverload(2), 2)
    assertEquals(statics.renamedOverload(2, 7), 9)
    assertEquals(statics.renamedOverload(1, 2, 3, 4), 10)
  }

  @Test def toplevel_static_method_export_constructor(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    assertEquals(statics.constructor(12), 24)
  }

  @Test def toplevel_static_method_export_uses_unique_object(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]

    statics.setMyVar(3)
    assertEquals(3, TopLevelStaticExportMethods.myVar)
    statics.setMyVar(7)
    assertEquals(7, TopLevelStaticExportMethods.myVar)
  }

  @Test def toplevel_static_method_export_also_exists_in_member(): Unit = {
    val statics = js.constructorOf[TopLevelStaticExportMethods]
    assertEquals(statics.alsoExistsAsMember(3), 15)

    val obj = new TopLevelStaticExportMethods
    assertEquals(6, obj.alsoExistsAsMember(3))
  }

  @Test def nested_basic_static_method_export(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    assertEquals(statics.basic(), 1)
  }

  @Test def nested_overloaded_static_method_export(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    assertEquals(statics.overload("World"), "Hello World")
    assertEquals(statics.overload(2), 2)
    assertEquals(statics.overload(2, 7), 9)
    assertEquals(statics.overload(1, 2, 3, 4), 10)
  }

  @Test def nested_renamed_static_method_export(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    assertEquals(statics.renamed(8), 11)
  }

  @Test def nested_renamed_overloaded_static_method_export(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    assertEquals(statics.renamedOverload("World"), "Hello World")
    assertEquals(statics.renamedOverload(2), 2)
    assertEquals(statics.renamedOverload(2, 7), 9)
    assertEquals(statics.renamedOverload(1, 2, 3, 4), 10)
  }

  @Test def nested_static_method_export_constructor(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    assertEquals(statics.constructor(12), 24)
  }

  @Test def nested_static_method_export_uses_unique_object(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]

    statics.setMyVar(3)
    assertEquals(3, JSExportStaticTest.StaticExportMethods.myVar)
    statics.setMyVar(7)
    assertEquals(7, JSExportStaticTest.StaticExportMethods.myVar)
  }

  @Test def nested_static_method_export_also_exists_in_member(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportMethods]
    assertEquals(statics.alsoExistsAsMember(3), 15)

    val obj = new JSExportStaticTest.StaticExportMethods
    assertEquals(6, obj.alsoExistsAsMember(3))
  }

  // Properties

  @Test def basic_static_prop_readonly(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertEquals(statics.basicReadOnly, 1)
  }

  @Test def basic_static_prop_readwrite(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertEquals(statics.basicReadWrite, 5)
    statics.basicReadWrite = 10
    assertEquals(statics.basicReadWrite, 15)
  }

  @Test def static_prop_set_wrong_type_throws_classcastexception(): Unit = {
    assumeTrue("assuming compliant asInstanceOfs", hasCompliantAsInstanceOfs)

    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertThrows(classOf[ClassCastException], {
      statics.basicReadWrite = "wrong type"
    })
  }

  @Test def overloaded_static_prop_setter(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertEquals(statics.overloadedSetter, "got: ")
    statics.overloadedSetter = "foo"
    assertEquals(statics.overloadedSetter, "got: foo")
    statics.overloadedSetter = 5
    assertEquals(statics.overloadedSetter, "got: foo10")
  }

  @Test def overloaded_static_prop_renamed(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertEquals(statics.renamed, 5)
    statics.renamed = 10
    assertEquals(statics.renamed, 15)
    statics.renamed = "foobar"
    assertEquals(statics.renamed, 21)
  }

  @Test def static_prop_constructor(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]

    assertEquals(statics.constructor, 102)
  }

  @Test def static_prop_also_exists_in_member(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportProperties]
    assertEquals(statics.alsoExistsAsMember, "also a member")

    val obj = new JSExportStaticTest.StaticExportProperties
    assertEquals(54, obj.alsoExistsAsMember)
  }

  // Fields

  @Test def basic_field(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportFields]

    // Initialization
    assertEquals(statics.basicVal, 5)
    assertEquals(statics.basicVar, "hello")

    // JS modifies var
    statics.basicVar = "hello world"
    assertEquals(statics.basicVar, "hello world")
    assertEquals(JSExportStaticTest.StaticExportFields.basicVar, "hello world")

    // Scala modifies var
    JSExportStaticTest.StaticExportFields.basicVar = "modified once more"
    assertEquals(JSExportStaticTest.StaticExportFields.basicVar,
        "modified once more")
    assertEquals(statics.basicVar, "modified once more")

    // Reset var
    JSExportStaticTest.StaticExportFields.basicVar = "hello"
  }

  @Test def read_tampered_var_causes_class_cast_exception(): Unit = {
    assumeTrue("assuming compliant asInstanceOfs", hasCompliantAsInstanceOfs)

    val statics = js.constructorOf[JSExportStaticTest.StaticExportFields]

    // JS modifies var with an incorrect type
    statics.basicVar = 42
    assertThrows(classOf[ClassCastException], {
      assertEquals(JSExportStaticTest.StaticExportFields.basicVar, 42)
    })

    // Reset var
    JSExportStaticTest.StaticExportFields.basicVar = "hello"
  }

  @Test def renamed_field(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportFields]

    // Initialization
    assertEquals(statics.renamedVal, 6)
    assertEquals(statics.renamedVar, "world")

    // JS modifies var
    statics.renamedVar = "hello world"
    assertEquals(statics.renamedVar, "hello world")
    assertEquals(JSExportStaticTest.StaticExportFields.renamedBasicVar,
        "hello world")

    // Scala modifies var
    JSExportStaticTest.StaticExportFields.renamedBasicVar = "modified once more"
    assertEquals(JSExportStaticTest.StaticExportFields.renamedBasicVar,
        "modified once more")
    assertEquals(statics.renamedVar, "modified once more")

    // Reset var
    JSExportStaticTest.StaticExportFields.renamedBasicVar = "world"
  }

  @Test def uninitialized_fields(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportFields]

    assertEquals(0, JSExportStaticTest.StaticExportFields.uninitializedVarInt)
    assertEquals(statics.uninitializedVarInt, 0)

    assertEquals(JSExportStaticTest.StaticExportFields.uninitializedVarString,
        null)
    assertEquals(statics.uninitializedVarString, null)

    assertEquals('\u0000',
        JSExportStaticTest.StaticExportFields.uninitializedVarChar)
    assertEquals(statics.uninitializedVarChar, ' ')
  }

  @Test def field_also_exists_in_member(): Unit = {
    val statics = js.constructorOf[JSExportStaticTest.StaticExportFields]
    assertEquals(statics.alsoExistsAsMember, "hello")

    val obj = new JSExportStaticTest.StaticExportFields
    assertEquals(5, obj.alsoExistsAsMember)
  }

}

class TopLevelStaticExportMethods extends js.Object {
  def alsoExistsAsMember(x: Int): Int = x * 2
}

object TopLevelStaticExportMethods {
  @JSExportStatic
  def basic(): Int = 1

  @JSExportStatic
  def overload(x: String): String = "Hello " + x

  @JSExportStatic
  def overload(x: Int, y: Int*): Int = x + y.sum

  @JSExportStatic("renamed")
  def renamedMethod(x: Int): Int = x + 3

  @JSExportStatic
  def renamedOverload(x: String): String = "Hello " + x

  @JSExportStatic("renamedOverload")
  def renamedOverloadedMethod(x: Int, y: Int*): Int = x + y.sum

  @JSExportStatic
  def constructor(x: Int): Int = 2 * x

  var myVar: Int = _

  @JSExportStatic
  def setMyVar(x: Int): Unit = myVar = x

  @JSExportStatic
  def alsoExistsAsMember(x: Int): Int = x * 5
}

object JSExportStaticTest {
  class StaticExportMethods extends js.Object {
    def alsoExistsAsMember(x: Int): Int = x * 2
  }

  object StaticExportMethods {
    @JSExportStatic
    def basic(): Int = 1

    @JSExportStatic
    def overload(x: String): String = "Hello " + x

    @JSExportStatic
    def overload(x: Int, y: Int*): Int = x + y.sum

    @JSExportStatic("renamed")
    def renamedMethod(x: Int): Int = x + 3

    @JSExportStatic
    def renamedOverload(x: String): String = "Hello " + x

    @JSExportStatic("renamedOverload")
    def renamedOverloadedMethod(x: Int, y: Int*): Int = x + y.sum

    @JSExportStatic
    def constructor(x: Int): Int = 2 * x

    var myVar: Int = _

    @JSExportStatic
    def setMyVar(x: Int): Unit = myVar = x

    @JSExportStatic
    def alsoExistsAsMember(x: Int): Int = x * 5
  }

  class StaticExportProperties extends js.Object {
    def alsoExistsAsMember: Int = 54
  }

  object StaticExportProperties {
    @JSExportStatic
    def basicReadOnly: Int = 1

    private var basicVar: Int = 5

    @JSExportStatic
    def basicReadWrite: Int = basicVar

    @JSExportStatic
    def basicReadWrite_=(v: Int): Unit = basicVar += v

    private var overloadedSetterVar: String = ""

    @JSExportStatic
    def overloadedSetter: String = "got: " + overloadedSetterVar

    @JSExportStatic
    def overloadedSetter_=(x: String): Unit = overloadedSetterVar += x

    @JSExportStatic
    def overloadedSetter_=(x: Int): Unit = overloadedSetterVar += 2 * x

    private var renamedPropVar: Int = 5

    @JSExportStatic("renamed")
    def renamedProp: Int = renamedPropVar

    @JSExportStatic("renamed")
    def renamedProp_=(v: Int): Unit = renamedPropVar += v

    @JSExportStatic("renamed")
    def renamedOverload_=(x: String): Unit = renamedPropVar += x.length

    @JSExportStatic
    def constructor: Int = 102

    @JSExportStatic
    def alsoExistsAsMember: String = "also a member"
  }

  class StaticExportFields extends js.Object {
    val alsoExistsAsMember: Int = 5
  }

  object StaticExportFields {
    @JSExportStatic
    val basicVal: Int = 5

    @JSExportStatic
    var basicVar: String = "hello"

    @JSExportStatic("renamedVal")
    val renamedBasicVal: Int = 6

    @JSExportStatic("renamedVar")
    var renamedBasicVar: String = "world"

    @JSExportStatic
    var uninitializedVarInt: Int = _

    @JSExportStatic
    var uninitializedVarString: String = _

    @JSExportStatic
    var uninitializedVarChar: Char = _

    @JSExportStatic
    val alsoExistsAsMember: String = "hello"
  }
}
