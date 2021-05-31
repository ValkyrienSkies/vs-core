package org.valkyrienskies.core.game.ships

import org.valkyrienskies.core.chunk_tracking.IShipChunkTracker
import org.valkyrienskies.core.chunk_tracking.ShipChunkTracker
import org.valkyrienskies.core.game.bridge.IPlayer
import org.valkyrienskies.core.networking.delta.DeltaEncodedChannelServerTCP
import org.valkyrienskies.core.networking.delta.ShipDataGeneralDeltaAlgorithm

class ShipObjectServer(
    override val shipData: ShipData
) : ShipObject(shipData) {

    val id get() = shipData.id

    internal val shipDataChannel = DeltaEncodedChannelServerTCP(ShipDataGeneralDeltaAlgorithm, shipData)

    /**
     * Players tracking this ship
     */
    internal val playersTracking = HashSet<IPlayer>()

    internal val shipChunkTracker: IShipChunkTracker =
        ShipChunkTracker(shipData.shipActiveChunksSet, DEFAULT_CHUNK_WATCH_DISTANCE, DEFAULT_CHUNK_UNWATCH_DISTANCE)

    companion object {
        private const val DEFAULT_CHUNK_WATCH_DISTANCE = 128.0
        private const val DEFAULT_CHUNK_UNWATCH_DISTANCE = 192.0
    }
}
