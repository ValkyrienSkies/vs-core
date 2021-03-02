package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * An immutable view [CollisionResult].
 */
interface CollisionResultc {
    fun getColliding(): Boolean
    fun getMinCollisionRange(): CollisionRangec
    fun getCollisionAxis(): Vector3dc
}