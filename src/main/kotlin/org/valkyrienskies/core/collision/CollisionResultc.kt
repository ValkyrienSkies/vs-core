package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * Interface to view the result of a collision test on two [ConvexPolygonc].
 */
interface CollisionResultc {
    /**
     * @return whether the polygons are colliding or not
     */
    val colliding: Boolean

    /**
     * Accessing this will throw a [NotCollidingException] when [colliding] is false.
     *
     * @return the normal with the smallest overlap
     */
    val collisionAxis: Vector3dc

    /**
     * Accessing this will throw a [NotCollidingException] when [colliding] is false.
     *
     * @return the penetration offset that moves the first polygon out of the second polygon
     */
    val penetrationOffset: Double

    fun getCollisionResponse(dest: Vector3d): Vector3d {
        return collisionAxis.mul(penetrationOffset, dest)
    }
}
