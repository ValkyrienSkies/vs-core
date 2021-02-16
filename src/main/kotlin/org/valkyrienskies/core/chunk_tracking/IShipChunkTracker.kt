package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.IPlayer
import org.valkyrienskies.core.game.ShipTransform

/**
 * The [IShipChunkTracker] keeps track of the players that are watching each ship chunk.
 *
 * It also determines when players should watch/unwatch ship chunks (see [getChunkWatchTasks] and [getChunkUnwatchTasks]).
 */
interface IShipChunkTracker {
    fun tick(players: Iterable<IPlayer>, removedPlayers: Iterable<IPlayer>, shipTransform: ShipTransform)
    fun getPlayersWatchingChunk(chunkX: Int, chunkZ: Int): Iterator<IPlayer>
    fun getChunkWatchTasks(): Iterator<ChunkWatchTask>
    fun getChunkUnwatchTasks(): Iterator<ChunkUnwatchTask>
}