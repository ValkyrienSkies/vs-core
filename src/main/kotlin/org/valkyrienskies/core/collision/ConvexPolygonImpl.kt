package org.valkyrienskies.core.collision

import org.joml.Vector3dc
import java.lang.Double.max
import java.lang.Double.min

class ConvexPolygonImpl private constructor(private val points: List<Vector3dc>, private val normals: List<Vector3dc>) :
    ConvexPolygon {
    override fun getProjectionAlongAxis(normalAxis: Vector3dc, output: CollisionRange): CollisionRange {
        var minProjection = Double.MAX_VALUE
        var maxProjection = Double.MIN_VALUE

        for (point in points) {
            val projection = point.dot(normalAxis)
            minProjection = min(minProjection, projection)
            maxProjection = max(maxProjection, projection)
        }

        output.min = minProjection
        output.max = maxProjection

        return output
    }

    override fun getNormals(): Iterator<Vector3dc> {
        return normals.iterator()
    }
}