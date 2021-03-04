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
        collisionResult.colliding = true // Initially assume that polygons are collided

        for (normal in normals) {
            // Calculate the overlapping range of the projection of both polygons along the [normal] axis
            val overlappingCollisionRange: CollisionRange = temp3
            val areRangesOverlapping = computeOverlapAlongNormal(firstPolygon, secondPolygon, normal, overlappingCollisionRange, temp1, temp2)

            if (!areRangesOverlapping) {
                // Polygons are separated along [normal], therefore they are NOT colliding
                collisionResult.colliding = false
                return
            } else {
                // Polygons are colliding along this axis, doesn't guarantee if the polygons are colliding or not
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

    @Suppress("MemberVisibilityCanBePrivate")
    fun computeOverlapAlongNormal(
        firstPolygon: ConvexPolygon,
        secondPolygon: ConvexPolygon,
        normal: Vector3dc,
        overlappingCollisionRangeOutput: CollisionRange,
        temp1: CollisionRange,
        temp2: CollisionRange
    ): Boolean {
        // Check if the polygons are separated along the [normal] axis
        val firstCollisionRange: CollisionRangec = firstPolygon.getProjectionAlongAxis(normal, temp1)
        val secondCollisionRange: CollisionRangec = secondPolygon.getProjectionAlongAxis(normal, temp2)

        // Calculate the overlapping range of the projection of both polygons along the [normal] axis
        val overlappingCollisionRange: CollisionRange = overlappingCollisionRangeOutput
        return CollisionRangec.computeOverlap(
            firstCollisionRange,
            secondCollisionRange,
            overlappingCollisionRange
        )
    }
}