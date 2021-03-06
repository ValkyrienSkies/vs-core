package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * A [CollisionResult] is used to describe the collision between two polygons. See [CollisionResultc] for more information.
 */
data class CollisionResult(
    internal var colliding: Boolean,
    internal val collisionRange: CollisionRange,
    internal val collisionAxis: Vector3d
) :
    CollisionResultc {

    override fun getColliding() = colliding

    override fun getCollisionRange(): CollisionRangec {
        if (!colliding) throw NotCollidingException("Cannot access collision range because we are not colliding.")
        return collisionRange
    }

    override fun getCollisionAxis(): Vector3dc {
        if (!colliding) throw NotCollidingException("Cannot access collision axis because we are not colliding.")
        return collisionAxis
    }

    companion object {
        fun create(): CollisionResult {
            return CollisionResult(false, CollisionRange(0.0, 0.0), Vector3d())
        }
    }
}
