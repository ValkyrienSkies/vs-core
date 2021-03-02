package org.valkyrienskies.core.physics

import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * All fields of inertia data can be mutated,
 * but request update on the rigid body which owns it.
 */
data class InertiaData(
	var mass: Double,
	/**
	 * The center of mass of the rigid body. This is a coordinate
	 * IN the world, NOT relative to the rigid body.
	 */
	val centerOfMass: Vector3dc,
	val inertia: Matrix3dc
) {
	companion object {
		val STATIC = InertiaData(0.0, Vector3d(), Matrix3d())
	}
}