package action.junyoung.chapter08.part1

private enum class Delivery { STANDARD, EXPEDITED }

private class Order(val itemCount: Int)

private fun getShippingCostCalculator(delivery: Delivery) : (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}

private data class Person(val firstName: String, val lastName: String, val phoneNumber: String?)

private class ContactListFilters {
    var prefix = ""
    var onlyWithPhoneNumber = false

    fun getPredicate(): (Person) -> Boolean {
        val startsWithPrefix = {p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix
        }
        return { startsWithPrefix(it) && it.phoneNumber != null }
    }

}

fun main() {
    val calc = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping costs ${calc(Order(3))}")

    val contacts = listOf(Person("Dmitry", "Jemerov", "123-4567"),
        Person("Svetlana", "Isakova", null))
    val contactListFilters = ContactListFilters()
    with(contactListFilters) {
        prefix = "Dm"
        onlyWithPhoneNumber = true
    }
    println(contacts.filter(contactListFilters.getPredicate()))
}