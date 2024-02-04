package action.junyoung.chapter06.part3

import action.junyoung.chapter06.DataParser
import action.junyoung.chapter06.part1.Person

class PersonParser : DataParser<Person> {
    override fun parseData(
        input: String,
        output: MutableList<Person>,
        errors: MutableList<String?>) {
        // ..
    }
}