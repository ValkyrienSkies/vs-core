package org.valkyrienskies.core.collision

import kotlin.math.abs

/**
 * An immutable view of [CollisionRange].
 */
interface CollisionRangec {
    fun getMin(): Double
    fun getMax(): Double

    companion object {
        /**
         * @return The offset we must move [collisionRange1] such that it is no longer overlapping with [collisionRange2]. If [collisionRange1] and [collisionRange2] are not overlapping then this returns 0.
         */
        fun computeCollisionResponse(
            collisionRange1: CollisionRangec,
            collisionRange2: CollisionRangec
        ): Double {
            val pushLeft = -collisionRange1.getMax() + collisionRange2.getMin()
            val pushRight = -collisionRange1.getMin() + collisionRange2.getMax()

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
