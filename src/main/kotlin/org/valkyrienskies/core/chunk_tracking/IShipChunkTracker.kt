package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.IPlayer
import org.valkyrienskies.core.game.ShipTransform

interface IShipChunkTracker<P : IPlayer> {
    fun tick(players: Iterator<P>, removedPlayers: Iterator<P>, shipTransform: ShipTransform)
    fun getPlayersWatchingChunk(chunkX: Int, chunkZ: Int): Iterator<P>
    fun getChunkWatchTasks(): Iterator<ChunkWatchTask<P>>
    fun getChunkUnwatchTasks(): Iterator<ChunkUnwatchTask<P>>
}