package org.valkyrienskies.core.physics

import org.joml.Vector3d
import org.joml.Vector3dc

// TODO: make 'RigidBodyShape' class for static objects

class RigidBody<T : CollisionShape>(
	val shape: T,
	/**
	 * If inertia data is null, then this rigid body
	 * is STATIC and does not move.
	 */
	inertiaData: InertiaData?
) {

	val inertiaData = inertiaData ?: InertiaData.STATIC

	/**
	 * You may mutate this (request update)
	 */
	val angularVelocity = Vector3d()

	/**
	 * You may mutate this (request update)
	 */
	val linearVelocity = Vector3d()

	/**
	 * Do NOT mutate
	 */
	internal val _totalTorque = Vector3d()
	val totalTorque: Vector3dc = _totalTorque

	/**
	 * Do NOT mutate
	 */
	internal val _totalForce = Vector3d()
	val totalForce: Vector3dc = _totalForce
}

