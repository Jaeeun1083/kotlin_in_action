package action.junyoung.chapter09.part1

private class Processor<T : Any> {
    fun process(value: T) {
        value.hashCode()
    }
}

fun main() {
//    val nullableStringProcessor = Processor<String?>()
//    nullableStringProcessor.process(null)
}