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

package org.scalajs.testsuite.utils

import scala.scalajs.js

object Platform {

  def scalaVersion: String = BuildInfo.scalaVersion

  /** Returns `true` if and only if the code is executing on a JVM.
   *  Note: Returns `false` when executing on any JS VM.
   */
  final val executingInJVM = false

  final val executingInJVMOnJDK6 = false

  final val executingInJVMOnJDK7OrLower = false

  final val executingInJVMOnJDK8OrLower = false

  def executingInNodeJS: Boolean = {
    js.typeOf(js.Dynamic.global.process) != "undefined" &&
    !js.isUndefined(js.Dynamic.global.process.release) &&
    (js.Dynamic.global.process.release.name: Any) == "node"
  }

  def jsSymbols: Boolean =
    assumeES2015 || js.typeOf(js.Dynamic.global.Symbol) != "undefined"

  def typedArrays: Boolean =
    assumeES2015 || js.typeOf(js.Dynamic.global.Int32Array) != "undefined"

  def sourceMaps: Boolean = BuildInfo.hasSourceMaps && executingInNodeJS

  def assumeES2015: Boolean = BuildInfo.es2015

  def isInFullOpt: Boolean = BuildInfo.isFullOpt
  def isInProductionMode: Boolean = BuildInfo.productionMode

  def hasCompliantAsInstanceOfs: Boolean = BuildInfo.compliantAsInstanceOfs

  def hasCompliantArrayIndexOutOfBounds: Boolean =
    BuildInfo.compliantArrayIndexOutOfBounds

  def hasCompliantModuleInit: Boolean = BuildInfo.compliantModuleInit
  def hasStrictFloats: Boolean = BuildInfo.strictFloats

  def isNoModule: Boolean = BuildInfo.isNoModule
  def isESModule: Boolean = BuildInfo.isESModule
  def isCommonJSModule: Boolean = BuildInfo.isCommonJSModule

  /** Runs the specified piece of code in the global context.
   *
   *  This only works on Node.js. It needs functionality from the `vm` module.
   *
   *  This method can be used to declare global let/const/classes. Any other
   *  attempt to do so (e.g., using a `require`d source file or a `js.eval`)
   *  would not expose the bindings in the global scope.
   */
  def nodejs_runInThisContext(code: String): Unit = {
    val vm = js.Dynamic.global.require("vm")
    val script = js.Dynamic.newInstance(vm.Script)(code)
    script.runInThisContext()
  }
}

private[utils] object BuildInfo {
  final val scalaVersion = ""
  final val hasSourceMaps = false
  final val isNoModule = false
  final val isESModule = false
  final val isCommonJSModule = false
  final val isFullOpt = false
  final val compliantAsInstanceOfs = false
  final val compliantArrayIndexOutOfBounds = false
  final val compliantModuleInit = false
  final val strictFloats = false
  final val productionMode = false
  final val es2015 = false
}
