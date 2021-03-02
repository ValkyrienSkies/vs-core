package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * A basic implementation of [ConvexPolygonCollider] using the Separating Axis Test algorithm.
 */
object SATConvexPolygonCollider : ConvexPolygonCollider {
    override fun checkIfColliding(
        firstPolygon: ConvexPolygon,
        secondPolygon: ConvexPolygon,
        normals: Iterator<Vector3dc>,
        collisionResult: CollisionResult,
        temp1: CollisionRange,
        temp2: CollisionRange,
        temp3: CollisionRange
    ) {
        var minCollisionDepth = Double.MAX_VALUE

        for (normal in normals) {
            val firstCollisionRange: CollisionRangec = firstPolygon.getProjectionAlongAxis(normal, temp1)
            val secondCollisionRange: CollisionRangec = secondPolygon.getProjectionAlongAxis(normal, temp2)

            val overlappingCollisionRange: CollisionRange = temp3
            val areRangesOverlapping = CollisionRangec.computeOverlap(
                firstCollisionRange,
                secondCollisionRange,
                overlappingCollisionRange
            )

            if (!areRangesOverlapping) {
                // Polygons are separated
                collisionResult.colliding = false
                return
            } else {
                // Polygons are colliding along this axis
                val collisionDepth = overlappingCollisionRange.getRangeLength()
                if (collisionDepth < minCollisionDepth) {
                    minCollisionDepth = collisionDepth
                    collisionResult.collisionAxis.set(normal)
                    collisionResult.minCollisionRange.min = overlappingCollisionRange.min
                    collisionResult.minCollisionRange.max = overlappingCollisionRange.max
                }
            }
        }
    }
}