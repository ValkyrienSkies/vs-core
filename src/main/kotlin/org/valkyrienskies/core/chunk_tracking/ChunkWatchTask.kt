package org.valkyrienskies.core.chunk_tracking

import kotlin.math.sign

data class ChunkWatchTask<P>(
    private val chunkPos: Long,
    val playersNeedWatching: Iterable<P>,
    val distanceToClosestPlayer: Double
) : Comparable<ChunkWatchTask<P>> {
    fun getChunkX(): Int = IShipActiveChunksSet.longToChunkX(chunkPos)
    fun getChunkZ(): Int = IShipActiveChunksSet.longToChunkZ(chunkPos)

    override fun compareTo(other: ChunkWatchTask<P>): Int {
        return (distanceToClosestPlayer - other.distanceToClosestPlayer).sign.toInt()
    }
}
