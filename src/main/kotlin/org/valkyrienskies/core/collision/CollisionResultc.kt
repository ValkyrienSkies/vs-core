package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * Interface to view the result of a collision test on two [ConvexPolygonc].
 */
interface CollisionResultc {
    /**
     * @return whether the polygons are colliding or not
     */
    fun getColliding(): Boolean

    /**
     * This method will throw a [NotCollidingException] when [getColliding] returns false.
     *
     * @return the normal with the smallest overlap
     */
    fun getCollisionAxis(): Vector3dc

    /**
     * This method will throw a [NotCollidingException] when [getColliding] returns false.
     *
     * @return the range of overlap along the normal with the smallest overlap
     */
    fun getCollisionRange(): CollisionRangec
}
