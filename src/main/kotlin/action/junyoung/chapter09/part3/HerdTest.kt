package action.junyoung.chapter09.part3

open class Animal {
    fun feed() = println("Yummy")
}

class Herd<out T : Animal> {
    val size: Int get() = 10
    operator fun get(i: Int): T {
        TODO()
    }
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) {
        animals[i].feed()
    }
}

class Cat : Animal() {
    fun cleanLitter() {}
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
         feedAll(cats)
    }
}

fun enumerateCats(f: (Cat) -> Number) {}
fun Animal.getIndex(): Int = 10

fun main() {
    enumerateCats(Animal::getIndex)
}

