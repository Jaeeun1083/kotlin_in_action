package action.junyoung.chapter05.part1

import action.junyoung.chapter07.part5.Email
import action.junyoung.chapter07.part5.loadEmails
import java.lang.StringBuilder

data class Person(val name: String, var age: Int) {
    val emails by lazy { loadEmails(this) }
}

private fun findTheOldest(people: List<Person>) {
    var maxAge = 0
    var theOldest: Person? = null

    for (person in people) {
        if(person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}

fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    block: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for ((idx, element) in this.withIndex()) {
        if (idx > 0) result.append(separator)

        if(block != null) {
            result.append(block(element))
        } else {
            result.append(element)
        }
    }
    result.append(postfix)
    return result.toString()
}

fun salute() = println("Salute!")
val action = { p: Person, msg: String -> sendEmail(p, msg) }

fun sendEmail(p: Person, msg: String) {
    println("""
        -----------
        To. ${p.name}
        $msg
        -----------
    """.trimIndent())
}

fun main() {
    val people = listOf(Person("Alice", 29), Person("Sponge", 52), Person("Bob", 97))
    println(people.maxBy { it.age })

    val names = people.joinToString(" ") { it.name }
    println(names)

    val sum= {x: Int, y: Int ->
        println("Computing the sum of $x and $y")
        x + y
    }
    println(sum(1, 2))

    val ageProperty = Person::age

    run (::salute)

    val personConstructor = ::Person

    action(personConstructor("전청조", 28), "나 전청조인데 개추")

}


