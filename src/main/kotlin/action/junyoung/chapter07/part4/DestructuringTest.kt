package action.junyoung.chapter07.part4

private class Point(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String): NameComponents {
    val result = fullName.split(".", limit = 2)
    return NameComponents(result[0], result[1])
}

fun main() {
    val p = Point(10, 20)
    val (x, y) = p
    println("x = $x, y = $y")

    val (name, ext) = splitFilename("example.kt")
    println(name)
    println(ext)
}

fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}