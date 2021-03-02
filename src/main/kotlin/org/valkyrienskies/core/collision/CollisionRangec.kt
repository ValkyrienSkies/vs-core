package org.valkyrienskies.core.collision

import java.lang.Double.max
import java.lang.Double.min

/**
 * An immutable view of [CollisionRange].
 */
interface CollisionRangec {
    fun getMin(): Double
    fun getMax(): Double
    fun getRangeLength() = getMax() - getMin()

    companion object {
        /**
         * If [collisionRange1] and [collisionRange2] are overlapping, then this sets [output] to be that overlap and returns true.
         *
         * Otherwise this returns false.
         */
        fun computeOverlap(
            collisionRange1: CollisionRangec,
            collisionRange2: CollisionRangec,
            output: CollisionRange
        ): Boolean {
            val maxOfMins = max(collisionRange1.getMin(), collisionRange2.getMin())
            val minOfMaxs = min(collisionRange1.getMax(), collisionRange2.getMax())
            return if (maxOfMins <= minOfMaxs) {
                output.min = maxOfMins
                output.max = minOfMaxs
                true
            } else {
                false
            }
        }
    }
}