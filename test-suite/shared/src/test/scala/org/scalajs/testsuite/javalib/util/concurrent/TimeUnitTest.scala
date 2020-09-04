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

package org.scalajs.testsuite.javalib.util.concurrent

import java.util.concurrent.TimeUnit

import org.junit.Test
import org.junit.Assert._

class TimeUnitTest {

  @Test def toNanos(): Unit = {
    assertEquals(42L, TimeUnit.NANOSECONDS.toNanos(42L))
    assertEquals(42000L, TimeUnit.MICROSECONDS.toNanos(42L))
    assertEquals(42000000L, TimeUnit.MILLISECONDS.toNanos(42L))
    assertEquals(42000000000L, TimeUnit.SECONDS.toNanos(42L))
    assertEquals(2520000000000L, TimeUnit.MINUTES.toNanos(42L))
    assertEquals(151200000000000L, TimeUnit.HOURS.toNanos(42L))
    assertEquals(3628800000000000L, TimeUnit.DAYS.toNanos(42L))
  }

  @Test def toMicros(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toMicros(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toMicros(42123L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toMicros(42L))
    assertEquals(42000L, TimeUnit.MILLISECONDS.toMicros(42L))
    assertEquals(42000000L, TimeUnit.SECONDS.toMicros(42L))
    assertEquals(2520000000L, TimeUnit.MINUTES.toMicros(42L))
    assertEquals(151200000000L, TimeUnit.HOURS.toMicros(42L))
    assertEquals(3628800000000L, TimeUnit.DAYS.toMicros(42L))
  }

  @Test def toMillis(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toMillis(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toMillis(42000123L))
    assertEquals(0L, TimeUnit.MICROSECONDS.toMillis(42L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toMillis(42123L))
    assertEquals(42L, TimeUnit.MILLISECONDS.toMillis(42L))
    assertEquals(42000L, TimeUnit.SECONDS.toMillis(42L))
    assertEquals(2520000L, TimeUnit.MINUTES.toMillis(42L))
    assertEquals(151200000L, TimeUnit.HOURS.toMillis(42L))
    assertEquals(3628800000L, TimeUnit.DAYS.toMillis(42L))
  }

  @Test def toSeconds(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toSeconds(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toSeconds(42000000123L))
    assertEquals(0L, TimeUnit.MICROSECONDS.toSeconds(42L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toSeconds(42000123L))
    assertEquals(0L, TimeUnit.MILLISECONDS.toSeconds(42L))
    assertEquals(42L, TimeUnit.MILLISECONDS.toSeconds(42123L))
    assertEquals(42L, TimeUnit.SECONDS.toSeconds(42L))
    assertEquals(2520L, TimeUnit.MINUTES.toSeconds(42L))
    assertEquals(151200L, TimeUnit.HOURS.toSeconds(42L))
    assertEquals(3628800L, TimeUnit.DAYS.toSeconds(42L))
  }

  @Test def toMinutes(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toMinutes(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toMinutes(2520000007380L))
    assertEquals(0L, TimeUnit.MICROSECONDS.toMinutes(42L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toMinutes(2520007380L))
    assertEquals(0L, TimeUnit.MILLISECONDS.toMinutes(42L))
    assertEquals(42L, TimeUnit.MILLISECONDS.toMinutes(2520738L))
    assertEquals(0L, TimeUnit.SECONDS.toMinutes(42L))
    assertEquals(42L, TimeUnit.SECONDS.toMinutes(2520L))
    assertEquals(42L, TimeUnit.MINUTES.toMinutes(42L))
    assertEquals(2520L, TimeUnit.HOURS.toMinutes(42L))
    assertEquals(60480L, TimeUnit.DAYS.toMinutes(42L))
  }

  @Test def toHours(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toHours(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toHours(151200000442800L))
    assertEquals(0L, TimeUnit.MICROSECONDS.toHours(42L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toHours(151200442800L))
    assertEquals(0L, TimeUnit.MILLISECONDS.toHours(42L))
    assertEquals(42L, TimeUnit.MILLISECONDS.toHours(151244280L))
    assertEquals(0L, TimeUnit.SECONDS.toHours(42L))
    assertEquals(42L, TimeUnit.SECONDS.toHours(151200L))
    assertEquals(0L, TimeUnit.MINUTES.toHours(42L))
    assertEquals(42L, TimeUnit.MINUTES.toHours(2520L))
    assertEquals(42L, TimeUnit.HOURS.toHours(42L))
    assertEquals(1008L, TimeUnit.DAYS.toHours(42L))
  }

  @Test def toDays(): Unit = {
    assertEquals(0L, TimeUnit.NANOSECONDS.toDays(42L))
    assertEquals(42L, TimeUnit.NANOSECONDS.toDays(3628800010627200L))
    assertEquals(0L, TimeUnit.MICROSECONDS.toDays(42L))
    assertEquals(42L, TimeUnit.MICROSECONDS.toDays(3628810627200L))
    assertEquals(0L, TimeUnit.MILLISECONDS.toDays(42L))
    assertEquals(42L, TimeUnit.MILLISECONDS.toDays(3629862720L))
    assertEquals(0L, TimeUnit.SECONDS.toDays(42L))
    assertEquals(42L, TimeUnit.SECONDS.toDays(3628800L))
    assertEquals(0L, TimeUnit.MINUTES.toDays(42L))
    assertEquals(42L, TimeUnit.MINUTES.toDays(60480L))
    assertEquals(1L, TimeUnit.HOURS.toDays(42L))
    assertEquals(42L, TimeUnit.HOURS.toDays(1008L))
    assertEquals(42L, TimeUnit.DAYS.toDays(42L))
  }

  @Test def values(): Unit = {
    val values = TimeUnit.values()
    assertEquals(7, values.length)
    assertEquals(values(0), TimeUnit.NANOSECONDS)
    assertEquals(values(1), TimeUnit.MICROSECONDS)
    assertEquals(values(2), TimeUnit.MILLISECONDS)
    assertEquals(values(3), TimeUnit.SECONDS)
    assertEquals(values(4), TimeUnit.MINUTES)
    assertEquals(values(5), TimeUnit.HOURS)
    assertEquals(values(6), TimeUnit.DAYS)
  }

  @Test def valueOf(): Unit = {
    assertEquals(TimeUnit.valueOf("NANOSECONDS"), TimeUnit.NANOSECONDS)
    assertEquals(TimeUnit.valueOf("MICROSECONDS"), TimeUnit.MICROSECONDS)
    assertEquals(TimeUnit.valueOf("MILLISECONDS"), TimeUnit.MILLISECONDS)
    assertEquals(TimeUnit.valueOf("SECONDS"), TimeUnit.SECONDS)
    assertEquals(TimeUnit.valueOf("MINUTES"), TimeUnit.MINUTES)
    assertEquals(TimeUnit.valueOf("HOURS"), TimeUnit.HOURS)
    assertEquals(TimeUnit.valueOf("DAYS"), TimeUnit.DAYS)
  }
}
