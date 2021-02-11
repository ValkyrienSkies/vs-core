package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.IPlayer
import kotlin.math.sign

/**
 * This task says that [playersNeedWatching] should be watching the chunk at [chunkPos].
 */
data class ChunkWatchTask(
    private val chunkPos: Long,
    val playersNeedWatching: Iterable<IPlayer>,
    val distanceToClosestPlayer: Double
) : Comparable<ChunkWatchTask> {
    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)

    override fun compareTo(other: ChunkWatchTask): Int {
        return (distanceToClosestPlayer - other.distanceToClosestPlayer).sign.toInt()
    }
}
