package org.valkyrienskies.core.collision

import org.joml.Vector3dc
import org.joml.primitives.AABBd
import kotlin.math.max
import kotlin.math.min

/**
 * An immutable view of [ConvexPolygon].
 */
interface ConvexPolygonc {
    val points: Iterable<Vector3dc>
    val normals: Iterable<Vector3dc>

    fun getProjectionAlongAxis(normalAxis: Vector3dc, output: CollisionRange): CollisionRange {
        var minProjection = Double.MAX_VALUE
        var maxProjection = Double.MIN_VALUE

        for (point in points) {
            val projection = point.dot(normalAxis)
            minProjection = java.lang.Double.min(minProjection, projection)
            maxProjection = java.lang.Double.max(maxProjection, projection)
        }

        output._min = minProjection
        output._max = maxProjection

        return output
    }

    fun getEnclosingAABB(output: AABBd): AABBd {
        output.minX = Double.POSITIVE_INFINITY
        output.minY = Double.POSITIVE_INFINITY
        output.minZ = Double.POSITIVE_INFINITY
        output.maxX = Double.NEGATIVE_INFINITY
        output.maxY = Double.NEGATIVE_INFINITY
        output.maxZ = Double.NEGATIVE_INFINITY

        for (point in points) {
            output.minX = min(output.minX, point.x())
            output.minY = min(output.minY, point.y())
            output.minZ = min(output.minZ, point.z())
            output.maxX = max(output.maxX, point.x())
            output.maxY = max(output.maxY, point.y())
            output.maxZ = max(output.maxZ, point.z())
        }
        return output
    }
}
