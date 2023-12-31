package action.junyoung.chapter02.part2_4

import java.util.TreeMap

fun main() {
    val binaryReps = TreeMap<Char, String>()

    for (c in 'A'..'F') {
        val binary = Integer.toBinaryString(c.code)
        binaryReps[c] = binary
    }
    for ((letter, binary) in binaryReps) {
        println("$letter = $binary")
    }

    val list = arrayListOf("10", "11", "1001")
    for (element in list) {
        print("$element ")
    }
    for ((idx, element) in list.withIndex()) {
        println("$idx : $element")
    }
}