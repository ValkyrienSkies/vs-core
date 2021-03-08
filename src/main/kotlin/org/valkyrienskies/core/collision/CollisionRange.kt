package org.valkyrienskies.core.collision

/**
 * A 1-D range used in collision code.
 */
data class CollisionRange internal constructor(var _min: Double, var _max: Double) : CollisionRangec {
    override val min: Double get() = _min
    override val max: Double get() = _max

    companion object {
        fun create(min: Double, max: Double): CollisionRange {
            return CollisionRange(min, max)
        }

        fun create(): CollisionRange {
            return create(0.0, 0.0)
        }
    }
}
