package org.valkyrienskies.core.util

import io.netty.buffer.ByteBuf
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import java.nio.ByteBuffer

fun ByteBuf.readVec3fAsDouble(): Vector3d {
    return Vector3d(readFloat().toDouble(), readFloat().toDouble(), readFloat().toDouble())
}

fun ByteBuf.writeVec3AsFloat(v: Vector3dc) {
    writeFloat(v.x().toFloat())
    writeFloat(v.y().toFloat())
    writeFloat(v.z().toFloat())
}

fun ByteBuf.readQuatfAsDouble(): Quaterniond {
    return Quaterniond(readFloat().toDouble(), readFloat().toDouble(), readFloat().toDouble(), readFloat().toDouble())
}

fun ByteBuf.writeQuatAsFloat(q: Quaterniondc) {
    writeFloat(q.x().toFloat())
    writeFloat(q.y().toFloat())
    writeFloat(q.z().toFloat())
    writeFloat(q.w().toFloat())
}

/**
 * Returns an array containing the results of applying the
 * given [transform] function to each element in the original array.
 */
inline fun <T, reified R> Array<T>.mapToArray(transform: (T) -> R): Array<R> =
    Array(size) { index -> transform(this[index]) }

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Array<T?>.filterNotNullToArray(): Array<T> =
    this.filterToArray { it != null } as Array<T>

/**
 * Note: This is NOT threadsafe, do not use this on an array that might be modified from another thread
 */
inline fun <reified T> Array<T>.filterToArray(predicate: (T) -> Boolean): Array<T> {
    val newArray = arrayOfNulls<T>(this.count(predicate))
    var count = 0

    try {
        for (item in this)
            if (predicate(item))
                newArray[count++] = item
    } catch (ex: IndexOutOfBoundsException) {
        throw ConcurrentModificationException("Array was modified while filtering", ex)
    }

    if (count != newArray.size)
        throw ConcurrentModificationException("Array was modified while filtering")

    // Unchecked cast is valid because because if the whole array were not filled we would have thrown already
    @Suppress("UNCHECKED_CAST")
    return newArray as Array<T>
}

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
