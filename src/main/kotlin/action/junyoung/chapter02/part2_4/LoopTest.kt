package action.junyoung.chapter02.part2_4

fun main() {
    for (i in 1..100) {
        print("${fizzBuzz(i)} ")
    }
    println()
    for (i in 100 downTo 1 step 2) {
        print("${fizzBuzz(i)} ")
    }
    for (i in 1 until 100) {
        print("${fizzBuzz(i)} ")
    }
}

fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "FizzBuzz"
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    else -> "$i"
}