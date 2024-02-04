package action.junyoung.chapter06.part1

fun strLen(s: String) = s.length

fun strLenSage(s: String?): Int = if(s != null) s.length else 0

fun printAllCaps(s: String?) {
    val allCaps: String? = s?.uppercase()
    println(allCaps)
}

fun verifyUserInput(input: String?) {
    if(input.isNullOrBlank()) println("Please fill in the required fields")
}

@JvmName("printHashCode1")
fun <T> printHashCode(t: T) {
    println(t?.hashCode())
}

@JvmName("printHashCode2")
fun <T: Any> printHashCode(t: T) {
    println(t.hashCode())
}

fun printString(s1: String?, s2: String) {
    println(s1 ?: "null")
    println(s2 ?: "null")
}

fun main() {
    printAllCaps("abc")
    printAllCaps(null)

    verifyUserInput(null)

    printString(null, "123")
}