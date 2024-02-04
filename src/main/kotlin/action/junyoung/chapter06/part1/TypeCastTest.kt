package action.junyoung.chapter06.part1

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JList

class Animal(val firstName: String, val lastName: String) {
    override fun equals(o: Any?): Boolean {
        val otherAnimal = o as? Animal ?: return false

        return otherAnimal.firstName == firstName &&
                otherAnimal.lastName == lastName
    }

    override fun hashCode(): Int =
        firstName.hashCode() * 37 + lastName.hashCode()
}

class CopyRowAction(val list: JList<String>) : AbstractAction() {
    override fun isEnabled(): Boolean = list.selectedValue != null
    override fun actionPerformed(e: ActionEvent) {
        val value = list.selectedValue!!
        // doSomething()
    }
}

fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!
    println(sNotNull.length)
}

fun main() {
    ignoreNulls(null)
}