package action.junyoung.chapter09.part2

import java.util.ServiceLoader

//fun <T> isA(value: Any) = value is T

//inline fun <reified T> isA(value: Any) = value is T

fun main() {
    val items = listOf(1, 2, 3)
    println(items.filterIsInstance<String>())
//    ServiceLoader
}