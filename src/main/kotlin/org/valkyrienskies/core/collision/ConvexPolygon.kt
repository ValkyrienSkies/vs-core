package org.valkyrienskies.core.collision

import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

/**
 * A convex polygon consists of points and normals.
 *
 * Note that we do not assign a particular normal for each point, because we do not need it for SAT collision.
 */
class ConvexPolygon private constructor(private val _points: List<Vector3d>, private val _normals: List<Vector3d>) :
    ConvexPolygonc {
    override val points: Iterable<Vector3dc> get() = _points
    override val normals: Iterable<Vector3dc> get() = _normals

    fun setFromAABB(aabb: AABBdc, transform: Matrix4dc? = null): ConvexPolygon {
        _points[0].set(aabb.minX(), aabb.minY(), aabb.minZ())
        _points[1].set(aabb.minX(), aabb.minY(), aabb.maxZ())
        _points[2].set(aabb.minX(), aabb.maxY(), aabb.minZ())
        _points[3].set(aabb.minX(), aabb.maxY(), aabb.maxZ())
        _points[4].set(aabb.maxX(), aabb.minY(), aabb.minZ())
        _points[5].set(aabb.maxX(), aabb.minY(), aabb.maxZ())
        _points[6].set(aabb.maxX(), aabb.maxY(), aabb.minZ())
        _points[7].set(aabb.maxX(), aabb.maxY(), aabb.maxZ())

        _normals[0].set(1.0, 0.0, 0.0)
        _normals[1].set(0.0, 1.0, 0.0)
        _normals[2].set(0.0, 0.0, 1.0)

        if (transform != null) {
            for (point in _points) transform.transformPosition(point)
            for (normal in _normals) transform.transformDirection(normal)
        }
        return this
    }

    companion object {
        private val UNIT_CUBE: AABBdc = AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        fun createFromPointsAndNormals(points: List<Vector3d>, normals: List<Vector3d>): ConvexPolygon {
            return ConvexPolygon(points, normals)
        }

        /**
         * Creates an empty polygon with 8 points and 3 vertices
         */
        private fun createEmptyConvexPolygon(): ConvexPolygon {
            val points: MutableList<Vector3d> = ArrayList()
            val normals: MutableList<Vector3d> = ArrayList()

            for (i in 1..8) points.add(Vector3d())
            for (i in 1..3) normals.add(Vector3d())

            return ConvexPolygon(points, normals)
        }

        fun createFromAABB(aabb: AABBdc, transform: Matrix4dc? = null): ConvexPolygon {
            return createEmptyConvexPolygon().setFromAABB(aabb, transform)
        }

        fun createUnitCube(): ConvexPolygon {
            return createFromAABB(UNIT_CUBE)
        }
    }
}
