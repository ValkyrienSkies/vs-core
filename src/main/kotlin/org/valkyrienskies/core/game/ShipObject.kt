package org.valkyrienskies.core.game

import org.valkyrienskies.core.chunk_tracking.IShipChunkTracker
import org.valkyrienskies.core.chunk_tracking.ShipChunkTracker

/**
 * A [ShipObject] is essentially a [ShipData] that has been loaded.
 *
 * Its just is to interact with the player. This includes stuff like rendering, colliding with entities, and adding
 * a rigid body to the physics engine.
 */
class ShipObject(val shipData: ShipData) {
    internal val shipChunkTracker: IShipChunkTracker =
        ShipChunkTracker(shipData.shipActiveChunksSet, DEFAULT_CHUNK_WATCH_DISTANCE, DEFAULT_CHUNK_UNWATCH_DISTANCE)

    fun getRenderTransform() = shipData.shipTransform

    companion object {
        private const val DEFAULT_CHUNK_WATCH_DISTANCE = 128.0
        private const val DEFAULT_CHUNK_UNWATCH_DISTANCE = 192.0
    }
}