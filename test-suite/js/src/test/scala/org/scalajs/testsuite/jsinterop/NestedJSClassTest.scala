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
import scala.scalajs.js.annotation._

import org.junit.Assert._
import org.junit.Test

class NestedJSClassTest {
  import NestedJSClassTest._

  @Test def innerJSClass_basics(): Unit = {
    val container1 = new ScalaClassContainer("hello")
    val innerJSClass = container1.getInnerJSClass
    assertSame(innerJSClass, container1.getInnerJSClass)
    assertSame(innerJSClass, js.constructorOf[container1.InnerJSClass])
    assertEquals(js.typeOf(innerJSClass), "function")

    val inner1 = new container1.InnerJSClass("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertTrue(inner1.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner1, innerJSClass))

    val inner2 = js.Dynamic.newInstance(innerJSClass)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertTrue(inner2.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner2, innerJSClass))

    val container2 = new ScalaClassContainer("hi")
    val innerJSClass2 = container2.getInnerJSClass
    assertNotSame(innerJSClass, innerJSClass2)

    val inner3 = new container2.InnerJSClass("world3")
    assertEquals(inner3.zzz, "hiworld3")
    assertEquals(inner3.foo("foo"), "hiworld3foo")
    assertTrue(inner3.isInstanceOf[container2.InnerJSClass])
    assertTrue(js.special.instanceof(inner3, container2.getInnerJSClass))

    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
    assertFalse(js.special.instanceof(inner3, innerJSClass))
  }

  @Test def localJSClass_basics(): Unit = {
    val container1 = new ScalaClassContainer("hello")
    val localJSClass1 = container1.makeLocalJSClass("wide1")
    assertEquals(js.typeOf(localJSClass1), "function")

    val inner1 = js.Dynamic.newInstance(localJSClass1)("world1")
    assertEquals(inner1.zzz, "hellowide1world1")
    assertEquals(inner1.foo("foo"), "hellowide1world1foo")
    assertTrue(js.special.instanceof(inner1, localJSClass1))
    assertFalse(inner1.isInstanceOf[container1.InnerJSClass])

    val inner2 = js.Dynamic.newInstance(localJSClass1)("world2")
    assertEquals(inner2.zzz, "hellowide1world2")
    assertEquals(inner2.foo("foo"), "hellowide1world2foo")

    val localJSClass2 = container1.makeLocalJSClass("wide2")
    assertNotSame(localJSClass1, localJSClass2)

    val inner3 = js.Dynamic.newInstance(localJSClass2)("world3")
    assertEquals(inner3.zzz, "hellowide2world3")
    assertEquals(inner3.foo("foo"), "hellowide2world3foo")
    assertTrue(js.special.instanceof(inner3, localJSClass2))
    assertFalse(js.special.instanceof(inner3, localJSClass1))
    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
  }

  @Test def innerJSClass_basicsInsideTrait(): Unit = {
    val container1 = new ScalaTraitContainerSubclass("hello")
    val innerJSClass = container1.getInnerJSClass
    assertSame(innerJSClass, container1.getInnerJSClass)
    assertSame(innerJSClass, js.constructorOf[container1.InnerJSClass])
    assertEquals(js.typeOf(innerJSClass), "function")

    val inner1 = new container1.InnerJSClass("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertTrue(inner1.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner1, innerJSClass))

    val inner2 = js.Dynamic.newInstance(innerJSClass)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertTrue(inner2.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner2, innerJSClass))

    val container2 = new ScalaTraitContainerSubclass("hi")
    val innerJSClass2 = container2.getInnerJSClass
    assertNotSame(innerJSClass, innerJSClass2)

    val inner3 = new container2.InnerJSClass("world3")
    assertEquals(inner3.zzz, "hiworld3")
    assertEquals(inner3.foo("foo"), "hiworld3foo")
    assertTrue(inner3.isInstanceOf[container2.InnerJSClass])
    assertTrue(js.special.instanceof(inner3, container2.getInnerJSClass))

    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
    assertFalse(js.special.instanceof(inner3, innerJSClass))
  }

  @Test def localJSClass_basicsInsideTrait(): Unit = {
    val container1 = new ScalaTraitContainerSubclass("hello")
    val localJSClass1 = container1.makeLocalJSClass("wide1")
    assertEquals(js.typeOf(localJSClass1), "function")

    val inner1 = js.Dynamic.newInstance(localJSClass1)("world1")
    assertEquals(inner1.zzz, "hellowide1world1")
    assertEquals(inner1.foo("foo"), "hellowide1world1foo")
    assertTrue(js.special.instanceof(inner1, localJSClass1))
    assertFalse(inner1.isInstanceOf[container1.InnerJSClass])

    val inner2 = js.Dynamic.newInstance(localJSClass1)("world2")
    assertEquals(inner2.zzz, "hellowide1world2")
    assertEquals(inner2.foo("foo"), "hellowide1world2foo")

    val localJSClass2 = container1.makeLocalJSClass("wide2")
    assertNotSame(localJSClass1, localJSClass2)

    val inner3 = js.Dynamic.newInstance(localJSClass2)("world3")
    assertEquals(inner3.zzz, "hellowide2world3")
    assertEquals(inner3.foo("foo"), "hellowide2world3foo")
    assertTrue(js.special.instanceof(inner3, localJSClass2))
    assertFalse(js.special.instanceof(inner3, localJSClass1))
    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
  }

  @Test def innerJSObject_basics(): Unit = {
    val container1 = new ScalaClassContainerWithObject("hello")
    val inner1 = container1.InnerJSObject
    assertSame(inner1, container1.InnerJSObject)
    assertEquals(js.typeOf(inner1), "object")

    assertEquals(inner1.zzz, "hellozzz")
    assertEquals(inner1.foo("foo"), "hellozzzfoo")
    assertTrue(inner1.isInstanceOf[container1.InnerJSObject.type])

    val container2 = new ScalaClassContainerWithObject("hi")
    val inner2 = container2.InnerJSObject
    assertNotSame(inner1, inner2)
    assertNotSame(inner1.asInstanceOf[js.Dynamic].constructor,
        inner2.asInstanceOf[js.Dynamic].constructor)
    assertEquals(inner2.zzz, "hizzz")
    assertEquals(inner2.foo("foo"), "hizzzfoo")

    assertFalse(inner2.isInstanceOf[container1.InnerJSObject.type])
  }

  @Test def localJSObject_basics(): Unit = {
    val container1 = new ScalaClassContainerWithObject("hello")
    val inner1 = container1.makeLocalJSObject("world1")

    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")

    val inner2 = container1.makeLocalJSObject("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")

    assertNotSame(inner1, inner2)
    assertNotSame(inner1.asInstanceOf[js.Dynamic].constructor,
        inner2.asInstanceOf[js.Dynamic].constructor)
  }

  @Test def innerJSClassExtendsInnerJSClass(): Unit = {
    val parentsContainer = new ScalaClassContainer("hello")
    val container1 =
      new ScalaClassContainerWithSubclasses("hi", parentsContainer)
    val innerJSClass = parentsContainer.getInnerJSClass
    val innerJSSubclass = container1.getInnerJSSubclass

    val inner1 = new container1.InnerJSSubclass("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertEquals(inner1.foobar(), "hiworld1helloworld1")

    assertTrue(inner1.isInstanceOf[container1.InnerJSSubclass])
    assertTrue(js.special.instanceof(inner1, innerJSSubclass))
    assertTrue(inner1.isInstanceOf[parentsContainer.InnerJSClass])
    assertTrue(js.special.instanceof(inner1, innerJSClass))

    val container2 =
      new ScalaClassContainerWithSubclasses("salut", parentsContainer)
    val innerJSSubclass2 = container2.getInnerJSSubclass

    val inner2 = js.Dynamic.newInstance(innerJSSubclass2)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertEquals(inner2.foobar(), "salutworld2helloworld2")

    assertTrue((inner2: Any).isInstanceOf[container2.InnerJSSubclass])
    assertFalse((inner2: Any).isInstanceOf[container1.InnerJSSubclass])
    assertTrue(js.special.instanceof(inner2, innerJSClass))

    val otherParentsContainer = new ScalaClassContainer("other")
    assertFalse(inner1.isInstanceOf[otherParentsContainer.InnerJSClass])
    assertFalse(inner2.isInstanceOf[otherParentsContainer.InnerJSClass])
  }

  @Test def localJSClassExtendsInnerJSClass(): Unit = {
    val parentsContainer = new ScalaClassContainer("hello")
    val container1 =
      new ScalaClassContainerWithSubclasses("hi", parentsContainer)

    val localJSClass1 = container1.makeLocalJSSubclass("wide1")
    assertEquals(js.typeOf(localJSClass1), "function")

    val inner1 = js.Dynamic.newInstance(localJSClass1)("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertEquals(inner1.foobar(), "hiwide1helloworld1")
    assertTrue(js.special.instanceof(inner1, localJSClass1))
    assertTrue(inner1.isInstanceOf[parentsContainer.InnerJSClass])
    assertFalse(inner1.isInstanceOf[container1.InnerJSSubclass])

    val inner2 = js.Dynamic.newInstance(localJSClass1)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertEquals(inner2.foobar(), "hiwide1helloworld2")

    val localJSClass2 = container1.makeLocalJSSubclass("wide2")
    assertNotSame(localJSClass1, localJSClass2)

    val inner3 = js.Dynamic.newInstance(localJSClass2)("world3")
    assertEquals(inner3.zzz, "helloworld3")
    assertEquals(inner3.foo("foo"), "helloworld3foo")
    assertEquals(inner3.foobar(), "hiwide2helloworld3")
    assertTrue(js.special.instanceof(inner3, localJSClass2))
    assertTrue(inner3.isInstanceOf[parentsContainer.InnerJSClass])
    assertFalse(js.special.instanceof(inner3, localJSClass1))
    assertFalse(inner3.isInstanceOf[container1.InnerJSSubclass])

    val otherParentsContainer = new ScalaClassContainer("other")
    assertFalse(inner1.isInstanceOf[otherParentsContainer.InnerJSClass])
    assertFalse(inner2.isInstanceOf[otherParentsContainer.InnerJSClass])
    assertFalse(inner3.isInstanceOf[otherParentsContainer.InnerJSClass])
  }

  @Test def innerJSObjectExtendsInnerJSClass(): Unit = {
    val parentsContainer = new ScalaClassContainer("hello")
    val container1 = new ScalaClassContainerWithSubObjects("hi",
        parentsContainer)
    val inner1 = container1.InnerJSObject
    assertSame(inner1, container1.InnerJSObject)
    assertEquals(js.typeOf(inner1), "object")

    assertEquals(inner1.zzz, "hellohi")
    assertEquals(inner1.foo("foo"), "hellohifoo")
    assertEquals(inner1.foobar(), "hihellohi")
    assertTrue(inner1.isInstanceOf[container1.InnerJSObject.type])
    assertTrue(inner1.isInstanceOf[parentsContainer.InnerJSClass])

    val container2 = new ScalaClassContainerWithSubObjects("hi2",
        parentsContainer)
    val inner2 = container2.InnerJSObject
    assertNotSame(inner1, inner2)
    assertNotSame(inner1.asInstanceOf[js.Dynamic].constructor,
        inner2.asInstanceOf[js.Dynamic].constructor)
    assertEquals(inner2.zzz, "hellohi2")
    assertEquals(inner2.foo("foo"), "hellohi2foo")
    assertEquals(inner2.foobar(), "hi2hellohi2")

    assertFalse(inner2.isInstanceOf[container1.InnerJSObject.type])

    val otherParentsContainer = new ScalaClassContainer("other")
    assertFalse(inner1.isInstanceOf[otherParentsContainer.InnerJSClass])
    assertFalse(inner2.isInstanceOf[otherParentsContainer.InnerJSClass])
  }

  @Test def localJSObjectExtendsInnerJSClass(): Unit = {
    val parentsContainer = new ScalaClassContainer("hello")
    val container1 = new ScalaClassContainerWithSubObjects("hi",
        parentsContainer)

    val inner1 = container1.makeLocalJSObject("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertEquals(inner1.foobar(), "hiworld1helloworld1")
    assertTrue(inner1.isInstanceOf[parentsContainer.InnerJSClass])

    val inner2 = container1.makeLocalJSObject("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertEquals(inner2.foobar(), "hiworld2helloworld2")
    assertTrue(inner2.isInstanceOf[parentsContainer.InnerJSClass])

    assertNotSame(inner1, inner2)
    assertNotSame(inner1.asInstanceOf[js.Dynamic].constructor,
        inner2.asInstanceOf[js.Dynamic].constructor)

    val otherParentsContainer = new ScalaClassContainer("other")
    assertFalse(inner1.isInstanceOf[otherParentsContainer.InnerJSClass])
    assertFalse(inner2.isInstanceOf[otherParentsContainer.InnerJSClass])
  }

  @Test def convolutedGenericTypeParametersInSuperClass(): Unit = {
    val parentsContainer = new GenericJSSuperClassContainer
    val container1 = new ScalaClassContainerWithTypeParameters[Int](5,
        parentsContainer)

    type MyB = List[List[Int]]

    val innerJSClass = js.constructorOf[container1.GenericJSInnerClass[MyB]]
    assertSame(innerJSClass,
        js.constructorOf[container1.GenericJSInnerClass[MyB]])
    assertEquals(js.typeOf(innerJSClass), "function")
    val inner: Any = new container1.GenericJSInnerClass[MyB](Nil)
    assertTrue(inner.isInstanceOf[parentsContainer.GenericJSSuperClass[_, _]])

    val localJSClass = container1.makeGenericJSLocalClass()
    assertNotSame(localJSClass, container1.makeGenericJSLocalClass())
    assertEquals(js.typeOf(localJSClass), "function")
    val local: Any = js.Dynamic.newInstance(localJSClass)(Nil.asInstanceOf[js.Any])
    assertTrue(local.isInstanceOf[parentsContainer.GenericJSSuperClass[_, _]])

    val innerJSObject = container1.GenericJSInnerObject
    assertSame(innerJSObject, container1.GenericJSInnerObject)
    assertTrue(innerJSObject.isInstanceOf[parentsContainer.GenericJSSuperClass[_, _]])

    val localJSObject = container1.makeGenericJSInnerObject(Nil)
    assertNotSame(localJSObject, container1.makeGenericJSInnerObject(Nil))
    assertTrue(localJSObject.isInstanceOf[parentsContainer.GenericJSSuperClass[_, _]])
  }

  @Test def innerJSClass_basicsInsideJSClass(): Unit = {
    val container1 = new JSClassContainer("hello")
    val innerJSClass = container1.getInnerJSClass
    assertSame(innerJSClass, container1.getInnerJSClass)
    assertSame(innerJSClass, js.constructorOf[container1.InnerJSClass])
    assertEquals(js.typeOf(innerJSClass), "function")

    val inner1 = new container1.InnerJSClass("world1")
    assertEquals(inner1.zzz, "helloworld1")
    assertEquals(inner1.foo("foo"), "helloworld1foo")
    assertTrue(inner1.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner1, innerJSClass))

    val inner2 = js.Dynamic.newInstance(innerJSClass)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertTrue(inner2.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner2, innerJSClass))

    assertTrue(js.isUndefined(container1.asInstanceOf[js.Dynamic].InnerScalaClass))
    val scalaInner = new container1.InnerScalaClass(543)
    assertEquals(543, scalaInner.zzz)

    val container2 = new JSClassContainer("hi")
    val innerJSClass2 = container2.getInnerJSClass
    assertNotSame(innerJSClass, innerJSClass2)

    val inner3 = new container2.InnerJSClass("world3")
    assertEquals(inner3.zzz, "hiworld3")
    assertEquals(inner3.foo("foo"), "hiworld3foo")
    assertTrue(inner3.isInstanceOf[container2.InnerJSClass])
    assertTrue(js.special.instanceof(inner3, container2.getInnerJSClass))

    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
    assertFalse(js.special.instanceof(inner3, innerJSClass))
  }

  @Test def innerJSClass_accessibleFromJS_ifInsideJSClass(): Unit = {
    val container1 = new JSClassContainer("hello")
    val innerJSClass = container1.asInstanceOf[js.Dynamic].getInnerJSClass
    assertSame(innerJSClass, container1.getInnerJSClass)
    assertSame(innerJSClass, js.constructorOf[container1.InnerJSClass])
    assertEquals(js.typeOf(innerJSClass), "function")

    val inner2 = js.Dynamic.newInstance(innerJSClass)("world2")
    assertEquals(inner2.zzz, "helloworld2")
    assertEquals(inner2.foo("foo"), "helloworld2foo")
    assertTrue(inner2.isInstanceOf[container1.InnerJSClass])
    assertTrue(js.special.instanceof(inner2, innerJSClass))

    val container2 = new JSClassContainer("hi")
    val innerJSClass2 = container2.asInstanceOf[js.Dynamic].getInnerJSClass
    assertNotSame(innerJSClass, innerJSClass2)

    val inner3 = js.Dynamic.newInstance(innerJSClass2)("world3")
    assertEquals(inner3.zzz, "hiworld3")
    assertEquals(inner3.foo("foo"), "hiworld3foo")
    assertTrue(inner3.isInstanceOf[container2.InnerJSClass])
    assertTrue(js.special.instanceof(inner3, container2.getInnerJSClass))

    assertFalse(inner3.isInstanceOf[container1.InnerJSClass])
    assertFalse(js.special.instanceof(inner3, innerJSClass))
  }

  @Test def innerJSClassObject_accessibleFromJS_ifInsideTopJSObject_issue4086(): Unit = {
    val container = NestedJSClassTest_TopLevelJSObject_Issue4086.asInstanceOf[js.Dynamic]

    assertEquals(js.typeOf(container.InnerScalaObject), "object")
    assertEquals(container.InnerScalaObject.toString(), "the InnerScalaObject of issue 4086")
    assertSame(NestedJSClassTest_TopLevelJSObject_Issue4086.InnerScalaObject, container.InnerScalaObject)

    assertEquals(js.typeOf(container.InnerJSObject), "object")
    assertEquals(container.InnerJSObject.toString(), "the InnerJSObject of issue 4086")
    assertSame(NestedJSClassTest_TopLevelJSObject_Issue4086.InnerJSObject, container.InnerJSObject)

    assertTrue(js.isUndefined(container.InnerScalaClass))
    val innerScalaObj = new NestedJSClassTest_TopLevelJSObject_Issue4086.InnerScalaClass(543)
    assertEquals(543, innerScalaObj.x)

    val cls = container.InnerJSClass
    assertEquals(js.typeOf(cls), "function")
    assertSame(js.constructorOf[NestedJSClassTest_TopLevelJSObject_Issue4086.InnerJSClass], cls)
    val obj = js.Dynamic.newInstance(cls)(5)
    assertEquals(obj.x, 5)
    assertEquals(obj.toString(), "InnerJSClass(5) of issue 4086")
  }

  @Test def doublyNestedInnerObject_issue4114(): Unit = {
    val outer1 = new DoublyNestedInnerObject_Issue4114().asInstanceOf[js.Dynamic]
    val outer2 = new DoublyNestedInnerObject_Issue4114().asInstanceOf[js.Dynamic]

    outer2.middle.inner.x = 10

    assertEquals(js.typeOf(outer1.middle), "object")
    assertEquals(outer1.middle.inner.x, 1)
    assertEquals(outer2.middle.inner.x, 10)
  }

  @Test def triplyNestedObject_issue4114(): Unit = {
    val obj = TriplyNestedObject_Issue4114.asInstanceOf[js.Dynamic]

    assertEquals(js.typeOf(obj.middle), "object")
    assertEquals(js.typeOf(obj.middle.inner), "object")
    assertEquals(obj.middle.inner.x, 1)

    obj.middle.inner.x = 10

    assertEquals(obj.middle.inner.x, 10)
  }

  @Test def triplyNestedClassSuperDispatch_issue4114(): Unit = {
    val x = new TriplyNestedClass_Issue4114().asInstanceOf[js.Dynamic]
    assertEquals(x.foo(3), 3)
  }

  @Test def localJSClassCapturesCharThatMustBeBoxed(): Unit = {
    @inline def makeChar(): Any = 'A'

    val char = makeChar()

    class LocalJSClassWithCharCapture extends js.Object {
      def getCharAny(): Any = char
      def getCharAsChar(): Char = char.asInstanceOf[Char]
    }

    val obj = new LocalJSClassWithCharCapture
    val charAny = obj.getCharAny()
    assertTrue(charAny.toString(), charAny.isInstanceOf[Char])
    assertEquals(charAny, 'A')
    assertEquals(charAny.toString(), "A")
    assertEquals('A', obj.getCharAsChar())
  }

  @Test def overloadedConstructorsInLocalJSClass(): Unit = {
    val a = 5
    val b = 10

    class LocalJSClassWithOverloadedConstructors(val x: Int) extends js.Object {
      val aa = a

      def this(x: Int, y: Int) = {
        this(x + y + b)
      }
    }

    val obj1 = new LocalJSClassWithOverloadedConstructors(50)
    assertEquals(50, obj1.x)
    assertEquals(5, obj1.aa)

    val obj2 = new LocalJSClassWithOverloadedConstructors(34, 78)
    assertEquals(34 + 78 + 10, obj2.x)
    assertEquals(5, obj2.aa)
  }

  @Test def selfReferencingLocalJSClass(): Unit = {
    class JSCons[+A](val head: A, val tail: JSCons[A]) extends js.Object {
      def ::[B >: A](x: B): JSCons[B] =
        new JSCons[B](x, this)

      def self: js.Dynamic = js.constructorOf[JSCons[_]]
    }

    val threeAndNil = new JSCons(3, null)
    val list = "head" :: 2 :: threeAndNil

    assertEquals(list.self, js.constructorOf[JSCons[_]])
    assertEquals(list.head, "head")
    assertEquals(list.tail.head, 2)
    assertEquals(list.tail.tail.head, 3)
    assertNull(list.tail.tail.tail)
  }

}

object NestedJSClassTest {
  trait TestInterface extends js.Object {
    val zzz: String

    def foo(a: String): String
  }

  class ScalaClassContainer(xxx: String) {
    class InnerJSClass(yyy: String) extends js.Object with TestInterface {
      val zzz: String = xxx + yyy

      def foo(a: String): String = xxx + yyy + a
    }

    def getInnerJSClass: js.Dynamic =
      js.constructorOf[InnerJSClass]

    def makeLocalJSClass(yyy: String): js.Dynamic = {
      class LocalJSClass(abc: String) extends js.Object with TestInterface {
        val zzz: String = xxx + yyy + abc

        def foo(a: String): String = xxx + yyy + abc + a
      }

      js.constructorOf[LocalJSClass]
    }
  }

  trait ScalaTraitContainer {
    def xxx: String

    class InnerJSClass(yyy: String) extends js.Object with TestInterface {
      val zzz: String = xxx + yyy

      def foo(a: String): String = xxx + yyy + a
    }

    def getInnerJSClass: js.Dynamic =
      js.constructorOf[InnerJSClass]

    def makeLocalJSClass(yyy: String): js.Dynamic = {
      class LocalJSClass(abc: String) extends js.Object with TestInterface {
        val zzz: String = xxx + yyy + abc

        def foo(a: String): String = xxx + yyy + abc + a
      }

      js.constructorOf[LocalJSClass]
    }
  }

  class ScalaTraitContainerSubclass(val xxx: String) extends ScalaTraitContainer

  class ScalaClassContainerWithObject(xxx: String) {
    object InnerJSObject extends js.Object with TestInterface {
      val zzz: String = xxx + "zzz"

      def foo(a: String): String = xxx + "zzz" + a
    }

    def makeLocalJSObject(yyy: String): TestInterface = {
      object LocalJSObject extends js.Object with TestInterface {
        val zzz: String = xxx + yyy

        def foo(a: String): String = xxx + yyy + a
      }

      LocalJSObject
    }
  }

  class ScalaClassContainerWithSubclasses(val abc: String,
      val parents: ScalaClassContainer) {

    class InnerJSSubclass(yyy: String) extends parents.InnerJSClass(yyy) {
      def foobar(): String = abc + yyy + zzz
    }

    def getInnerJSSubclass: js.Dynamic =
      js.constructorOf[InnerJSSubclass]

    def makeLocalJSSubclass(yyy: String): js.Dynamic = {
      class LocalJSSubclass(xyz: String) extends parents.InnerJSClass(xyz) {
        def foobar(): String = abc + yyy + zzz
      }
      js.constructorOf[LocalJSSubclass]
    }
  }

  class ScalaClassContainerWithSubObjects(val abc: String,
      val parents: ScalaClassContainer) {

    object InnerJSObject extends parents.InnerJSClass(abc) {
      def foobar(): String = abc + zzz
    }

    def makeLocalJSObject(yyy: String): js.Dynamic = {
      object LocalJSObject extends parents.InnerJSClass(yyy) {
        def foobar(): String = abc + yyy + zzz
      }
      LocalJSObject.asInstanceOf[js.Dynamic]
    }
  }

  class GenericJSSuperClassContainer {
    class GenericJSSuperClass[A, B <: List[Seq[A]]](val a: A, val b: B)
        extends js.Object
  }

  class ScalaClassContainerWithTypeParameters[A](val a: A,
      val parents: GenericJSSuperClassContainer) {

    class GenericJSInnerClass[B <: List[Seq[A]]](b: B)
        extends parents.GenericJSSuperClass[A, B](a, b)

    def makeGenericJSLocalClass(): js.Dynamic = {
      class GenericJSLocalClass[B <: List[Seq[A]]](b: B)
          extends parents.GenericJSSuperClass[A, B](a, b)
      js.constructorOf[GenericJSLocalClass[_]]
    }

    object GenericJSInnerObject
        extends parents.GenericJSSuperClass[A, List[List[A]]](a, Nil)

    def makeGenericJSInnerObject[B <: List[Seq[A]]](b: B): js.Dynamic = {
      object GenericJSInnerObject
          extends parents.GenericJSSuperClass[A, B](a, b)

      GenericJSInnerObject.asInstanceOf[js.Dynamic]
    }
  }

  class JSClassContainer(xxx: String) extends js.Object {
    class InnerJSClass(yyy: String) extends js.Object with TestInterface {
      val zzz: String = xxx + yyy

      def foo(a: String): String = xxx + yyy + a
    }

    def getInnerJSClass: js.Dynamic =
      js.constructorOf[InnerJSClass]

    // Not visible from JS, but can be instantiated from Scala.js code
    class InnerScalaClass(val zzz: Int)
  }

  class DoublyNestedInnerObject_Issue4114 extends js.Object {
    object middle extends js.Object {
      object inner extends js.Object {
        var x = 1
      }
    }
  }

  object TriplyNestedObject_Issue4114 extends js.Object {
    object middle extends js.Object {
      object inner extends js.Object {
        var x = 1
      }

      class InnerClass extends js.Object {
        def foo(x: Int): Int = x
      }
    }
  }

  class TriplyNestedClass_Issue4114 extends TriplyNestedObject_Issue4114.middle.InnerClass {
    def foo(x: String): String = x
  }
}

object NestedJSClassTest_TopLevelJSObject_Issue4086 extends js.Object {
  object InnerScalaObject {
    override def toString(): String = "the InnerScalaObject of issue 4086"
  }

  object InnerJSObject extends js.Object {
    override def toString(): String = "the InnerJSObject of issue 4086"
  }

  // Not visible from JS, but can be instantiated from Scala.js code
  class InnerScalaClass(val x: Int)

  class InnerJSClass(val x: Int) extends js.Object {
    override def toString(): String = s"InnerJSClass($x) of issue 4086"
  }
}
