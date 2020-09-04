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

package org.scalajs.testsuite.javalib.lang.reflect


import scala.scalajs.js

import org.junit.Test
import org.junit.Assert._

class ReflectArrayJSTest {

  @inline
  private def testBase(clazz: Class[_], length: Int, expectedClazz: Class[_],
      sampleElem: Any): Unit = {
    val array =
      java.lang.reflect.Array.newInstance(clazz, length).asInstanceOf[Array[_]]
    assertEquals(array.getClass, expectedClazz)
    assertTrue(array.getClass.isArray)
    assertEquals(length, array.length)
    for (i <- 0 until array.length)
      assertEquals(array(i), sampleElem)
  }

  @noinline
  private def testNewInstanceNoInline(clazz: Class[_], length: Int,
      expectedClazz: Class[_], sampleElem: Any): Unit = {
    testBase(clazz, length, expectedClazz, sampleElem)
  }

  @inline
  def testNewInstance(clazz: Class[_], expectedClazz: Class[_],
      sampleElem: Any): Unit = {
    testNewInstanceNoInline(clazz, length = 2, expectedClazz, sampleElem)
    testBase(clazz, length = 2, expectedClazz, sampleElem)

    testNewInstanceNoInline(clazz, length = 0, expectedClazz, sampleElem)
    testBase(clazz, length = 0, expectedClazz, sampleElem)
  }

  @Test def newInstance(): Unit = {
    testNewInstance(classOf[js.Date], classOf[Array[js.Date]], null)
    testNewInstance(classOf[js.Dictionary[_]], classOf[Array[js.Dictionary[_]]], null)
  }
}
