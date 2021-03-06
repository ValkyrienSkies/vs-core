package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * A [CollisionResult] is used to describe the collision between two polygons.
 */
data class CollisionResult constructor(
    internal var colliding: Boolean,
    internal val minCollisionRange: CollisionRange,
    internal val collisionAxis: Vector3d
) :
    CollisionResultc {

    override fun getColliding() = colliding

    override fun getMinCollisionRange(): CollisionRangec {
        if (!colliding) throw IllegalAccessException("Cannot access min collision range as we are not colliding.")
        return minCollisionRange
    }

    override fun getCollisionAxis(): Vector3dc {
        if (!colliding) throw IllegalAccessException("Cannot access collision axis as we are not colliding.")
        return collisionAxis
    }

    companion object {
        fun create(): CollisionResult {
            return CollisionResult(false, CollisionRange(0.0, 0.0), Vector3d())
        }
    }
}
