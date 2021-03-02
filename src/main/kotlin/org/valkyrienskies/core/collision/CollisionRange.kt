package org.valkyrienskies.core.collision

class CollisionRange(internal var min: Double, internal var max: Double) : CollisionRangec {
    override fun getMin() = min
    override fun getMax() = max
}