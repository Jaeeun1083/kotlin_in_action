package action.junyoung.test

import kotlin.reflect.full.memberProperties

enum class PostCategory(val title: String) {
    C1("자유 게시판"), C2("정보 게시판");

    override fun toString(): String {
        return title
    }
}

data class Member(
    val username: String,
    val password: String
)

class Post(
    val title: String = "",
    val content: String = "",
    val isDeleted: Boolean = false,
    val category: PostCategory = PostCategory.C1,
    val writer: Member
) : Detectable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (title != other.title) return false
        if (content != other.content) return false
        if (isDeleted != other.isDeleted) return false
        return category == other.category
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + isDeleted.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }

}

interface Detectable {
    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
}

data class Item(
    val order: Int,
    val propertyName: String,
    val label: String,
    val v1: String = "",
    val v2: String = ""
)

/**
 * 두 객체를 비교해 변경된 부분을 찾는 함수
 * @param origin 기존 객체
 * @param target 비교 대상 객체
 * @param itemList 변경을 감지할 아이템 리스트
 * */
inline fun <reified T : Detectable> findChanges(
    origin: T,
    target: T,
    itemList: List<Item>,
): List<Item> {
    val clazz = T::class

    val result = mutableListOf<Item>()

    for (property in clazz.memberProperties) {
        val propertyName = property.name

        itemList.find { it.propertyName == propertyName }?.let {
            val originValue = property.get(origin).toString()
            val targetValue = property.get(target).toString()

            if (originValue != targetValue) {
                val v1 = it.v1.ifEmpty { originValue }
                val v2 = it.v2.ifEmpty { targetValue }

                result += Item(it.order, it.propertyName, it.label, v1, v2)
            }
        }
    }
    return result.sortedBy { it.order }
}

val origin = Post("제목1","내용1", writer = Member("admin", "1234"))
val changed = Post("제목1", "내용1", true, PostCategory.C2, writer = Member("tester", "1234"))

fun main() {
    val items = listOf(
        Item(1, "title", "제목"),
        Item(2, "writer", "작성자", origin.writer.username, changed.writer.username),
        Item(3, "category", "카테고리"),
        Item(4, "isDeleted", "삭제여부", "삭제됨", "삭제되지 않음")
    )

    val changeList = findChanges(origin, changed, items)
    println(changeList)
}
