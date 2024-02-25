package action.junyoung.chapter09.part1

val <T> List<T>.penultimate: T
    get() = this[size - 2]

fun main() {
    val letters = ('a'..'z').toList()
    println(letters.slice<Char>(0..2))

    val authors = listOf("Dmitry", "Svetlana")
    val readers = mutableListOf<String>(/* ... */)

    authors.filter { it.startsWith("D") }

    println(letters.penultimate)

}