package action.junyoung.chapter10.part2

import java.lang.StringBuilder
import kotlin.reflect.full.memberProperties


private fun StringBuilder.serializeObject(obj: Any) {
   val kClass = obj.javaClass.kotlin
   val properties = kClass.memberProperties

   properties.joinToString(
      prefix = "{",
      postfix = "}"
   )

}

class Wrapper<out T : Any>(private val value: T) {
   fun getValue(): T = value
}

fun <T : Any, R : Any> Wrapper<T>.map(v: T,
                    block: (T, T) -> R): Wrapper<R> {
   return Wrapper(block(getValue(), v))
}
