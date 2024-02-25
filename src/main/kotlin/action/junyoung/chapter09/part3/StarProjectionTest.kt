package action.junyoung.chapter09.part3

import java.lang.IllegalArgumentException
import java.util.Collections
import java.util.Random
import kotlin.reflect.KClass

fun printFirst(list: List<*>) {
    if (list.isNotEmpty()) {
        println(list.first())
    }
}

interface FieldValidator<in T> {
    fun validate(input: T): Boolean
}

private object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

private object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}

object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()

    fun <T : Any> registerValidator(
        kClass: KClass<T>, fieldValidator: FieldValidator<T>
    ) {
        validators[kClass] = fieldValidator
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(kClass: KClass<T>): FieldValidator<T> =
        validators[kClass] as? FieldValidator<T>
            ?: throw IllegalArgumentException(
                "No validator for ${kClass.simpleName}"
            )
}

fun validatorTest() {
    val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()
    validators[String::class] = DefaultStringValidator
    validators[Int::class] = DefaultIntValidator

//    validators[String::class]!!.validate("Hello")

    val stringValidator = validators[String::class] as FieldValidator<String>
    stringValidator.validate("Hello")

    val stringFaultValidator = validators[Int::class] as FieldValidator<String>
    println(stringFaultValidator.validate(""))

//    println(Validators[String::class].validate(42))
}

fun main() {
    val list: MutableList<Any?> = mutableListOf('a', 1, "QWE")
    val chars = mutableListOf('a', 'b', 'c')
    val unknownElements: MutableList<*> = if (Random().nextBoolean()) list else chars

    println(unknownElements)

    validatorTest()

//    unknownElements.add(42)
}

