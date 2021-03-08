package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * A convex polygon consists of points and normals.
 *
 * Note that we do not assign a particular normal for each point, because we do not need it for SAT collision.
 */
open class ConvexPolygon internal constructor(
    private val _points: List<Vector3dc>, private val _normals: List<Vector3dc>
) : ConvexPolygonc {
    override val points: Iterable<Vector3dc> get() = _points
    override val normals: Iterable<Vector3dc> get() = _normals

    companion object {
        fun createFromPointsAndNormals(points: List<Vector3dc>, normals: List<Vector3dc>): ConvexPolygon {
            return ConvexPolygon(points, normals)
        }
    }
}
