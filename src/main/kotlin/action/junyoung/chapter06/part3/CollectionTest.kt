package action.junyoung.chapter06.part3

import action.junyoung.chapter06.CollectionUtils
import java.io.BufferedReader
import java.io.StringReader
import java.lang.NumberFormatException

fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()

    for (line in reader.lineSequence()) {
        try {
            val number = line.toInt()
            result += (number)
        } catch (e: NumberFormatException) {
            result += (null)
        }
    }
    return result
}

fun addValidNumbers(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid numbers : ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
}

fun <T> copyElements(source: Collection<T>,
                     target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

fun main() {
    val reader = BufferedReader(StringReader("1\nabc\n42"))
    val numbers = readNumbers(reader)
    addValidNumbers(numbers)

    val source: Collection<Int> = arrayListOf(3, 5, 7)
    val target: MutableCollection<Int> = arrayListOf(1)

    copyElements(source, target)
    println(target)

    var list = listOf(1, 2, 3)
    println(list.javaClass.hashCode())
    list += 4
    println(list.javaClass.hashCode())

    var map = mapOf(1 to 1, 2 to 2)
    println(map.javaClass.hashCode())
    map += 3 to 3
    println(map.javaClass.hashCode())
    println(map)

    val strList = listOf("hello", "water")
    CollectionUtils.uppercaseAll(strList)
    println(strList)
}