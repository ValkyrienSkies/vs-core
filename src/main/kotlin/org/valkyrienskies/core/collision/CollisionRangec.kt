package org.valkyrienskies.core.collision

import java.lang.Double.max
import java.lang.Double.min

interface CollisionRangec {
    fun getMin(): Double
    fun getMax(): Double
    fun getRangeLength() = getMax() - getMin()

    companion object {
        fun computeOverlap(
            collisionRange1: CollisionRangec,
            collisionRange2: CollisionRangec,
            output: CollisionRange,
            isOverlapping: CollisionRangeOverlapping
        ) {
            val maxOfMins = max(collisionRange1.getMin(), collisionRange2.getMin())
            val minOfMaxs = min(collisionRange1.getMax(), collisionRange2.getMax())
            if (maxOfMins <= minOfMaxs) {
                isOverlapping.overlapping = true
                output.min = maxOfMins
                output.max = minOfMaxs
            } else {
                isOverlapping.overlapping = false
            }
        }
    }
}