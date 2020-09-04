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

import org.junit.Test
import org.junit.Assert._

import org.scalajs.testsuite.utils.Platform.executingInJVM

class ThreadTest {

  @Test def getName_and_setName(): Unit = {
    if (!executingInJVM) {
      val t = Thread.currentThread()
      assertEquals(t.getName, "main") // default name of the main thread
      t.setName("foo")
      try {
        assertEquals(t.getName, "foo")
      } finally {
        t.setName("main") // don't pollute the rest of the world with this test
      }
      assertEquals(t.getName, "main")
    }
  }

  @Test def currentThread_getStackTrace(): Unit = {
    Thread.currentThread().getStackTrace()
  }

  @Test def getId(): Unit = {
    assertTrue(Thread.currentThread().getId > 0)
  }

  @Test def interrupt_exist_and_the_status_is_properly_reflected(): Unit = {
    val t = Thread.currentThread()
    assertFalse(t.isInterrupted())
    assertFalse(Thread.interrupted())
    assertFalse(t.isInterrupted())
    t.interrupt()
    assertTrue(t.isInterrupted())
    assertTrue(Thread.interrupted())
    assertFalse(t.isInterrupted())
    assertFalse(Thread.interrupted())
  }
}
