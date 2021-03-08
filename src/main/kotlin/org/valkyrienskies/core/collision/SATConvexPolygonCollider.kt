package org.valkyrienskies.core.collision

import org.joml.Vector3dc
import kotlin.math.abs

/**
 * A basic implementation of [ConvexPolygonCollider] using the Separating Axis Theorem algorithm.
 */
object SATConvexPolygonCollider : ConvexPolygonCollider {
    override fun checkIfColliding(
        firstPolygon: ConvexPolygonc,
        secondPolygon: ConvexPolygonc,
        normals: Iterator<Vector3dc>,
        collisionResult: CollisionResult,
        temp1: CollisionRange,
        temp2: CollisionRange
    ) {
        var minCollisionDepth = Double.MAX_VALUE
        collisionResult._colliding = true // Initially assume that polygons are collided

        for (normal in normals) {
            // Calculate the overlapping range of the projection of both polygons along the [normal] axis
            val rangeOverlapResponse =
                computeCollisionResponseAlongNormal(firstPolygon, secondPolygon, normal, temp1, temp2)

            if (rangeOverlapResponse == 0.0) {
                // Polygons are separated along [normal], therefore they are NOT colliding
                collisionResult._colliding = false
                return
            } else {
                // Polygons are colliding along this axis, doesn't guarantee if the polygons are colliding or not
                val collisionDepth = abs(rangeOverlapResponse)
                if (collisionDepth < minCollisionDepth) {
                    minCollisionDepth = collisionDepth
                    collisionResult._collisionAxis.set(normal)
                    collisionResult._penetrationOffset = rangeOverlapResponse
                }
            }
        }
    }

    fun computeCollisionResponseAlongNormal(
        firstPolygon: ConvexPolygonc,
        secondPolygon: ConvexPolygonc,
        normal: Vector3dc,
        temp1: CollisionRange,
        temp2: CollisionRange
    ): Double {
        // Check if the polygons are separated along the [normal] axis
        val firstCollisionRange: CollisionRangec = firstPolygon.getProjectionAlongAxis(normal, temp1)
        val secondCollisionRange: CollisionRangec = secondPolygon.getProjectionAlongAxis(normal, temp2)

        return CollisionRangec.computeCollisionResponse(
            firstCollisionRange,
            secondCollisionRange
        )
    }
}
