package org.valkyrienskies.core.collision

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SATConvexPolygonColliderTest {

    /**
     * Test two polygons that are colliding
     */
    @Test
    fun checkIfColliding() {
        // The optimal collision response is to move firstPolygon by (0, -0.1, 0)
        val firstPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(0.0, 0.9, 0.0, 1.0, 1.0, 1.0))
        val normals: List<Vector3dc> = listOf(Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        val collisionResult: CollisionResult = CollisionResult.create()
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()

        SATConvexPolygonCollider.checkIfColliding(
            firstPolygon, secondPolygon, normals.iterator(), collisionResult, temp1, temp2
        )

        assertTrue(collisionResult.colliding)
        assertEquals(Vector3d(0.0, 1.0, 0.0), collisionResult._collisionAxis)
        assertEquals(-0.1, collisionResult.penetrationOffset, EPSILON)
    }

    /**
     * Test two polygons that are not colliding
     */
    @Test
    fun checkIfColliding2() {
        val firstPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(2.0, 0.0, 0.0, 3.0, 1.0, 1.0))
        val normals: List<Vector3dc> = listOf(Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        val collisionResult: CollisionResult = CollisionResult.create()
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()

        SATConvexPolygonCollider.checkIfColliding(
            firstPolygon, secondPolygon, normals.iterator(), collisionResult, temp1, temp2
        )

        assertFalse(collisionResult.colliding)
    }

    /**
     * Test the projection of a polygon along a normal axis
     */
    @Test
    fun computeOverlapAlongNormal() {
        // The optimal collision response is to move firstPolygon by (0, 0.2, 0)
        val firstPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(0.0, 0.8, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc =
            TransformedCuboidPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val normal: Vector3dc = Vector3d(0.0, 1.0, 0.0)
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()

        val collisionResponse = SATConvexPolygonCollider.computeCollisionResponseAlongNormal(
            firstPolygon, secondPolygon, normal, temp1, temp2
        )

        assertEquals(0.2, collisionResponse, EPSILON)
    }

    companion object {
        // The epsilon we use to check if two floating point numbers are equal, to account for floating point error
        private const val EPSILON = 1e-8
    }
}
