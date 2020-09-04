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

import org.junit.Assert._
import org.junit.Test

import org.scalajs.testsuite.jsinterop.TimeoutMock
import org.scalajs.testsuite.utils.AssertThrows._

import java.util.Timer
import java.util.TimerTask

import scala.collection._

class TimerTest {
  // Note that we are not currently testing `scheduleAtFixedRate`, because we
  // cannot mock `System.nanoTime`.

  @Test def scheduleOnce(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var completed = false
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          completed = true
        }
      }
      timer.schedule(task, 1000)
      tick(999)
      assertFalse(completed)
      tick(2)
      assertTrue(completed)
      assertFalse(task.cancel())

      val secondTask = new TimerTask {
        def run(): Unit = {}
      }
      assertThrows(classOf[IllegalArgumentException], timer.schedule(secondTask, -1))
    }
  }

  @Test def scheduleOnceCancel(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var completed = false
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          completed = true
        }
      }
      timer.schedule(task, 1000)
      tick(500)
      assertTrue(task.cancel())
      tick(501)
      assertFalse(completed)
    }
  }

  @Test def scheduleOnceAndCancelInRun(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var cancelReturnValue = true
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          cancelReturnValue = this.cancel()
        }
      }
      timer.schedule(task, 1000)
      tick(1000)
      assertFalse(cancelReturnValue)
    }
  }

  @Test def cancelBeforeSchedule(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var completed = false
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          completed = true
        }
      }
      assertFalse(task.cancel())
      assertThrows(classOf[IllegalStateException], timer.schedule(task, 1000))
      tick(2000)
      assertFalse(completed)
    }
  }

  @Test def scheduleFixedDelay(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var seen = mutable.Buffer[Int]()
      val timer = new Timer
      val task = new TimerTask {
        var count = 0
        def run(): Unit = {
          seen += count
          count += 1
        }
      }
      timer.schedule(task, 1000, 100)
      assertEquals(seen, Seq())
      tick(500)
      assertEquals(seen, Seq())
      tick(500)
      assertEquals(seen, Seq(0))
      tick(100)
      assertEquals(seen, Seq(0, 1))
      tick(100)
      assertEquals(seen, Seq(0, 1, 2))
      tick(100)
      assertEquals(seen, Seq(0, 1, 2, 3))
      tick(250)
      assertEquals(seen, Seq(0, 1, 2, 3, 4, 5))
      assertTrue(task.cancel())
      tick(100)
      assertEquals(seen, Seq(0, 1, 2, 3, 4, 5))
      assertFalse(task.cancel())

      val zeroPeriodTask = new TimerTask {
        def run(): Unit = {}
      }
      assertThrows(classOf[IllegalArgumentException],
          timer.schedule(zeroPeriodTask, 1000, 0))

      val secondTask = new TimerTask {
        var count = 6
        def run(): Unit = {
          seen += count
          count += 1
        }
      }
      timer.schedule(secondTask, 0, 100)
      tick(100)
      assertEquals(seen, Seq(0, 1, 2, 3, 4, 5, 6, 7))
      timer.cancel()
      tick(200)
      assertEquals(seen, Seq(0, 1, 2, 3, 4, 5, 6, 7))

      val afterCancelTask = new TimerTask {
        def run(): Unit = {}
      }
      assertThrows(classOf[IllegalStateException],
          timer.schedule(afterCancelTask, 1000, 100))
    }
  }

  @Test def scheduleFixedDelayCancel(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var executed = false
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          executed = true
        }
      }
      timer.schedule(task, 1000, 100)
      tick(500)
      assertTrue(task.cancel())
      assertFalse(task.cancel())
      tick(501)
      assertFalse(executed)
    }
  }

  @Test def scheduleFixedDelayAndCancelInRun(): Unit = {
    TimeoutMock.withMockedTimeout { tick =>
      var cancelReturnValue = false
      val timer = new Timer
      val task = new TimerTask {
        def run(): Unit = {
          cancelReturnValue = this.cancel()
        }
      }
      timer.schedule(task, 1000, 100)
      tick(999)
      assertFalse(cancelReturnValue)
      tick(2)
      assertTrue(cancelReturnValue)
    }
  }
}
