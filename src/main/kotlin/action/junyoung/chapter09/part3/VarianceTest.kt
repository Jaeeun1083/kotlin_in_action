package action.junyoung.chapter09.part3

fun <T> copyData1(
    source: MutableList<T>,
    destination: MutableList<T>,
) {
    for (item in source) {
        destination.add(item)
    }
}

fun <T : R, R> copyData2(
    source: MutableList<T>,
    destination: MutableList<R>,
) {
    for (item in source) {
        destination.add(item)
    }
}

fun <T> copyData3(
    source: MutableList<out T>,
    destination: MutableList<T>,
) {
    for (item in source) {
        destination.add(item)
    }
}

fun anyList(list: List<Any>) {
    println(list)
}

fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}


fun main() {
    val ints = mutableListOf(1, 2, 3)
    val anyItems = mutableListOf<Any>()

    val list: MutableList<out Number> = mutableListOf(1, 2, 3)
//    list.add(42)

    copyData2(ints, anyItems)
    println(anyItems)

    val anyComparator = Comparator<Any> { e1, e2 ->
        e1.hashCode() - e2.hashCode()
    }

    val strings = listOf("S1", "S2")
    strings.sortedWith(anyComparator)
}