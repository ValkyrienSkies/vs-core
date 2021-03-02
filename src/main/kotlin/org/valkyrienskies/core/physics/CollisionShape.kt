package org.valkyrienskies.core.physics

import org.joml.Matrix4d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.valkyrienskies.core.util.multiplyTerms
import java.nio.ByteBuffer

sealed class CollisionShape(
	val transform: Matrix4d
)

class VoxelShape(
	/**
	 * Buffer representing the voxel shape (bit set to 1 is an occupied voxel).
	 * It goes 0 -> +x, then 0 -> +y, then 0 -> +z
	 *
	 * Do NOT mutate this
	 */
	val buffer: ByteBuffer,
	/**
	 * List of voxel indices to remove on next physics tick. Will be cleared on next physics tick.
	 *
	 * You may mutate this AND change reference (request update)
	 */
	var toRemove: IntArray,
	/**
	 * List of voxel indices to add on next physics tick. Will be cleared on next physics tick.
	 *
	 * You may mutate this AND change reference (request update)
	 */
	var toAdd: IntArray,
	/**
	 * Dimensions of the voxel shape
	 *
	 * You may mutate this (request update)
	 */
	val dimensions: Vector3ic,
	transform: Matrix4d
) : CollisionShape(transform) {
	init {
		require(buffer.isDirect && buffer.capacity() >= ((dimensions.multiplyTerms() + 7) / 8)) {
			"Buffer must be direct and capacity must fit volume / 8"
		}
	}
}

class CuboidShape(
	val halfExtents: Vector3dc,
	transform: Matrix4d
) : CollisionShape(transform)