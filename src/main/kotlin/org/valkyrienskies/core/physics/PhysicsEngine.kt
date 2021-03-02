package org.valkyrienskies.core.physics

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.util.VectorTemps

interface PhysicsEngine {
	companion object {
		val temps = VectorTemps()
		val tempList = mutableListOf<Vector3d>()
	}

	fun applyForce(body: RigidBody<*>, force: Vector3dc, position: Vector3dc)
	fun addCentralForce(body: RigidBody<*>, force: Vector3dc)

	/**
	 * Call this if you want the physics engine to
	 * check the parameters of the rigid body again next tick, like if you changed
	 * the inertia data. Note that the physics engine may update WITHOUT you ever
	 * having set this to true. This is just a hint.
	 */
	fun requestUpdate(body: RigidBody<*>)

	fun addRigidBody(body: RigidBody<*>)
	fun removeRigidBody(body: RigidBody<*>)

	fun getPenetrationAndNormal(
		shape1: CollisionShape,
		shape2: CollisionShape,
		t: PenetrationAndNormal
	): PenetrationAndNormal?

	fun getPenetrationAndNormal(
		shape1: RigidBody<*>,
		shape2: RigidBody<*>,
		t: PenetrationAndNormal
	): PenetrationAndNormal? {
		return getPenetrationAndNormal(shape1.shape, shape2.shape, t)
	}

	val rigidBodies: Set<RigidBody<*>>

	fun tick(deltaNs: Long)
	fun unload() {}
}

