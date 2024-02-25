package action.junyoung.chapter09.part2

import java.util.ServiceLoader

interface Service

inline fun <reified T> loadService(): ServiceLoader<T> = ServiceLoader.load(T::class.java)

fun main() {
//    val service: ServiceLoader<Service> = ServiceLoader.load(Service::class.java)
    val service = loadService<Service>()
}