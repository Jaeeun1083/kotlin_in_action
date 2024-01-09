package action.junyoung.chapter04.part4

import action.junyoung.chapter04.part2.getFacebookName

class User {
    val nickname: String

    constructor(email: String) {
        nickname = email.substringBefore('@')
    }

    constructor(facebookAccountId: Int) {
        nickname = getFacebookName(facebookAccountId)
    }

}

class User2 private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String)= User(email.substringBefore('@'))
        fun newFacebookUser(accountId: Int) = User(getFacebookName(accountId))
    }
}