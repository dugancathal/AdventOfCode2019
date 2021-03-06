@file:Suppress("NOTHING_TO_INLINE")

package commons

import kotlin.math.*

// y- is upward, y+ is downward, unlike traditional math, to accommodate 2D arrays
@Suppress("EqualsOrHashCode")
data class Vec2(val x: Int, val y: Int) {
    companion object {
        val ZERO = Vec2(0, 0)
        inline val ORIGIN get() = ZERO
        val READING_ORDER = Comparator(Vec2::compareReadingOrder)
    }

    // Manhattan distance
    fun manDist(other: Vec2) = abs(x - other.x) + abs(y - other.y)
    fun manDist() = abs(x) + abs(y)

    operator fun plus(dir: Dir2) = when(dir) {
        Dir2.Right -> Vec2(x+1, y)
        Dir2.Down -> Vec2(x, y+1)
        Dir2.Left -> Vec2(x-1, y)
        Dir2.Up -> Vec2(x, y-1)
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scale: Int) = Vec2(x * scale, y * scale)

    fun opposite() = Vec2(-x, -y)
    inline operator fun unaryMinus() = opposite()

    // cross product
    infix fun cross(b: Vec2) = x.toLong() * b.y - y.toLong() * b.x

    override fun hashCode(): Int = x.bitConcat(y).hash().toInt()

    fun compareReadingOrder(b: Vec2): Int {
        y.compareTo(b.y).let { if(it != 0) return it }
        return x.compareTo(b.x)
    }
}

enum class Dir2 { Right, Down, Left, Up;
    companion object {
        inline val East get() = Right
        inline val South get() = Down
        inline val West get() = Left
        inline val North get() = Up

        val values = values().asList()

        fun fromChar(char: Char) = when(char) {
            in "RrEe>" -> Right
            in "DdSsv" -> Down
            in "LlWw<" -> Left
            in "UuNn^" -> Up
            else -> error("Unrecognized direction: $char")
        }
    }

    fun right() = values[(ordinal + 1) % 4]
    inline operator fun inc() = right()

    fun left() = values[(ordinal + 3) % 4]
    inline operator fun dec() = left()

    fun opposite() = values[(ordinal + 2) % 4]
    inline operator fun unaryMinus() = opposite()
}

operator fun <V> Map<Vec2, V>.get(x: Int, y: Int) = get(Vec2(x, y))
operator fun <V> MutableMap<Vec2, V>.set(x: Int, y: Int, v: V) = set(Vec2(x, y), v)
operator fun <V: Any> NonNullMap<Vec2, V>.get(x: Int, y: Int) = get(Vec2(x, y))