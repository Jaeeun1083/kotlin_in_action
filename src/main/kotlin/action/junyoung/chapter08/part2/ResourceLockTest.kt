package action.junyoung.chapter08.part2

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock



fun main() {
    val l: Lock = ReentrantLock()

    l.withLock {
        // 락에 의해 보호되는 자원 사용
    }
}