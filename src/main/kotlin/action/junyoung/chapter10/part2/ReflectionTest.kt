package action.junyoung.chapter10.part2

import kotlin.reflect.full.memberProperties

open class Animal(val height: Int)

private class Person(val name: String, val age: Int, height: Int) : Animal(height)

fun main() {
//   kClassTest()
//   kFunctionTest()
   kPropertyTest()
}

fun foo(x: Int) = println(x)

fun kFunctionTest() {
   val kFunction = ::foo
   kFunction.invoke(10)
   kFunction(42)
}

fun kPropertyTest() {
   val p = Person("Alice", 29, 198)
   val memberProperty = Person::age
   println(memberProperty.get(p))
}

fun kClassTest() {
   val p = Person("Alice", 29, 197)
   val kClass = p.javaClass.kotlin

   println(kClass.simpleName)

   kClass.memberProperties.forEach { print("${it.name} ") }
}