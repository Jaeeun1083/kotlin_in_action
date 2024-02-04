package action.junyoung.chapter08.part1

private data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

private enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

private val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)

private fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }
    .map(SiteVisit::duration)
    .average()

private fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) = filter(predicate)
    .map(SiteVisit::duration)
    .average()

fun main() {
    val averageWindowsDuration = log.averageDurationFor {
        it.os == OS.IOS && it.path == "/signup"
    }
    val averageMacDuration = log.averageDurationFor {
        it.os == OS.MAC && it.path == "/"
    }
    // 출력 : 8.0
    println(averageWindowsDuration)
    // 출력 : 22.0
    println(averageMacDuration)
}