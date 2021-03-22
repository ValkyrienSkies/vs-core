package org.valkyrienskies.core.util

import org.joml.Vector3i
import org.joml.Vector3ic
import java.nio.ByteBuffer

fun asLong(x: Int, y: Int): Long = x.toLong() or (y.toLong() shl 32)
fun asInts(x: Long): Pair<Int, Int> = Pair(x.toInt(), (x shr 32).toInt())

fun Short.toIntUnsigned() = this.toInt() and 0xffff

inline fun Byte.iterateBits(func: (Boolean, Int) -> Unit) {
    for (i in 8 downTo 0) {
        val masked = (this.toInt() and (1 shl i))
        func(masked != 0, i)
    }
}

inline fun Int.iterateBits(func: (Boolean, Int) -> Unit) {
    for (i in 32 downTo 0) {
        val masked = this and (1 shl i)
        func(masked != 0, i)
    }
}

inline fun Long.iterateBits(func: (Boolean, Int) -> Unit) {
    for (i in 64 downTo 0) {
        val masked = this and (1L shl i)
        func(masked != 0L, i)
    }
}

/**
 * For example ByteBuffer is 01110 then this is called with
 * (false, 0), (true, 1), (true, 2), (true, 3), (false, 4)
 */
inline fun ByteBuffer.iterateBits(func: (Boolean, Int) -> Unit) {
    for (i in 0..this.capacity()) {
        val byte = this.get(i)
        byte.iterateBits { bit, j -> func(bit, i * 8 + j) }
    }
}

/**
 * Take (x, y, z) and produce index (i)
 */
fun unwrapIndex(index: Int, dimensions: Vector3ic, v: Vector3i): Vector3i {
    val z = index / (dimensions.x * dimensions.y)
    val y = (index - (z * dimensions.x * dimensions.y)) / dimensions.x
    val x = (index - (z * dimensions.x * dimensions.y)) % dimensions.x

    return v.set(x, y, z)
}

fun wrapIndex(x: Int, y: Int, z: Int, dimensions: Vector3ic): Int =
    x + (y * dimensions.x) + (z * dimensions.x * dimensions.y)

fun wrapIndex(point: Vector3ic, dimensions: Vector3ic): Int =
    wrapIndex(point.x, point.y, point.z, dimensions)

inline fun timeNanos(func: () -> Unit): Long {
    val start = System.nanoTime()
    func()
    return System.nanoTime() - start
}

inline fun timeMillis(func: () -> Unit): Long {
    val start = System.currentTimeMillis()
    func()
    return System.currentTimeMillis() - start
}

inline fun tryAndPrint(func: () -> Unit) {
    try {
        func()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
