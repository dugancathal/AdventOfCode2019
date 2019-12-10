package commons

import kotlin.math.*

/** Saturation-cast to 32-bit */
fun Long.coerceToInt() = coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

fun Boolean.toLong() = if(this) 1L else 0L

/** Concatenates [this] 32-bit integer to an[other] 32-bit integer bitwise, and returns the result as a 64-bit integer. **/
fun Int.bitConcat(other: Int) = toLong().shl(32) or other.toLong().and(0xffff_ffff)

fun ByteArray.toLong() = longFromBytes(
    this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7])

fun longFromBytes(
    b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte, b8: Byte
): Long {
    return (b1.toLong() and 0xFFL shl 56
            or (b2.toLong() and 0xFFL shl 48)
            or (b3.toLong() and 0xFFL shl 40)
            or (b4.toLong() and 0xFFL shl 32)
            or (b5.toLong() and 0xFFL shl 24)
            or (b6.toLong() and 0xFFL shl 16)
            or (b7.toLong() and 0xFFL shl 8)
            or (b8.toLong() and 0xFFL))
}

fun intFromBytes(
    b1: Byte, b2: Byte, b3: Byte, b4: Byte
): Int {
    return (b1.toInt() and 0xFF shl 24
            or (b2.toInt() and 0xFF shl 16)
            or (b3.toInt() and 0xFF shl 8)
            or (b4.toInt() and 0xFF))
}

tailrec fun gcd(a: Int, b: Int): Int = if(a == 0) abs(b) else gcd(b % a, a)