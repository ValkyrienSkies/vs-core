package org.valkyrienskies.core.collision

import org.joml.Vector3dc

interface ConvexPolygon {
    fun getProjectionAlongAxis(normalAxis: Vector3dc, output: CollisionRange): CollisionRange
    fun getNormals(): Iterator<Vector3dc>
}