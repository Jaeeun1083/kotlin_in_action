package action.junyoung.chapter06.part5

fun main() {
    val letters = Array<String>(26) { i -> ('a' + i).toString() }
    println(letters.joinToString(" "))

    val strings = listOf("a", "b", "c")
    println("%s%s%s".format(*strings.toTypedArray()))
}
