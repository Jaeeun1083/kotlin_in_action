package action.junyoung.chapter06.part1

fun sendEmailTo(email: String) { /*...*/ }
fun main() {
    val email: String? = "전청조"
//    if(email != null) sendEmailTo(email)
    email?.let { email -> sendEmailTo(email) }
}