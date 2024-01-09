package action.junyoung.chapter04.part3

class Client(val name: String, val postalCode: Int) {
    fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Client(name, postalCode)
}

fun main() {
    val client1 = Client("Alice", 4122)
    val client2 = Client("Alice", 4122)
    println(client1)
    println(client2)
    println(client1 == client2)

    val processed = hashSetOf(Client("Alice", 4122))
    println(processed.contains(Client("Alice", 4122)))

    println(client1.copy(postalCode = 4000))
}