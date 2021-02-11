package org.valkyrienskies.core.chunk_tracking

import org.valkyrienskies.core.game.IPlayer

/**
 * This task says that the chunk at [chunkPos] should no longer be watched by [playersNeedUnwatching].
 */
data class ChunkUnwatchTask(private val chunkPos: Long, val playersNeedUnwatching: Iterable<IPlayer>) {
    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)
}
