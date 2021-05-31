package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.bridge.IPlayer

/**
 * This task says that [playersNeedWatching] should be watching the chunk at [chunkPos].
 */
class ChunkWatchTask(
    private val chunkPos: Long,
    val playersNeedWatching: Iterable<IPlayer>,
    val distanceSqToClosestPlayer: Double,
    private val onExecute: () -> Unit
) : Comparable<ChunkWatchTask> {

    private var hasBeenExecuted = false

    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)

    /**
     * Call this after the chunk at [chunkPos] has been watched by [playersNeedWatching] to update the [ShipChunkTracker].
     */
    fun onExecuteChunkWatchTask() {
        require(!hasBeenExecuted) {
            "This ChunkWatchTask has already been executed!"
        }
        onExecute()
        hasBeenExecuted = true
    }

    override fun compareTo(other: ChunkWatchTask): Int {
        return distanceSqToClosestPlayer.compareTo(other.distanceSqToClosestPlayer)
    }
}
