package org.valkyrienskies.core.physics.bullet.compound

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold
import org.valkyrienskies.core.util.attached

fun btCollisionShape.getLocalInertia(mass: Float, inertia: Vector3): Vector3 {
	calculateLocalInertia(mass, inertia)
	return inertia
}

val btPersistentManifold.contactPoints
	get() =
		this.attached.computeIfAbsent {
			bulletArrayToList({ numContacts }, { getContactPoint(it) })
		}