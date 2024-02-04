package action.junyoung.chapter07.part5

import action.junyoung.chapter05.part1.Person

class Email(val title: String, val content: String, val writer: Person)

fun findAllEmailByPerson(person: Person): List<Email> {
    return listOf()
}

fun loadEmails(person: Person): List<Email> {
    println("${person.name}의 이메일을 가져옴")
    return findAllEmailByPerson(person)
}

fun main() {

}