package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.collision.CollisionResultDirection.SAME_AS_AXIS

/**
 * A [CollisionResult] is used to describe the collision between two polygons. See [CollisionResultc] for more information.
 */
data class CollisionResult(
    var _colliding: Boolean,
    val _collisionRange: CollisionRange,
    val _collisionAxis: Vector3d,
    val _collisionResultDirection: CollisionResultDirection
) :
    CollisionResultc {

    override val colliding: Boolean get() = _colliding
    override val collisionAxis: Vector3dc
        get() {
            if (!colliding) throw NotCollidingException("Cannot access collisionAxis because we are not colliding.")
            return _collisionAxis
        }
    override val collisionRange: CollisionRangec
        get() {
            if (!colliding) throw NotCollidingException("Cannot access collisionRange because we are not colliding.")
            return _collisionRange
        }
    override val collisionResultDirection: CollisionResultDirection
        get() {
            if (!colliding) throw NotCollidingException(
                "Cannot access collisionResultDirection because we are not colliding."
            )
            return _collisionResultDirection
        }

    companion object {
        fun create(): CollisionResult {
            return CollisionResult(false, CollisionRange.create(), Vector3d(), SAME_AS_AXIS)
        }
    }
}
