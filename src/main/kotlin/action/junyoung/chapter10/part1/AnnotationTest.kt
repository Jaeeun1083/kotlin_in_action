@file:JvmName("FooFunctions")

package action.junyoung.chapter10.part1

import action.junyoung.chapter10.JsonName

annotation class JsonNaming(val name: String, val value: String)

fun main() {
   @JsonName("Hello", name = "hello")
   @JsonNaming("hello", "hello")
   val t = 10
}

fun foo() {
   println("foo")
}

object Foos{
   @JvmStatic
   fun fooo() {
      println("fooo")
   }
}