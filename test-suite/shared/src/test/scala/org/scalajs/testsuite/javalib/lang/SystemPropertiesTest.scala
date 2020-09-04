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

import language.implicitConversions

import org.junit.{After, Test}
import org.junit.Assert._
import org.junit.Assume._

import org.scalajs.testsuite.utils.AssertThrows._
import org.scalajs.testsuite.utils.Platform._

class SystemPropertiesTest {

  private final val LineSeparatorPropName = "line.separator"
  private final val ExistingPropName = "org.scalajs.testsuite.existingprop"
  private final val TestPropName = "org.scalajs.testsuite.testprop"

  @After def resetSystemPropertiesAfterEachTest(): Unit = {
    System.setProperties(null)
  }

  /** Tests scenarios where only `getProperty`, `setProperty` and
   *  `clearProperty` are used.
   *
   *  In those scenarios, the inner `java.util.Properties` is not forced, and
   *  only the `dict` field of `SystemProperties` is manipulated.
   */
  @Test def testScenariosWithoutJavaUtilProperties(): Unit = {
    // Known property, always \n even on the JVM because our build ensures it
    assertEquals(System.getProperty(LineSeparatorPropName), """
""")
    assertEquals(System.getProperty(LineSeparatorPropName, "some default"), """
""")
    assertEquals(System.getProperty("this.property.does.not.exist"), null)
    assertEquals(System.getProperty("this.property.does.not.exist", "some default"),
        "some default")

    assertEquals(System.getProperty(TestPropName), null)
    assertEquals(System.setProperty(TestPropName, "test value"), null)
    assertEquals(System.getProperty(TestPropName), "test value")
    assertEquals(System.getProperty(TestPropName, "some default"), "test value")
    assertEquals(System.setProperty(TestPropName, "another value"), "test value")
    assertEquals(System.getProperty(TestPropName), "another value")
    assertEquals(System.clearProperty(TestPropName), "another value")
    assertEquals(System.getProperty(TestPropName), null)
    assertEquals(System.clearProperty(TestPropName), null)
  }

  /** Tests scenarios where we call `getProperties()`, forcing the inner
   *  `java.util.Properties` to be instantiated.
   *
   *  Also tests interactions between the existing values in `dict` at the time
   *  we force the `java.util.Properties`, and further interactions with
   *  `getProperty`, `setProperty` and `clearProperty`.
   */
  @Test def testScenariosWithGetProperties(): Unit = {
    System.setProperty(ExistingPropName, "existing value")

    val props = System.getProperties()
    assertNotNull(props)

    assertEquals(props.getProperty(LineSeparatorPropName), """
""")
    assertEquals(props.getProperty(LineSeparatorPropName, "some default"), """
""")
    assertEquals(props.getProperty("this.property.does.not.exist"), null)
    assertEquals(props.getProperty("this.property.does.not.exist", "some default"),
        "some default")

    // Existing props prior to calling getProperties() are visible
    assertEquals(props.getProperty(ExistingPropName), "existing value")

    // Manipulate props
    assertEquals(props.getProperty(TestPropName), null)
    assertEquals(props.setProperty(TestPropName, "test value"), null)
    assertEquals(props.getProperty(TestPropName), "test value")
    assertEquals(System.getProperty(TestPropName), "test value") // reflects on System
    assertEquals(props.getProperty(TestPropName, "some default"), "test value")
    assertEquals(props.setProperty(TestPropName, "another value"), "test value")
    assertEquals(props.getProperty(TestPropName), "another value")
    assertEquals(System.setProperty(TestPropName, "third value"), "another value")
    assertEquals(props.getProperty(TestPropName), "third value") // System reflects on props
    assertEquals(System.clearProperty(TestPropName), "third value")
    assertEquals(props.getProperty(TestPropName), null) // System.clear also reflects on props
    assertEquals(System.clearProperty(TestPropName), null)

    // Kill everything
    props.clear()
    assertEquals(props.getProperty(LineSeparatorPropName), null)
    assertEquals(System.getProperty(LineSeparatorPropName), null) // also kills it for System
  }

  /** Tests the effects of `setProperties()`, and its interactions with the
   *  other methods related to system properties.
   */
  @Test def testScenariosWithSetProperties(): Unit = {
    val props = new java.util.Properties()
    assertEquals(props.getProperty(LineSeparatorPropName), null)
    assertEquals(props.setProperty(TestPropName, "test value"), null)

    System.setProperties(props)
    assertSame(props, System.getProperties())
    assertEquals(System.getProperty(LineSeparatorPropName), null)
    assertEquals(System.getProperty(TestPropName), "test value")
  }
}
