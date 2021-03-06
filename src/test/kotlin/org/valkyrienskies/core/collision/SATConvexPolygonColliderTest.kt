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
        val firstPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(0.0, 0.5, 0.0, 1.0, 1.0, 1.0))
        val normals: List<Vector3dc> = listOf(Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        val collisionResult: CollisionResult = CollisionResult.create()
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()
        val temp3: CollisionRange = CollisionRange.create()

        SATConvexPolygonCollider.checkIfColliding(
            firstPolygon, secondPolygon, normals.iterator(), collisionResult, temp1, temp2, temp3
        )

        assertTrue(collisionResult.colliding)
        assertEquals(CollisionResult(true, CollisionRange(0.5, 1.0), Vector3d(0.0, 1.0, 0.0)), collisionResult)
    }

    /**
     * Test two polygons that are not colliding
     */
    @Test
    fun checkIfColliding2() {
        val firstPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(2.0, 0.0, 0.0, 3.0, 1.0, 1.0))
        val normals: List<Vector3dc> = listOf(Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        val collisionResult: CollisionResult = CollisionResult.create()
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()
        val temp3: CollisionRange = CollisionRange.create()

        SATConvexPolygonCollider.checkIfColliding(
            firstPolygon, secondPolygon, normals.iterator(), collisionResult, temp1, temp2, temp3
        )

        assertFalse(collisionResult.colliding)
    }

    /**
     * Test the projection of a polygon along a normal axis
     */
    @Test
    fun computeOverlapAlongNormal() {
        val firstPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        val secondPolygon: ConvexPolygonc = ConvexPolygon.createFromAABB(AABBd(0.0, 0.5, 0.0, 1.0, 1.0, 1.0))
        val normal: Vector3dc = Vector3d(0.0, 1.0, 0.0)
        val collisionRangeOutput: CollisionRange = CollisionRange.create()
        val temp1: CollisionRange = CollisionRange.create()
        val temp2: CollisionRange = CollisionRange.create()

        val collidingAlongNormal = SATConvexPolygonCollider.computeOverlapAlongNormal(
            firstPolygon, secondPolygon, normal, collisionRangeOutput, temp1, temp2
        )

        assertTrue(collidingAlongNormal)
        assertEquals(CollisionRange.create(0.5, 1.0), collisionRangeOutput)
    }
}
