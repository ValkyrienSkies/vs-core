package org.valkyrienskies.core.collision

import org.joml.Vector3dc

/**
 * A [ConvexPolygonCollider] computes whether two polygons are colliding or not, and returns the result.
 */
interface ConvexPolygonCollider {
    /**
     * The polygons tested for collision are [firstPolygon] and [secondPolygon], the normals they are tested with are [normals].
     *
     * The result of this test is stored in the output parameter [collisionResult].
     *
     * The temp* parameters are objects used during the computation to avoid creating new objects. Their values do not matter, expect their values to be replaced with garbage.
     */
    fun checkIfColliding(
        firstPolygon: ConvexPolygonc,
        secondPolygon: ConvexPolygonc,
        firstPolygonVelocity: Vector3dc,
        normals: Iterator<Vector3dc>,
        collisionResult: CollisionResult,
        temp1: CollisionRange,
        temp2: CollisionRange,
        forcedResponseNormal: Vector3dc? = null
    )
}
