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
        firstPolygonVelocity: Vector3dc,
        normals: Iterator<Vector3dc>,
        collisionResult: CollisionResult,
        temp1: CollisionRange,
        temp2: CollisionRange,
        forcedResponseNormal: Vector3dc?
    ) {
        var minCollisionDepth = Double.MAX_VALUE
        collisionResult._colliding = true // Initially assume that polygons are collided

        for (normal in normals) {
            // Calculate the overlapping range of the projection of both polygons along the [normal] axis
            val rangeOverlapResponse =
                computeCollisionResponseAlongNormal(
                    firstPolygon, secondPolygon, firstPolygonVelocity, normal, temp1, temp2
                )

            if (abs(rangeOverlapResponse) < 1.0e-6) {
                // Polygons are separated along [normal], therefore they are NOT colliding
                collisionResult._colliding = false
                return
            } else {
                if (forcedResponseNormal != null) {
                    val dotProduct = forcedResponseNormal.dot(normal)
                    if (abs(dotProduct) < 1e-6) continue // Skip
                    val modifiedRangeOverlapResponse = rangeOverlapResponse / dotProduct

                    // Polygons are colliding along this axis, doesn't guarantee if the polygons are colliding or not
                    val collisionDepth = abs(modifiedRangeOverlapResponse)
                    if (collisionDepth < minCollisionDepth) {
                        minCollisionDepth = collisionDepth
                        collisionResult._collisionAxis.set(forcedResponseNormal)
                        collisionResult._penetrationOffset = modifiedRangeOverlapResponse
                    }
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

        if (minCollisionDepth == Double.MAX_VALUE) collisionResult._colliding = false
    }

    fun computeCollisionResponseAlongNormal(
        firstPolygon: ConvexPolygonc,
        secondPolygon: ConvexPolygonc,
        firstPolygonVelocity: Vector3dc,
        normal: Vector3dc,
        temp1: CollisionRange,
        temp2: CollisionRange
    ): Double {
        // Check if the polygons are separated along the [normal] axis
        val firstCollisionRange: CollisionRangec = firstPolygon.getProjectionAlongAxis(normal, temp1)
        val secondCollisionRange: CollisionRangec = secondPolygon.getProjectionAlongAxis(normal, temp2)
        val firstRangeVelocityAlongNormal = firstPolygonVelocity.dot(normal)

        return CollisionRangec.computeCollisionResponse(
            firstCollisionRange,
            secondCollisionRange,
            firstRangeVelocityAlongNormal
        )
    }
}
