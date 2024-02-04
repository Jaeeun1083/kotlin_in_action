package action.junyoung.chapter07

data class Rectangle(
    val upperLeft: Point,
    val lowerRight: Point
)

data class Point(var x: Int, var y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun plusAssign(other: Point) {
        x += other.x
        y += other.y
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Point) return false
        return other.x == x && other.y == y
    }
}