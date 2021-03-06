package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * An immutable view of [ConvexPolygon].
 */
interface ConvexPolygonc {
    fun getProjectionAlongAxis(normalAxis: Vector3dc, output: CollisionRange): CollisionRange
    fun getNormals(): Iterator<Vector3dc>
}
