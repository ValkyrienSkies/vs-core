package org.valkyrienskies.core.util

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.joml.Matrix3d
import org.joml.Matrix4d
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3i

/**
 * Class the lazily initializes a bunch of vector types,
 * so that you don't have to constantly create objects
 */
class VectorTemps {

	val v3GDX = LazyList { Vector3() }
	val v3d = LazyList { Vector3d() }
	val v3i = LazyList { Vector3i() }
	val m3d = LazyList { Matrix3d() }
	val m4f = LazyList { Matrix4f() }
	val m4d = LazyList { Matrix4d() }
	val m4GDX = LazyList { Matrix4() }
}

class LazyList<T>(private inline val supplier: () -> T) {

	@Suppress("UNCHECKED_CAST")
	private var backing = arrayOfNulls<Any>(2) as Array<T?>

	operator fun get(index: Int): T {
		require(index < 1_000) { "Why are you creating 1000 temp objects..?" }
		expandToFit(index)

		return backing[index] ?: supplier().also { backing[index] = it }
	}

	private fun expandToFit(indexToFit: Int) {
		var newSize: Int
		do
			newSize = backing.size * 2
		while (newSize <= indexToFit)

		@Suppress("UNCHECKED_CAST")
		val newBacking = arrayOfNulls<Any>(newSize) as Array<T?>
		System.arraycopy(backing, 0, newBacking, 0, backing.size)
		backing = newBacking
	}
}