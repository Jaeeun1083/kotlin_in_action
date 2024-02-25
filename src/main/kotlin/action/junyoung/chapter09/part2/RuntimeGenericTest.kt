package action.junyoung.chapter09.part2

import java.lang.IllegalArgumentException

fun typeCastTest(value: Collection<Int>) {
    if (value is List<Int>) {
        for (i in value) {
            println(i)
        }
    }
}

fun <T> checkType(value: T) {
    if (value is List<*>) {
        println("True")
    }
}

fun main() {
    val list1: List<String> = listOf("a", "b")
    val list2: List<Int> = listOf(1, 2, 3)
    typeCastTest(list2)
}