package action.junyoung.chapter08.part2

//fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
//    return TransformingSequence(this, transform)
//}

//inline fun foo(inlined: () -> Unit, noinline notInline: () -> Unit) {
//
//}

private data class Person(val name: String, val age: Int)

private inline fun fooPrint(f1: () -> Unit) {
    for (i in 0 until 101) {
        f1.invoke()
    }
}

private inline fun inlineFun(f1: () -> Unit) {
    fooPrint(f1)
}

fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    // 람다를 사용해 거르기
//    val seq = people.asSequence().filter { it.age < 30 }.map(Person::name)
//    println(seq)

    // 컬렉션을 직접 거르기
//    val result = mutableListOf<Person>()
//    for (person in people) {
//        if(person.age < 30) result += person
//    }
//    println(result)

    inlineFun {
        println("foo")
    }
}