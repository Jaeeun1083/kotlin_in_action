package action.junyoung.chapter09.part1

import java.lang.Appendable
import java.lang.StringBuilder

fun <T> ensureTrailingPeriod(seq: T)
        where T : CharSequence, T : Appendable {
            if(!seq.endsWith('.')) seq.append('.')
}

fun <T : Comparable<T>> max(first: T, second: T): T {
    return if(first > second) first else second
}

private class TestClass(val num: Int)
fun main() {

    val sb = StringBuilder("Hello World")
    ensureTrailingPeriod(sb)
    println(sb)


    val numbers = (1..100).toList()
    numbers.sum()

//    max(TestClass(10), TestClass(20))
}