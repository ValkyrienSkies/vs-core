package org.valkyrienskies.core.collision

/**
 * A 1-D range used in collision code.
 */
data class CollisionRange(internal var min: Double, internal var max: Double) : CollisionRangec {
    override fun getMin() = min
    override fun getMax() = max

    companion object {
        fun create(min: Double, max: Double): CollisionRange {
            return CollisionRange(min, max)
        }

        fun create(): CollisionRange {
            return create(0.0, 0.0)
        }
    }
}
