package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * A [CollisionResult] is used to describe the collision between two polygons. See [CollisionResultc] for more information.
 */
data class CollisionResult internal constructor(
    var _colliding: Boolean,
    val _collisionAxis: Vector3d,
    var _penetrationOffset: Double
) :
    CollisionResultc {

    override val colliding: Boolean get() = _colliding
    override val collisionAxis: Vector3dc
        get() {
            if (!colliding) throw NotCollidingException("Cannot access collisionAxis because we are not colliding.")
            return _collisionAxis
        }
    override val penetrationOffset: Double
        get() {
            if (!colliding) throw NotCollidingException("Cannot access collisionRange because we are not colliding.")
            return _penetrationOffset
        }

    companion object {
        fun create(): CollisionResult {
            return CollisionResult(false, Vector3d(), 0.0)
        }
    }
}
