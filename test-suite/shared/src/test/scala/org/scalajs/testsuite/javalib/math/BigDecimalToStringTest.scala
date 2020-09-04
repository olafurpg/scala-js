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

package org.scalajs.testsuite.javalib.math

import java.math.{BigInteger, BigDecimal}

import org.junit.Test
import org.junit.Assert._

class BigDecimalToStringTest {

  @Test def testToStringWithCornerCaseScales(): Unit = {
    val bigIntOne = BigInteger.valueOf(1)

    assertEquals(new BigDecimal(bigIntOne, 0).toString(), "1")

    assertEquals(new BigDecimal(bigIntOne, 2).toString(), "0.01")
    assertEquals(new BigDecimal(bigIntOne, 6).toString(), "0.000001")
    assertEquals(new BigDecimal(bigIntOne, 7).toString(), "1E-7")
    assertEquals(new BigDecimal(bigIntOne, 2147483647).toString(), "1E-2147483647")

    assertEquals(new BigDecimal(bigIntOne, -1).toString(), "1E+1")
    assertEquals(new BigDecimal(bigIntOne, -2).toString(), "1E+2")
    assertEquals(new BigDecimal(bigIntOne, -15).toString(), "1E+15")
    assertEquals(new BigDecimal(bigIntOne, -2147483647).toString(), "1E+2147483647")
    assertEquals(new BigDecimal(bigIntOne, -2147483648).toString(), "1E+2147483648") // #4088

    val bigInt123 = BigInteger.valueOf(123)

    assertEquals(new BigDecimal(bigInt123, 0).toString(), "123")

    assertEquals(new BigDecimal(bigInt123, 2).toString(), "1.23")
    assertEquals(new BigDecimal(bigInt123, 6).toString(), "0.000123")
    assertEquals(new BigDecimal(bigInt123, 8).toString(), "0.00000123")
    assertEquals(new BigDecimal(bigInt123, 9).toString(), "1.23E-7")
    assertEquals(new BigDecimal(bigInt123, 2147483647).toString(), "1.23E-2147483645")

    assertEquals(new BigDecimal(bigInt123, -1).toString(), "1.23E+3")
    assertEquals(new BigDecimal(bigInt123, -2).toString(), "1.23E+4")
    assertEquals(new BigDecimal(bigInt123, -15).toString(), "1.23E+17")
    assertEquals(new BigDecimal(bigInt123, -2147483647).toString(), "1.23E+2147483649") // #4088
    assertEquals(new BigDecimal(bigInt123, -2147483648).toString(), "1.23E+2147483650") // #4088
  }

}
