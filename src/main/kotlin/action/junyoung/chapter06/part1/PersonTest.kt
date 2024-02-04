package action.junyoung.chapter06.part1

import action.junyoung.chapter06.JPerson

fun yellAt(person: JPerson) {
    println(person.name.uppercase() + "!!!")
}

fun main() {
    yellAt(JPerson(null))
}