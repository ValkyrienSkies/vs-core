package org.valkyrienskies.core.collision

enum class CollisionResultDirection(val multiplier: Double) {
    SAME_AS_AXIS(1.0), OPPOSITE_OF_AXIS(-1.0)
}
