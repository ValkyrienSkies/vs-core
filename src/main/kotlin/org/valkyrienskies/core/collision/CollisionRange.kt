package org.valkyrienskies.core.collision

/**
 * A 1-D range used in collision code.
 */
class CollisionRange(internal var min: Double, internal var max: Double) : CollisionRangec {
    override fun getMin() = min
    override fun getMax() = max
}