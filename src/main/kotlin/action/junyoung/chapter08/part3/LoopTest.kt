package action.junyoung.chapter08.part3

private data class Person(val name: String, val age: Int)

private fun lookForAliceLabel(people: List<Person>) {
    people.forEach label@{ person ->
        if(person.name == "Alice") return@label

    }
}

private fun lookForAliceAnonymousFunction(people: List<Person>) {
    people.forEach(fun(person) {
        if(person.name == "Alice") return
        println("${person.name} is not Alice")
    })
}

private fun lookForUnderAge30(people: List<Person>) {
    people.filter(fun (person): Boolean {
        return person.age < 30
    })

    people.filter(fun(person) = person.age < 30)
}

private fun lookForAlice(people: List<Person>) {
    println("start")
    people.forEach { person ->
        if (person.name == "Alice") {
            println("Found")
            return
        }
    }
    println("end")
//    for (person in people) {
//        if (person.name == "Alice") {
//            println("Found")
//            return
//        }
//    }
}

fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))
    lookForAlice(people)
}