package org.valkyrienskies.core.collision

import kotlin.math.abs

/**
 * An immutable view of [CollisionRange].
 */
interface CollisionRangec {
    val min: Double
    val max: Double

    companion object {
        /**
         * @return The offset we must move [collisionRange1] such that it is no longer overlapping with [collisionRange2]. If [collisionRange1] and [collisionRange2] are not overlapping then this returns 0.
         */
        fun computeCollisionResponse(
            collisionRange1: CollisionRangec,
            collisionRange2: CollisionRangec,
            collisionRange1Velocity: Double = 0.0
        ): Double {
            var pushLeft = -collisionRange1.max + collisionRange2.min
            var pushRight = -collisionRange1.min + collisionRange2.max

            if (collisionRange1Velocity > 0) {
                pushLeft -= collisionRange1Velocity
            } else {
                pushRight -= collisionRange1Velocity
            }

            return if (pushRight <= 0 || pushLeft >= 0) {
                // Not overlapping
                0.0
            } else if (abs(pushRight) < abs(pushLeft)) {
                // Its more efficient to push [collisionRange1] left
                pushRight
            } else {
                // Its more efficient to push [collisionRange1] right
                pushLeft
            }
        }
    }
}
