package action.junyoung.chapter08.part1

import java.lang.StringBuilder

//private val sum = { x: Int, y: Int -> x + y }
//private val sum: (Int, Int) -> Int = { x, y -> x + y }
var canReturnNull: ((Int, Int) -> Int?) = { x, y -> null }
var funOrNull: ((Int, Int) -> Int)? = null

fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}

fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in indices) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}

private fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element) ?: element.toString()
        result.append(str)
    }
    result.append(postfix)
    return result.toString()
}

fun foo(callback: (() -> Unit)?) {
    if(callback != null) callback()
}

fun main() {
    twoAndThree { a, b -> a + b }
    println("ab1c".filter { it in 'a'..'z' })

    val letters = listOf("Alpha", "Beta")
    println(letters.joinToString())
    println(letters.joinToString { it.lowercase() })
    println(letters.joinToString(separator = "! ", postfix = "! ") { it.uppercase() })

}